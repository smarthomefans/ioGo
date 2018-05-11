package com.example.nagel.io1.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.nagel.io1.data.model.AppDatabase;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.data.model.StateDao;
import com.example.nagel.io1.data.repository.FunctionRepository;
import com.example.nagel.io1.data.repository.RoomRepository;
import com.example.nagel.io1.data.repository.StateRepository;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SocketService extends Service {
    final private String TAG = "SocketService";

    private Socket mSocket;
    private String cookie;

    @Inject
    public FunctionRepository functionRepository;

    @Inject
    public RoomRepository roomRepository;

    @Inject
    public StateRepository stateRepository;

    public SocketService() {
        Log.i(TAG, "instance cerated");
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();

        DataBus.getBus().register(this);

        new NetworkAsync().execute();

        Log.i(TAG, "onCreate finished");
    }

    private void init(){
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isProEnabled = sharedPref.getBoolean("pro_cloud_enabled",false);
        if(isProEnabled) {
            String username = sharedPref.getString("pro_username", null);
            String password = sharedPref.getString("pro_password", null);
            init_pro(username,password);
        }else{
            String ssid = sharedPref.getString("wifi_ssid", null);
            String url = sharedPref.getString("wifi_url", null);
            init_wifi(ssid,url);

        }
    }

    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
        return null;
    }

    private void init_wifi(String ssid, String url) {
        String current_ssid = getWifiName(this);
        if(ssid != null && ssid.equals(current_ssid)) {
            createSocket(url);
        }
    }

    private void init_pro(String username, String password){
        String cookieUrl = "https://iobroker.pro/login?app=true";
        getCookie(cookieUrl, username, password);

        try {
            username = URLEncoder.encode(username, "UTF-8");
            password = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String socketUrl = "https://iobroker.pro/?key=nokey" + "&user=" + username + "&pass=" + password;
        createSocket(socketUrl);
    }

    private void getCookie(String url, String username, String password) {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Referrer", "https://iobroker.pro/login")
                .addHeader("Host", "iobroker.pro")
                .addHeader("Origin", "https://iobroker.pro")
                .build();
        Response response = null;
        OkHttpClient client = new OkHttpClient();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cookie = response.headers().get("Set-Cookie");
    }

    private void createSocket(String url) {
        IO.Options opts = new IO.Options();
        opts.upgrade = true;
        opts.reconnection = true;
        opts.forceNew = true;
        opts.transports = new String[]{WebSocket.NAME};

        try {
            mSocket = IO.socket(url, opts);
            mSocket.io().on(Manager.EVENT_TRANSPORT, onTransport);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("stateChange", onStateChange);
            mSocket.connect();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
            stateRepository.saveState(args[0].toString(), args[1].toString());
        }
    };

    @Subscribe
    public void setState(final Events.SetState event) {
        if(isConnected()) {
            mSocket.emit("setState", event.getId(), event.getVal());
        }
    }

    @Subscribe
    public void getStates(final Events.getStates event){
        if(isConnected()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //List<String> stateIds = stateRepository.getAllStateIds();
                    //JSONArray json = new JSONArray(stateIds);
                    JSONArray json = new JSONArray();
                    List<State> states = stateRepository.getAllStates();
                    for(int i = 0; i<states.size();i++){
                        json.put(states.get(i).getId());
                    }
                    mSocket.emit("getStates", json, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.i("onConnect", "receiving states");
                            stateRepository.saveStates(args[1].toString());
                        }
                    });
                }
            });
        }
    }

    @Subscribe
    public void getObjects(final Events.getObjects event){
        if(isConnected()) {
            mSocket.emit("getObjects", null, new Ack() {
                @Override
                public void call(Object... args) {
                    Log.i("getEnumObjects", "receiving objects");
                    stateRepository.saveObjects(args[1].toString());
                }
            });
        }
    }

    @Subscribe
    public void getEnumRooms(final Events.getEnumRooms event){
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
                    Log.i("getEnumObjects", "receiving objects");
                    roomRepository.saveObjects(args[1].toString());
                }
            });
        }
    }

    @Subscribe
    public void getEnumFunctions(final Events.getEnumFunctions event){
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
                    Log.i("getEnumObjects", "receiving objects");
                    functionRepository.saveObjects(args[1].toString());
                }
            });
        }
    }

    private Boolean isConnected(){
        if(mSocket == null){
            init();
        }
        return (mSocket != null && mSocket.connected());
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class NetworkAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            init();

            return "You are at PostExecute";
        }

    }

}
