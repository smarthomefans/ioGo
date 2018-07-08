package de.nisnagel.iogo.service;

import android.util.Patterns;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean isValidUrl(String url) {
        Timber.v("isValidUrl called");
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    public static Socket getSocket(String url) {
        Timber.v("getSocket called");
        IO.Options opts = new IO.Options();
        opts.upgrade = true;
        opts.reconnection = true;
        opts.forceNew = true;
        opts.transports = new String[]{WebSocket.NAME};

        try {
            return IO.socket(url, opts);
        } catch (Throwable e) {
            Timber.e(e);
        }

        return null;

    }
}
