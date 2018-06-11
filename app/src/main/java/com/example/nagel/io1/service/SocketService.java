package com.example.nagel.io1.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.nagel.io1.data.model.AppDatabase;
import com.example.nagel.io1.data.repository.FunctionRepository;
import com.example.nagel.io1.data.repository.RoomRepository;
import com.example.nagel.io1.data.repository.StateRepository;
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
    public FunctionRepository functionRepository;

    @Inject
    public RoomRepository roomRepository;

    @Inject
    public StateRepository stateRepository;

    @Inject
    public AppDatabase appDatabase;

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
        //new NetworkAsync().execute();
        Toast.makeText(this, "service created", Toast.LENGTH_LONG).show();

        Log.i(TAG, "onCreate finished");
    }

    private void init(){
        Boolean isProEnabled = sharedPref.getBoolean("pro_cloud_enabled",false);
        if(isProEnabled) {
            String username = sharedPref.getString("pro_username", null);
            String password = sharedPref.getString("pro_password", null);
            init_pro(username,password);
        }else{
            String ssid = sharedPref.getString("wifi_ssid", null);

            String current_ssid = null;//NetworkUtils.getWifiName(this);
            if(ssid != null && ssid.equals(current_ssid)) {
                String url = sharedPref.getString("wifi_socket_url", null);
                init_direct(url);
            }else{
                String url = sharedPref.getString("mobile_socket_url", null);
                init_direct(url);
            }
        }
    }

    private void init_direct(String url) {
        if(url != null) {
            createSocket(url);
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
            createSocket(socketUrl);
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
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!isConnected()) {
            new NetworkAsync().execute();
            Toast.makeText(this, "service start command", Toast.LENGTH_LONG).show();
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

        Toast.makeText(this, "service destroyed", Toast.LENGTH_LONG).show();

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

                }
            });

            Log.i(TAG, "connected");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
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

    @Subscribe
    public void sync(final Events.SyncObjects event){
        if(isConnected()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    appDatabase.clearAllTables();
                    getEnumRooms();
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
                    roomRepository.saveObjects(args[1].toString());
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
                    functionRepository.saveObjects(args[1].toString());
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
                    List<String> functionStateIds = functionRepository.getAllStateIds();
                    List<String> roomStateIds = roomRepository.getAllStateIds();
                    for(String s: roomStateIds){
                        if(!functionStateIds.contains(s))
                            functionStateIds.add(s);
                    }
                    for(int i = 0; i<functionStateIds.size();i++){
                        json.put(functionStateIds.get(i));
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
        if(mSocket == null){
            new NetworkAsync().execute();
        }
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
