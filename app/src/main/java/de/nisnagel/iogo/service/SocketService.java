package de.nisnagel.iogo.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.nisnagel.iogo.data.model.AppDatabase;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.data.repository.StateRepository;
import io.socket.client.Ack;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

public class SocketService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener{
    final private String TAG = "SocketService";

    private Socket mSocket;
    private String cookie;

    SharedPreferences sharedPref;

    @Inject
    public EnumRepository enumRepository;

    @Inject
    public StateRepository stateRepository;

    public SocketService() {
        Log.i(TAG, "instance created");
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        DataBus.getBus().register(this);

        stateRepository.saveSocketState("unknown");

        Log.i(TAG, "onCreate finished");
    }

    private void init(){
        Boolean isProEnabled = sharedPref.getBoolean("pro_cloud_enabled",false);
        if(isProEnabled) {
            String username = sharedPref.getString("pro_username", null);
            String password = sharedPref.getString("pro_password", null);
            init_pro(username,password);
        }else{
            String url = sharedPref.getString("mobile_socket_url", null);
            if(url != null && NetworkUtils.isValidUrl(url)) {
                createSocket(url);
            }
        }
    }

    private void init_pro(String username, String password){
        if(username != null && password != null) {
            cookie = NetworkUtils.getProCookie(username, password);

            try {
                username = URLEncoder.encode(username, "UTF-8");
                password = URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String socketUrl = "https://iobroker.pro/?key=nokey" + "&user=" + username + "&pass=" + password;
            if(cookie != null) {
                createSocket(socketUrl);
            }
        }
    }

    private void createSocket(String url) {
        mSocket = NetworkUtils.getSocket(url);

        if(mSocket != null) {
            if(cookie != null) {
                mSocket.io().on(Manager.EVENT_TRANSPORT, onTransport);
            }
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("stateChange", onStateChange);
            mSocket.connect();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!isConnected()) {
            new NetworkAsync().execute();
            Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isConnected()) {
            mSocket.disconnect();
        }
        DataBus.getBus().unregister(this);

        Log.i(TAG, "onDestroy finished");
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<String> stateIds = stateRepository.getAllStateIds();
                    JSONArray json = new JSONArray(stateIds);
                    mSocket.emit("subscribe", json);
                    syncStates();
                }
            });
            stateRepository.saveSocketState("connected");
            Log.i(TAG, "connected");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            stateRepository.saveSocketState("offline");
            Log.i(TAG, "disconnected");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "connect error");
        }
    };

    private Emitter.Listener onTransport = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Transport transport = (Transport) args[0];

            transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    @SuppressWarnings("unchecked")
                    Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                    headers.put("Cookie", Arrays.asList(cookie));
                }
            });
        }
    };

    private Emitter.Listener onStateChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(args[0] != null) {
                if(args[1] != null) {
                    stateRepository.saveStateChange(args[0].toString(), args[1].toString());
                }else{
                    Log.w(TAG,"onStateChange: state deleted: "+args[0].toString());
                }
            }
        }
    };

    @Subscribe
    public void setState(final Events.SetState event) {
        if(isConnected()) {
            mSocket.emit("setState", event.getId(), event.getVal());
        }
    }

    public void syncStates(){
        if(isConnected()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    JSONArray json = new JSONArray();
                    List<String> stateIds = stateRepository.getAllStateIds();
                    for(int i = 0; i<stateIds.size();i++){
                        json.put(stateIds.get(i));
                    }
                    mSocket.emit("getStates", json, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.i(TAG,"syncStates: receiving states");
                            stateRepository.saveStateChanges(args[1].toString());
                        }
                    });
                }
            });
        }
    }

    @Subscribe
    public void syncObjects(final Events.SyncObjects event){
        if(isConnected()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    getEnumRooms();
                }
            });
        }
    }

    @Subscribe
    public void loadHistory(final Events.LoadHistory event){
        if(isConnected()) {
            JSONObject json = new JSONObject();
            try {
                //json.put("id", "javascript.0.flur.temperature");
                //json.put("start", 1);//(System.currentTimeMillis() - 3600000));
                //json.put("end", System.currentTimeMillis());
                json.put("aggregate", "none");
                //json.put("count", "100");
                //json.put("timeout", "2000");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("getHistory", "javascript.0.flur.temperature", json, new Ack() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG,"loadHistory: receiving historical data");
                }
            });
        }
    }

    public void getEnumRooms(){
        if(isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("startkey", "enum.rooms.");
                json.put("endkey", "enum.rooms.\u9999");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("getObjectView", "system", "enum", json, new Ack() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG,"getEnumRooms: receiving objects");
                    enumRepository.saveRoomObjects(args[1].toString());
                    getEnumFunctions();
                }
            });
        }
    }

    public void getEnumFunctions(){
        if(isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("startkey", "enum.functions.");
                json.put("endkey", "enum.functions.\u9999");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("getObjectView", "system", "enum", json, new Ack() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG,"getEnumFunctions: receiving objects");
                    enumRepository.saveFunctionObjects(args[1].toString());
                    getStates();
                }
            });
        }
    }

    public void getStates(){
        if(isConnected()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    JSONArray json = new JSONArray();
                    List<String> stateIds = enumRepository.getAllStateIds();
                    for(int i = 0; i<stateIds.size();i++){
                        json.put(stateIds.get(i));
                    }
                    mSocket.emit("getStates", json, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.i(TAG,"getStates: receiving states");
                            stateRepository.saveStateChanges(args[1].toString());
                            getObjects();
                        }
                    });
                }
            });
        }
    }

    public void getObjects(){
        if(isConnected()) {
            mSocket.emit("getObjects", null, new Ack() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG,"getObjects: receiving objects");
                    stateRepository.saveObjects(args[1].toString());
                }
            });
        }
    }

    private Boolean isConnected(){
        return (mSocket != null && mSocket.connected());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Toast.makeText(getApplicationContext(),"Settings changed", Toast.LENGTH_LONG).show();
        new NetworkAsync().execute();
    }

    class NetworkAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");
            if(isConnected()){
                mSocket.disconnect();
            }
            init();

            return "You are at PostExecute";
        }

    }

}
