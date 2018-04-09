package com.example.nagel.io1.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.service.MyService;
import com.example.nagel.io1.R;
import com.example.nagel.io1.viewmodel.ListViewModel;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Switch wSwitch;
    private TextView wTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        wTextView = findViewById(R.id.textState);

        DataBus.getBus().register(this);

        Intent intent = new Intent(this, MyService.class);
        startService(intent);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Subscribe
    public void onStateChange(final Events.StateChange event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("onStateChange", event.getId() + " => " + event.getText());
                wTextView.setText(event.getId());
                if ("javascript.0.vi_switch".equals(event.getId())) {
                    JSONObject data = null;
                    try {
                        data = new JSONObject(event.getText());
                        wSwitch.setChecked(data.getBoolean("val"));
                    } catch(JSONException e){
                        Log.e("onStateChange", e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataBus.getBus().unregister(this);
    }

    public void onClickSwitch(View v){

        wSwitch = v.findViewById(R.id.vi_switch);
        String tmp = (wSwitch.isChecked()) ? "true" : "false";

        Events.SetState event = new Events.SetState();
        event.setId("javascript.0.vi_switch");
        event.setVal(tmp);
        DataBus.getBus().post(event);

        Log.d("onClickSwitch", "new value: " + tmp);

    }

    public void onClickShowList(View v){
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
    }

    public void onClickShowTemp(View v){
        Intent i = new Intent(this, TemperatureActivity.class);
        startActivity(i);
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
