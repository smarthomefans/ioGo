package com.example.nagel.io1.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.nagel.io1.service.model.IoState;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends Service {
    private Socket mSocket;

    public SocketService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        DataBus.getBus().register(this);

        try {
            mSocket = IO.socket("http://192.168.1.33:8084/");
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("stateChange", onStateChange);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.connect();
        getStates();
        getObjects();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataBus.getBus().unregister(this);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("subscribe", "javascript.0.*");
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

    public void getStates(){
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


    public void getStates(Ack ack){
        mSocket.emit("getStates", "javascript.0.*", ack);
    }

    private void getObjects(){
        mSocket.emit("getObjects", null, new Ack() {
            @Override
            public void call(Object... args) {
                Log.i("onConnect","receiving objects");
                JSONObject data = (JSONObject) args[1];

            }

        });
    }
/*
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
*/
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
