/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.data.repository.StateHistoryRepository;
import de.nisnagel.iogo.data.repository.StateRepository;
import de.nisnagel.iogo.service.util.HistoryUtils;
import de.nisnagel.iogo.service.util.NetworkUtils;
import de.nisnagel.iogo.service.util.SyncUtils;
import io.socket.client.Ack;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import timber.log.Timber;

public class SocketService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Socket mSocket;
    private String cookie;

    @Inject
    public SharedPreferences sharedPref;

    @Inject
    public EnumRepository enumRepository;

    @Inject
    public StateRepository stateRepository;

    @Inject
    public StateHistoryRepository stateHistoryRepository;

    public SocketService() {
        Timber.v("instance created");
    }

    @Override
    public void onCreate() {
        Timber.v(" onCreate called");
        AndroidInjection.inject(this);
        super.onCreate();

        boolean isWebEnabled = sharedPref.getBoolean(getString(R.string.pref_connect_web), false);
        boolean isCloudEnabled = sharedPref.getBoolean(getString(R.string.pref_connect_cloud), false);
        if (isWebEnabled || isCloudEnabled) {
            stateRepository.saveSocketState("unknown");
        }
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        DataBus.getBus().register(this);

    }

    private void init() {
        Timber.v(" init called");
        boolean isCloud = sharedPref.getBoolean(getString(R.string.pref_connect_cloud), false);
        boolean isWeb = sharedPref.getBoolean(getString(R.string.pref_connect_web), false);
        if (isCloud) {
            String username = sharedPref.getString(getString(R.string.pref_connect_cloud_user), null);
            String password = sharedPref.getString(getString(R.string.pref_connect_cloud_password), null);
            init_cloud(username, password);
        } else if (isWeb) {
            String url = sharedPref.getString(getString(R.string.pref_connect_web_url), null);
            url = NetworkUtils.cleanUrl(url);
            String username = sharedPref.getString(getString(R.string.pref_connect_web_user), null);
            String password = sharedPref.getString(getString(R.string.pref_connect_web_password), null);
            init_web(url, username, password);
        }
    }

    private void init_web(String url, String username, String password) {
        Timber.v(" init_web called");
        String socketUrl;
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            //cookie = NetworkUtils.getCookie(url, username, password);

            try {
                username = URLEncoder.encode(username, "UTF-8");
                password = URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Timber.e(e);
            }
            socketUrl = url + "/?key=nokey" + "&user=" + username + "&pass=" + password;
        } else {
            socketUrl = url;
        }

        createSocket(socketUrl);
    }

    private void init_cloud(String username, String password) {
        Timber.v(" init_cloud called");
        if (username != null && password != null) {
            cookie = NetworkUtils.getProCookie(username, password);

            try {
                username = URLEncoder.encode(username, "UTF-8");
                password = URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Timber.e(e);
            }
            String socketUrl = "https://iobroker.pro/?key=nokey" + "&user=" + username + "&pass=" + password;
            if (cookie != null) {
                createSocket(socketUrl);
            } else {
                Timber.e("cookie missing");
            }
        }
    }

    private void createSocket(String url) {
        Timber.v(" createSocket called");
        if (mSocket == null) {
            mSocket = NetworkUtils.getSocket(url);
            if (mSocket != null) {
                if (cookie != null) {
                    mSocket.io().on(Manager.EVENT_TRANSPORT, onTransport);
                } else {
                    Timber.d("cookie is null");
                }
                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                mSocket.on("stateChange", onStateChange);
            }
        }

        if (mSocket != null) {
            if (!mSocket.connected()) {
                mSocket.connect();
            }
        } else {
            Timber.w("socket is null");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.v(" onStartCommand called");

        boolean isWebEnabled = sharedPref.getBoolean(getString(R.string.pref_connect_web), false);
        boolean isCloudEnabled = sharedPref.getBoolean(getString(R.string.pref_connect_cloud), false);
        if (isWebEnabled || isCloudEnabled) {
            if (!isConnected()) {
                new NetworkAsync().execute();
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.v("onDestroy called");
        super.onDestroy();
        if (isConnected()) {
            mSocket.disconnect();
        }
        DataBus.getBus().unregister(this);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            AsyncTask.execute(() -> {
                List<String> stateIds = stateRepository.getAllStateIds();
                JSONArray json = new JSONArray(stateIds);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mSocket.emit("subscribe", json);
                        getStates();
                    }
                }, 2000);
            });
            stateRepository.saveSocketState("connected");

            Timber.i("connected");
        }
    };

    @Subscribe
    public void addUser(final Events.User event) {
        Timber.v("addUser called");
        if (isConnected()) {
            JSONObject json = new JSONObject();
            JSONObject common = new JSONObject();
            try {
                json.put("type", "state");
                common.put("read", "true");
                common.put("write", "true");
                common.put("desc", "nur ein test");
                common.put("role", "app.token");
                json.put("common", common);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("setObject", "iogo.0." + event.name + ".token", json, (Ack) args -> {
                Timber.i("setObject: receiving data");
            });
            setState(new Events.SetState("iogo.0." + event.name + ".token", event.token, "string"));
        }
    }

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            stateRepository.saveSocketState("offline");
            stateRepository.setSyncAll(false);
            Timber.i("disconnected");
        }
    };

    private Emitter.Listener onConnectError = args -> Timber.w("connect error");

    private Emitter.Listener onTransport = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Transport transport = (Transport) args[0];

            transport.on(Transport.EVENT_REQUEST_HEADERS, args1 -> {
                @SuppressWarnings("unchecked")
                Map<String, List<String>> headers = (Map<String, List<String>>) args1[0];
                headers.put("Cookie", Collections.singletonList(cookie));
            });
        }
    };

    private Emitter.Listener onStateChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Timber.v("onStateChange called");
            if (args[0] != null) {
                if (args[1] != null) {
                    SyncUtils.saveState(stateRepository, args[0].toString(), args[1].toString());
                } else {
                    Timber.w("onStateChange: state deleted stateId:" + args[0].toString());
                }
            }
            Timber.v("onStateChange");
        }
    };

    @Subscribe
    public void setState(final Events.SetState event) {
        Timber.v("setState called");
        if (isConnected()) {
            switch (event.getType()) {
                case "boolean":
                    Boolean bool = "true".equals(event.getVal());
                    mSocket.emit("setState", event.getId(), bool);
                    break;
                case "number":
                    Float num = Float.parseFloat(event.getVal());
                    mSocket.emit("setState", event.getId(), num);
                    break;
                default:
                    mSocket.emit("setState", event.getId(), event.getVal());
                    break;
            }
        }
    }

    @Subscribe
    public void syncObjects(final Events.SyncObjects event) {
        Timber.v("syncObjects called");
        if (isConnected()) {
            AsyncTask.execute(this::getEnumRooms);
        }
    }

    private void getEnumRooms() {
        Timber.v("getEnumRooms called");
        if (isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("startkey", "enum.rooms.");
                json.put("endkey", "enum.rooms.\u9999");
            } catch (JSONException e) {
                Timber.e(e);
            }
            Timber.i("getEnumRooms: reuquesting enum.rooms from server");
            mSocket.emit("getObjectView", "system", "enum", json, (Ack) args -> {
                Timber.i("getEnumRooms: receiving enum.rooms");
                if (args[1] != null) {
                    SyncUtils.saveEnums(enumRepository, args[1].toString(), EnumRepository.TYPE_ROOM);
                }
                getEnumFunctions();
            });
        }
    }

    private void getEnumFunctions() {
        Timber.v("getEnumFunctions called");
        if (isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("startkey", "enum.functions.");
                json.put("endkey", "enum.functions.\u9999");
            } catch (JSONException e) {
                Timber.e(e);
            }
            Timber.i("getEnumFunctions: reuquesting enum.functions from server");
            mSocket.emit("getObjectView", "system", "enum", json, (Ack) args -> {
                Timber.i("getEnumFunctions: receiving enum.functions");
                if (args[1] != null) {
                    SyncUtils.saveEnums(enumRepository, args[1].toString(), EnumRepository.TYPE_FUNCTION);
                }
                getObjects();
            });
        }
    }

    private void getObjects() {
        Timber.v("getObjects called");
        if (isConnected()) {
            Timber.i("getObjects: requesting all objects from server");
            mSocket.emit("getObjects", null, args -> {
                if (args[1] != null) {
                    SyncUtils.saveObjects(stateRepository, args[1].toString());
                }
                getStates();
            });
        }
    }

    private void getStates() {
        Timber.v("getStates called");
        if (isConnected()) {
            AsyncTask.execute(() -> {
                JSONArray json = new JSONArray();
                List<String> objectIds = stateRepository.getAllStateIds();
                if (objectIds != null && objectIds.size() > 0) {
                    for (int i = 0; i < objectIds.size(); i++) {
                        json.put(objectIds.get(i));
                    }
                    Timber.i("getStates: reuquesting " + objectIds.size() + " states from server");
                    mSocket.emit("getStates", json, (Ack) args -> {
                        Timber.i("getStates: receiving states");
                        if (args[1] != null) {
                            SyncUtils.saveStates(stateRepository, args[1].toString());
                        }
                    });
                } else {
                    Timber.w("getStates: no states found in database");
                }
            });
        }
    }

    @Subscribe
    public void loadHistory(final Events.LoadHistory event) {
        Timber.v("loadHistory called");
        if (isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("id", event.id);
                json.put("start", HistoryUtils.startOfDay * 1000);
                json.put("end", HistoryUtils.endOfDay * 1000);
                json.put("step", "60000");
                json.put("aggregate", "average");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("getHistory", event.id, json, (Ack) args -> {
                if (args[1] != null && !"[]".equals(args[1].toString())) {
                    stateHistoryRepository.syncHistoryDay(event.id, args[1].toString());
                    Timber.i("loadHistory: receiving historical data");
                }
            });
            json = new JSONObject();
            try {
                json.put("id", event.id);
                json.put("start", HistoryUtils.startOfWeek * 1000);
                json.put("end", HistoryUtils.endOfDay * 1000);
                json.put("step", (HistoryUtils.endOfDay - HistoryUtils.startOfWeek) * 1000 / 200);
                json.put("aggregate", "average");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("getHistory", event.id, json, (Ack) args -> {
                if (args[1] != null && !"[]".equals(args[1].toString())) {
                    stateHistoryRepository.syncHistoryWeek(event.id, args[1].toString());
                    Timber.i("loadHistory: receiving historical data");
                }
            });
            json = new JSONObject();
            try {
                json.put("id", event.id);
                json.put("start", HistoryUtils.startOfMonth * 1000);
                json.put("end", HistoryUtils.endOfDay * 1000);
                json.put("step", (HistoryUtils.endOfDay - HistoryUtils.startOfMonth) * 1000 / 200);
                json.put("aggregate", "average");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("getHistory", event.id, json, (Ack) args -> {
                if (args[1] != null && !"[]".equals(args[1].toString())) {
                    stateHistoryRepository.syncHistoryMonth(event.id, args[1].toString());
                    Timber.i("loadHistory: receiving historical data");
                }
            });
            json = new JSONObject();
            try {
                json.put("id", event.id);
                json.put("start", HistoryUtils.startOfYear * 1000);
                json.put("end", HistoryUtils.endOfDay * 1000);
                json.put("step", (HistoryUtils.endOfDay - HistoryUtils.startOfYear) * 1000 / 200);
                json.put("aggregate", "average");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("getHistory", event.id, json, (Ack) args -> {
                if (args[1] != null && !"[]".equals(args[1].toString())) {
                    stateHistoryRepository.syncHistoryYear(event.id, args[1].toString());
                    Timber.i("loadHistory: receiving historical data");
                }
            });
        }
    }

    private boolean isConnected() {
        return (mSocket != null && mSocket.connected());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_connect_web))
                || key.equals(getString(R.string.pref_connect_web_url))
                || key.equals(getString(R.string.pref_connect_web_user))
                || key.equals(getString(R.string.pref_connect_web_password))
                || key.equals(getString(R.string.pref_connect_cloud))
                || key.equals(getString(R.string.pref_connect_cloud_user))
                || key.equals(getString(R.string.pref_connect_cloud_password))) {
            Toast.makeText(getApplicationContext(), "Settings changed", Toast.LENGTH_LONG).show();

            new NetworkAsync().execute();
        }
    }

    class NetworkAsync extends AsyncTask<Void, Integer, String> {
        protected String doInBackground(Void... arg0) {
            Timber.v(" doInBackground called");
            if (isConnected()) {
                mSocket.disconnect();
            }
            init();

            return "You are at PostExecute";
        }

    }

}
