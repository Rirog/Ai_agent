package org.example.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    public static boolean isOnline() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("HEAD");
            int code = conn.getResponseCode();
            return (code == 200 || code == 204 || code == 302);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isNetworkError(Exception e) {
        if (e == null || e.getMessage() == null) return false;
        String msg = e.getMessage().toLowerCase();
        return msg.contains("unknown host") ||
                msg.contains("network is unreachable") ||
                msg.contains("connection refused") ||
                msg.contains("timeout") ||
                msg.contains("no address");
    }
}