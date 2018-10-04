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

package de.nisnagel.iogo.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.util.NetworkUtils;
import io.socket.client.Ack;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import timber.log.Timber;

public class WebService {

    private Socket mSocket;
    private String cookie;

    public Context context;

    public SharedPreferences sharedPref;

    public WebService(SharedPreferences sharedPref, Context context) {
        Timber.v("instance created");
        this.sharedPref = sharedPref;
        this.context = context;
    }

    public void init() {
        Timber.v(" init called");
        boolean isCloud = sharedPref.getBoolean(context.getString(R.string.pref_connect_cloud), false);
        boolean isWeb = sharedPref.getBoolean(context.getString(R.string.pref_connect_web), false);
        if (isCloud) {
            String username = sharedPref.getString(context.getString(R.string.pref_connect_cloud_user), null);
            String password = sharedPref.getString(context.getString(R.string.pref_connect_cloud_password), null);
            init_cloud(username, password);
        } else if (isWeb) {
            String url = sharedPref.getString(context.getString(R.string.pref_connect_web_url), null);
            url = NetworkUtils.cleanUrl(url);
            String username = sharedPref.getString(context.getString(R.string.pref_connect_web_user), null);
            String password = sharedPref.getString(context.getString(R.string.pref_connect_web_password), null);
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
            }
        }
    }

    public void on(String event, Emitter.Listener fn){
        if(mSocket != null) {
            mSocket.on(event, fn);
        }else{
            Timber.w("socket is null");
        }
    }

    public void start() {
        Timber.v(" start called");
        if (mSocket != null) {
            if (!mSocket.connected()) {
                mSocket.connect();
            }
        } else {
            Timber.w("socket is null");
        }
    }

    public void stop() {
        Timber.v("stop called");
        if (isConnected()) {
            mSocket.disconnect();
        }
    }

    public void subscribe(final Object... args){
        mSocket.emit("subscribe", args);
    }

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

    public void setState(String id, String val, String type) {
        Timber.v("setState called");
        if (isConnected()) {
            switch (type) {
                case "boolean":
                    Boolean bool = "true".equals(val);
                    mSocket.emit("setState", id, bool);
                    break;
                case "number":
                    Float num = Float.parseFloat(val);
                    mSocket.emit("setState", id, num);
                    break;
                default:
                    mSocket.emit("setState", id, val);
                    break;
            }
        }
    }

    public void getEnumObjects(String key, Ack callback){
        Timber.v("getEnumRooms called");
        if (isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("startkey", key);
                json.put("endkey", key + "\u9999");
            } catch (JSONException e) {
                Timber.e(e);
            }
            mSocket.emit("getObjectView", "system", "enum", json, callback);
        }
    }

    public void getObjects(Object args, Ack callback) {
        Timber.v("getObjects called");
        if (isConnected()) {
            mSocket.emit("getObjects", args, callback);
        }
    }

    public void getHistory(String id, Object args, Ack callback) {
        Timber.v("getHistory called");
        if (isConnected()) {
            mSocket.emit("getHistory", args, callback);
        }
    }

    public void getStates(Object args, Ack callback){
        Timber.v("getStates called");
        mSocket.emit("getStates", args, callback);
    }

    /*
    public void getHistory(final Events.LoadHistory event) {
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
*/
    public boolean isConnected() {
        return (mSocket != null && mSocket.connected());
    }

    /*
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
    */

}
