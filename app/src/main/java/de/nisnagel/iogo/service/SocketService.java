package de.nisnagel.iogo.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
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
import timber.log.Timber;

public class SocketService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Socket mSocket;
    private String cookie;

    SharedPreferences sharedPref;

    @Inject
    public EnumRepository enumRepository;

    @Inject
    public StateRepository stateRepository;

    public SocketService() {
        Timber.v("instance created");
    }

    @Override
    public void onCreate() {
        Timber.v(" onCreate called");
        AndroidInjection.inject(this);
        super.onCreate();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        DataBus.getBus().register(this);

        stateRepository.saveSocketState("unknown");
    }

    private void init(){
        Timber.v(" init called");
        boolean isProEnabled = sharedPref.getBoolean("pro_cloud_enabled",false);
        if(isProEnabled) {
            String username = sharedPref.getString("pro_username", null);
            String password = sharedPref.getString("pro_password", null);
            init_pro(username,password);
        }else{
            String url = sharedPref.getString("mobile_socket_url", null);
            if(url != null && NetworkUtils.isValidUrl(url)) {
                createSocket(url);
            }else{
                Timber.e("url invalid or empty");
            }
        }
    }

    private void init_pro(String username, String password){
        Timber.v(" init_pro called");
        if(username != null && password != null) {
            cookie = NetworkUtils.getProCookie(username, password);

            try {
                username = URLEncoder.encode(username, "UTF-8");
                password = URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Timber.e(e);
            }
            String socketUrl = "https://iobroker.pro/?key=nokey" + "&user=" + username + "&pass=" + password;
            if(cookie != null) {
                createSocket(socketUrl);
            }else{
                Timber.e("cookie missing");
            }
        }
    }

    private void createSocket(String url) {
        Timber.v(" createSocket called");
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
        Timber.v(" onStartCommand called");
        if(!isConnected()) {
            new NetworkAsync().execute();
            Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.v("onDestroy called");
        super.onDestroy();
        if(isConnected()) {
            mSocket.disconnect();
        }
        DataBus.getBus().unregister(this);
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

            Timber.i("connected");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            stateRepository.saveSocketState("offline");
            Timber.i("disconnected");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Timber.w("connect error");
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
            Timber.v("onStateChange called");
            if(args[0] != null) {
                if(args[1] != null) {
                    stateRepository.saveStateChange(args[0].toString(), args[1].toString());
                }else{
                    Timber.w("onStateChange: state deleted stateId:"+args[0].toString());
                }
            }
        }
    };

    @Subscribe
    public void setState(final Events.SetState event) {
        Timber.v("setState called");
        if(isConnected()) {
            mSocket.emit("setState", event.getId(), event.getVal());
        }
    }

    private void syncStates(){
        Timber.v("setState called");
        if(isConnected()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    JSONArray json = new JSONArray();
                    List<String> stateIds = stateRepository.getAllStateIds();
                    if(stateIds != null && stateIds.size() > 0) {
                        for (int i = 0; i < stateIds.size(); i++) {
                            json.put(stateIds.get(i));
                        }
                        Timber.i("getStates: reuquesting "+stateIds.size()+" states from server");
                        mSocket.emit("getStates", json, new Ack() {
                            @Override
                            public void call(Object... args) {
                                Timber.i("syncStates: receiving states");
                                stateRepository.saveStateChanges(args[1].toString());
                            }
                        });
                    }else{
                        Timber.w("getStates: no states found in database");
                    }
                }
            });
        }
    }

    @Subscribe
    public void syncObjects(final Events.SyncObjects event){
        Timber.v("syncObjects called");
        if(isConnected()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    getEnumRooms();
                }
            });
        }
    }

    private void getEnumRooms(){
        Timber.v("getEnumRooms called");
        if(isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("startkey", "enum.rooms.");
                json.put("endkey", "enum.rooms.\u9999");
            } catch (JSONException e) {
                Timber.e(e);
            }
            Timber.i("getStates: reuquesting enum.rooms from server");
            mSocket.emit("getObjectView", "system", "enum", json, new Ack() {
                @Override
                public void call(Object... args) {
                    Timber.i("getEnumRooms: receiving enum.rooms");
                    enumRepository.saveRoomObjects(args[1].toString());
                    getEnumFunctions();
                }
            });
        }
    }

    private void getEnumFunctions(){
        Timber.v("getEnumFunctions called");
        if(isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("startkey", "enum.functions.");
                json.put("endkey", "enum.functions.\u9999");
            } catch (JSONException e) {
                Timber.e(e);
            }
            Timber.i("getStates: reuquesting enum.functions from server");
            mSocket.emit("getObjectView", "system", "enum", json, new Ack() {
                @Override
                public void call(Object... args) {
                    Timber.i("getEnumFunctions: receiving enum.functions");
                    enumRepository.saveFunctionObjects(args[1].toString());
                    getStates();
                }
            });
        }
    }

    private void getStates(){
        Timber.v("getStates called");
        if(isConnected()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    JSONArray json = new JSONArray();
                    List<String> stateIds = enumRepository.getAllStateIds();
                    if(stateIds != null && stateIds.size() > 0) {
                        for (int i = 0; i < stateIds.size(); i++) {
                            json.put(stateIds.get(i));
                        }
                        Timber.i("getStates: reuquesting "+stateIds.size()+" states from server");
                        mSocket.emit("getStates", json, new Ack() {
                            @Override
                            public void call(Object... args) {
                                Timber.i("getStates: receiving states");
                                stateRepository.saveStateChanges(args[1].toString());
                                getObjects();
                            }
                        });
                    }else{
                        Timber.w("getStates: no states found in database");
                    }
                }
            });
        }
    }

    private void getObjects(){
        Timber.v("getObjects called");
        if(isConnected()) {
            Timber.i("getObjects: requesting all objects from server");
            mSocket.emit("getObjects", null, new Ack() {
                @Override
                public void call(Object... args) {
                    stateRepository.saveObjects(args[1].toString());
                }
            });
        }
    }

    private boolean isConnected(){
        return (mSocket != null && mSocket.connected());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("mobile_socket_url") || key.equals("pro_cloud_enabled") || key.equals("pro_username") || key.equals("pro_password")) {
            Toast.makeText(getApplicationContext(), "Settings changed", Toast.LENGTH_LONG).show();

            new NetworkAsync().execute();
        }
    }

    class NetworkAsync extends AsyncTask<Void, Integer, String>
    {
        protected String doInBackground(Void...arg0) {
            Timber.v(" doInBackground called");
            if(isConnected()){
                mSocket.disconnect();
            }
            init();

            return "You are at PostExecute";
        }

    }

}
