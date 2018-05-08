package com.example.nagel.io1.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.nagel.io1.ui.settings.SettingsActivity;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

@Singleton
public class SocketService extends Service {
    final private String TAG = "SocketService";

    private final String baseUrl = "https://iobroker.pro";
    private String username;
    private String password;
    private final String origin = "https://iobroker.pro";
    private final String referrer = "https://iobroker.pro/login";;
    private final String host = "iobroker.pro";
    private Socket mSocket;
    private IO.Options opts;
    private String socketUrl;
    private String cookieUrl;
    private String cookie;

    public SocketService() {
        Log.i(TAG, "instance cerated");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DataBus.getBus().register(this);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        username = sharedPref.getString("pro_username", null);
        password = sharedPref.getString("pro_password", null);

        try {
            socketUrl = baseUrl + "/?key=nokey" + "&user=" + URLEncoder.encode(username, "UTF-8") + "&pass=" + password;
        } catch (UnsupportedEncodingException e) {
            socketUrl = baseUrl + "/?key=nokey" + "&user=" + username + "&pass=" + password;
        }
        cookieUrl = baseUrl + "/login?app=true";

        if(username != null) {
            new NetworkAsync().execute();
        }

        Log.i(TAG, "onCreate finished");
    }

    private void getCookie() {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(cookieUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Referrer", referrer)
                .addHeader("Host", host)
                .addHeader("Origin", origin)
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

    private void createSocket() {
        try {
            mSocket = IO.socket(socketUrl, opts);
            mSocket.io().on(Manager.EVENT_TRANSPORT, onTransport);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("stateChange", onStateChange);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void createIoOptions() {
        opts = new IO.Options();
        opts.upgrade = true;
        opts.reconnection = true;
        opts.forceNew = true;
        opts.transports = new String[]{WebSocket.NAME};
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        DataBus.getBus().unregister(this);
        Log.i(TAG, "onDestroy finished");
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("subscribe", "javascript.0.*");
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
            Events.StateChange event = new Events.StateChange();
            event.setId(args[0].toString());
            event.setData(args[1].toString());
            DataBus.getBus().post(event);
        }
    };

    @Subscribe
    public void setState(final Events.SetState event) {
        mSocket.emit("setState", event.getId(), event.getVal());
    }

    @Subscribe
    public void getStates(final Events.getStates event){
        mSocket.emit("getStates", "javascript.0.*",new Ack() {
            @Override
            public void call(Object... args) {
                Log.i("onConnect","receiving states");
                Events.States event = new Events.States();
                event.setData(args[1].toString());
                DataBus.getBus().post(event);
            }
        });
    }

    @Subscribe
    public void getEnumRooms(final Events.getEnumRooms event){
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
                Log.i("getEnumObjects","receiving objects");
                Events.Objects event = new Events.Objects();
                event.setData(args[1].toString());
                DataBus.getBus().post(event);
            }
        });
    }

    @Subscribe
    public void getEnumFunctions(final Events.getEnumFunctions event){
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
                Log.i("getEnumObjects","receiving objects");
                Events.Objects event = new Events.Objects();
                event.setData(args[1].toString());
                DataBus.getBus().post(event);
            }
        });
    }


    public void getStateObjects(final Events.getStateObjects event){
        JSONObject json = new JSONObject();
        try {
            json.put("startkey", "system.states.");
            json.put("endkey", "system.states.\u9999");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("getObjectView", "system", "state", json, new Ack() {
            @Override
            public void call(Object... args) {
                Log.i("getStateObjects","receiving objects");
                Events.Objects event = new Events.Objects();
                event.setData(args[1].toString());
                DataBus.getBus().post(event);
            }
        });
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

            getCookie();

            createIoOptions();

            createSocket();

            mSocket.connect();
            return "You are at PostExecute";
        }

    }

}
