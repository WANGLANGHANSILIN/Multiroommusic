package cn.com.multiroommusic.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wang l on 2017/5/26.
 */

public class MRMHttp {
    public static InputStream getInputStream(String url) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            return connection.getInputStream();
        }
        return null;
    }

    public static HttpURLConnection getConnection(String url) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            return connection;
        }
        return null;
    }
}
