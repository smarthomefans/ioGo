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

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public WebService() {
        Timber.v("instance created");
    }

    public void initWeb(String url, String username, String password) {
        Timber.v(" initWeb called");
        String socketUrl;
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {

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

    public void initCloud(String username, String password) {
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

    public void on(String event, Emitter.Listener fn) {
        if (mSocket != null) {
            mSocket.on(event, fn);
        } else {
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

    public void subscribe(final Object... args) {
        mSocket.emit("subscribe", args);
    }

    private Emitter.Listener onTransport = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(args != null && args[0] != null) {
                Transport transport = (Transport) args[0];

                transport.on(Transport.EVENT_REQUEST_HEADERS, args1 -> {
                    @SuppressWarnings("unchecked")
                    Map<String, List<String>> headers = (Map<String, List<String>>) args1[0];
                    headers.put("Cookie", Collections.singletonList(cookie));
                });
            }
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
                    Float num = null;
                    if(val != null) {
                        num = Float.parseFloat(val);
                    }
                    mSocket.emit("setState", id, num);
                    break;
                default:
                    mSocket.emit("setState", id, val);
                    break;
            }
        }
    }

    public void getEnumObjects(String key, String type, OnEnumReceived listener) {
        Timber.v("getEnumRooms called");
        if (isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("startkey", key);
                json.put("endkey", key + "\u9999");
            } catch (JSONException e) {
                Timber.e(e);
            }
            Trace trace = FirebasePerformance.getInstance().newTrace("WebService.getEnumObjects");
            trace.start();
            mSocket.emit("getObjectView", "system", "enum", json, (Ack) args -> {
                if (args != null && args.length > 1 && args[1] != null) {
                    trace.putMetric("length", args[1].toString().getBytes().length);
                } else {
                    trace.putMetric("length", 0);
                }
                trace.stop();
                if (args != null && args.length > 1 && args[1] != null) {
                    listener.onEnumReceived(args[1].toString(), type);
                }
            });
        }
    }

    public void getObjects(OnObjectsReceived listener) {
        Timber.v("getObjects called");
        if (isConnected()) {
            Trace trace = FirebasePerformance.getInstance().newTrace("WebService.getObjects");
            trace.start();
            mSocket.emit("getObjects", null, args -> {
                if (args != null && args.length > 1 && args[1] != null) {
                    trace.putMetric("length", args[1].toString().getBytes().length);
                } else {
                    trace.putMetric("length", 0);
                }
                trace.stop();
                if (args != null && args.length > 1 && args[1] != null) {
                    listener.onObjectsReceived(args[1].toString());
                }
            });
        }
    }

    public void getHistory(String id, String type, JSONObject args, OnHistoryReceived listener) {
        Timber.v("getHistory called");
        if (isConnected()) {
            Trace trace = FirebasePerformance.getInstance().newTrace("WebService.getHistory");
            trace.start();
            mSocket.emit("getHistory", id, args, (Ack) args1 -> {
                if (args1 != null && args1.length > 1 && args1[1] != null) {
                    trace.putMetric("length", args1[1].toString().getBytes().length);
                } else {
                    trace.putMetric("length", 0);
                }
                trace.stop();
                if (args1 != null && args1.length > 1 && args1[1] != null) {
                    listener.onHistoryReceived(id, args1[1].toString(), type);
                }
            });
        }
    }

    public void getStates(Object args, OnStatesReceived listener) {
        Timber.v("getStates called");
        if (isConnected()) {
            Trace trace = FirebasePerformance.getInstance().newTrace("WebService.getStates");
            trace.start();
            mSocket.emit("getStates", args, (Ack) args1 -> {
                if (args1 != null && args1.length > 1 && args1[1] != null) {
                    trace.putMetric("length", args1[1].toString().getBytes().length);
                } else {
                    trace.putMetric("length", 0);
                }
                trace.stop();
                if (args1 != null && args1.length > 1 && args1[1] != null) {
                    listener.onStatesReceived(args1[1].toString());
                }
            });
        }
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

}
