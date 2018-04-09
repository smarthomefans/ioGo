package com.example.nagel.io1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    private Switch wSwitch;
    private TextView wTextView;
    private Boolean isConnected = true;
    private Map<String,String> states;
    private Map<String,String> objects;
    private Map<String,List<String>> oRoles;
    private ArrayAdapter <String> aktienlisteAdapter;
    int clickCounter=0;
    private String [] aktienlisteArray = {
            "Adidas - Kurs: 73,45 €",
            "Allianz - Kurs: 145,12 €",
            "BASF - Kurs: 84,27 €",
            "Bayer - Kurs: 128,60 €",
            "Beiersdorf - Kurs: 80,55 €"
    };
    List <String> aktienListe = new ArrayList<>(Arrays.asList(aktienlisteArray));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        wTextView = findViewById(R.id.textState);
        states = new HashMap<>();
        objects = new HashMap<>();
        oRoles = new HashMap<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            mSocket = IO.socket("http://192.168.1.33:8084/");
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("stateChange", onStateChange);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.connect();

        //ArrayList<String> arrayList = new ArrayList<>();
        //arrayList = (ArrayList) oRoles.get("value.temperature");
        aktienlisteAdapter =
                new ArrayAdapter<>(
                        this, // Die aktuelle Umgebung (diese Activity)
                        R.layout.list_item, // ID der XML-Layout Datei
                        R.id.list_item_textview, // ID des TextViews
                        aktienListe); // Beispieldaten in einer ArrayList
        ListView aktienlisteListView = (ListView) findViewById(R.id.listview);
        aktienlisteListView.setAdapter(aktienlisteAdapter);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("subscribe", "javascript.0.*");
            if (!isConnected) {
                wTextView.setText("connected");
                isConnected = true;
            }
            getStates();
            getObjects();

        }
    };

    private Emitter.Listener onStateChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onStateChange", args[0].toString() + " => " + args[1].toString());
                    wTextView.setText(args[0].toString());
                    JSONObject data = (JSONObject) args[1];
                    states.put(args[0].toString(),args[1].toString());
                    Log.d("onStateChange", "states.size => " + states.size());
                    if(args[0].equals("javascript.0.vi_switch")){
                        try{
                            wSwitch.setChecked(data.getBoolean("val"));
                        } catch (JSONException e) {
                            Log.e("onStateChange", e.getMessage());
                        }
                    }

                }
            });
        }
    };

    private void getStates(){
        mSocket.emit("getStates", "javascript.0.*",new Ack() {
            @Override
            public void call(Object... args) {
                Log.i("onConnect","receiving states");
                JSONObject data = (JSONObject) args[1];
                Iterator<String> iter = data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        states.put(key, data.get(key).toString());
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
                Log.i("getStates",states.size() + " states received");
            }
        });
    }

    private void getObjects(){
        mSocket.emit("getObjects", null, new Ack() {
            @Override
            public void call(Object... args) {
                Log.i("onConnect","receiving objects");
                JSONObject data = (JSONObject) args[1];
                Iterator<String> iter = data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        objects.put(key, data.get(key).toString());
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
                Log.i("getObjects",objects.size() + " objects received");
                inspectObjects();
            }

        });
    }

    private void inspectObjects(){
        for (Map.Entry<String, String> entry : objects.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            JSONObject object = null;
            JSONObject common = null;
            String role = null;
            ArrayList<String> list = new ArrayList<>();

            try {
                object = new JSONObject(value.toString());
                common = object.getJSONObject("common");
                role = common.optString("role");
                if(role != null) {
                    if (!oRoles.containsKey(role)) {
                        oRoles.put(role, list);
                    }
                    list = (ArrayList<String>) oRoles.get(role);
                    list.add(key);
                    oRoles.put(role, list);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("inspectObjects", oRoles.size() + " roles detected");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    public void onClickSwitch(View v){

        wSwitch = v.findViewById(R.id.vi_switch);
        String tmp = (wSwitch.isChecked()) ? "true" : "false";
        mSocket.emit("setState", "javascript.0.vi_switch", tmp);
        Log.d("onClickSwitch", "new value: " + tmp);

    }

    public void onClickInspectObjects(View v){
        aktienListe.add("Clicks: " + clickCounter++);
        aktienlisteAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this,"Settings", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
