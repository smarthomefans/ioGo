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

package de.nisnagel.iogo.service.util;

import java.io.IOException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

public class NetworkUtils {

    public static String getProCookie(String username, String password) {
        Timber.v("getProCookie called");
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Timber.d("getProCookie: requestBody build");

        Request request = new Request.Builder()
                .url("https://iobroker.pro/login?app=true")
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Referrer", "https://iobroker.pro/login")
                .addHeader("Host", "iobroker.pro")
                .addHeader("Origin", "https://iobroker.pro")
                .build();
        Timber.d("getProCookie: request build");

        Response response = null;
        OkHttpClient client = new OkHttpClient();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Timber.e(e);
        }
        if (response != null) {
            return response.headers().get("Set-Cookie");
        } else {
            Timber.e("getProCookie: no response cookie received");
            return null;
        }
    }

    private static SSLContext getSSLContext() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String cleanUrl(String url) {
        Timber.v("cleanUrl called");
        if (url == null) {
            return null;
        }
        url = url.toLowerCase();
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        if(url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static Socket getSocket(String url) {
        Timber.v("getSocket called");

        IO.setDefaultHostnameVerifier((hostname, session) -> true);
        IO.setDefaultSSLContext(getSSLContext());
        IO.Options opts = new IO.Options();
        //opts.callFactory = okHttpClient;
        //opts.webSocketFactory = okHttpClient;
        opts.forceNew = true;
        opts.transports = new String[]{WebSocket.NAME};

        try {
            return IO.socket(url, opts);
        } catch (Throwable e) {
            Timber.w(e);
        }

        return null;
    }

}
