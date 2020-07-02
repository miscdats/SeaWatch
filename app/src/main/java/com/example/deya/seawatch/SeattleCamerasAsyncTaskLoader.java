package com.example.deya.seawatch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

public class SeattleCamerasAsyncTaskLoader extends AsyncTaskLoader<String> {

    private final static String TAG = "CAMERA_ASYNC_LOAD: ";

    private String queryString;

    public SeattleCamerasAsyncTaskLoader(Context context, String queryString) {
        super(context);
        setQueryString(queryString);
    }

    private void setQueryString(String s) {
        this.queryString = "https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return NetworkConnection.getData(queryString);
    }

    protected void onStartLoading() {
        forceLoad();
    }
}
