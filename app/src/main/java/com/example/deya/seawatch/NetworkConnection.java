package com.example.deya.seawatch;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkConnection {

    final static String TAG = "NETWORK_CONNECTION: ";

    public static String getData(String url) {
        try {
            return getData(new URL(url));
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Malformed URI");
        }
        return null;
    }

    public static String getData(String url, String... uriParams) {
        Uri.Builder builder = Uri.parse(url).buildUpon();

        if ((uriParams.length % 2) != 0) {
            Log.e(TAG, "Odd number of params provided... weird!?");
            return null;
        }

        for (int i = 0; i < uriParams.length; i += 2) {
            builder.appendQueryParameter(uriParams[i], uriParams[i + 1]);
        }

        return getData(builder.build().toString());
    }

    public static String getData(URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String results = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            results = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }
}
