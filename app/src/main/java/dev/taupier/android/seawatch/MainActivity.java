package dev.taupier.android.seawatch;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = "MAIN_ACTIVITY : ";
    static TextView textLocation;
    static Context context;
    private SDOTCameraAdapter cameraAdapter;
    private TrafficCamera[] trafficCameras;

    private FusedLocationProviderClient mLocationClient;
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    protected static String CAM_INFO = "Query: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLocation = (TextView)findViewById(R.id.text_location);
        Button button = findViewById(R.id.btn_data_get);
        button.setOnClickListener(this);

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Bundle bundle = new Bundle();
            getSupportLoaderManager().restartLoader(0, bundle, this);
        } else {
            Toast.makeText(this,
                    getResources().getString(R.string.no_connection),
                    Toast.LENGTH_SHORT).show();
        }

        Bundle bundle = new Bundle();
        bundle.putString("queryString", "Fauntleroy_SW_Cloverdale_NS.jpg");
        getSupportLoaderManager().restartLoader(0, bundle, this);

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new SeattleCamerasAsyncTaskLoader(this, "");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        try {
            // the JSON object itself
            JSONObject rootObject = new JSONObject(s);
            JSONArray features = rootObject.getJSONArray("Features");

            // create list of all data in object
            trafficCameras = new TrafficCamera[features.length()];

            for (int i = 0; i < features.length(); i++) {
                JSONObject current = features.getJSONObject(i);

                JSONArray coordinates = current.getJSONArray("PointCoordinate");
                double latitude = coordinates.getDouble(0);
                double longitude = coordinates.getDouble(1);

                JSONArray cameras = current.getJSONArray("Cameras");
                JSONObject camera = cameras.getJSONObject(0);

                String id = camera.getString("Id");
                String description = camera.getString("Description");
                String imageUrl = camera.getString("ImageUrl");
                String type = camera.getString("Type");

                TrafficCamera trafficCamera = new TrafficCamera(latitude, longitude, id, description, imageUrl, type);
                trafficCameras[i] = trafficCamera;
            }

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_data_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);

            // lay it all out in recycler view
            cameraAdapter = new SDOTCameraAdapter(trafficCameras);
            recyclerView.setAdapter(cameraAdapter);

            // click on item, sends camera info for selection
            cameraAdapter.setListener(new SDOTCameraAdapter.Listener() {
                @Override
                public void onClick(int pos) {
                    Log.d(TAG, trafficCameras[pos].getId() + " clicked = " + trafficCameras[pos].getDescription());

                    Intent intent = new Intent(getApplicationContext(), MappedOut.class);
                    String[] cameraMeta = new String[4];
                    cameraMeta[0] = trafficCameras[pos].getDescription();
                    cameraMeta[1] = Double.toString(trafficCameras[pos].getLatitude());
                    cameraMeta[2] = Double.toString(trafficCameras[pos].getLongitude());
                    cameraMeta[3] = trafficCameras[pos].getImageUrl();
                    intent.putExtra(CAM_INFO, cameraMeta);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public static TextView getTextLocation() {
        return textLocation;
    }


}
