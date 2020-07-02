package dev.taupier.android.seawatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MappedOut extends FragmentActivity implements OnMapReadyCallback {

    private final static String TAG = "GOOGLE_MAP_FRAGMENT : ";
    private GoogleMap mMap;
    private FusedLocationProviderClient mLocationClient;
    private boolean mLocationPermissionGranted = false;
    private static final String ERROR_MSG = "PLAY SERVICES NOT AVAILABLE";

    public MappedOut() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mapped_out);

        // Location of client
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        getLocation();

        // camera info
        Intent intent = getIntent();
        if (intent != null) {
            String[] cameraMeta = intent.getStringArrayExtra(MainActivity.CAM_INFO);
            Double latitude = Double.parseDouble(cameraMeta[2]);
            Double longitude = Double.parseDouble(cameraMeta[1]);
            String name = cameraMeta[0];
            String url = cameraMeta[3];

            Log.e(TAG,  name + latitude.toString() + longitude.toString() + url);

            LatLng coordinates = new LatLng(latitude, longitude);

            mMap.addMarker(
                    new MarkerOptions().position(coordinates).title(name));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }



    @SuppressLint("MissingPermission")
    private void getLocation() {

        if (mLocationPermissionGranted) {
            try {
                Task location = mLocationClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location actualLocation = task.getResult();

                        if (actualLocation != null) {
                            String latlong = String.format("Lat: %f, Long: %f",
                                    actualLocation.getLatitude(),
                                    actualLocation.getLongitude());
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);

                            // Update the map with current location
                            LatLng currentLocation
                                    = new LatLng(actualLocation.getLatitude(),
                                    actualLocation.getLongitude());
                            mMap.addMarker(
                                    new MarkerOptions()
                                            .position(currentLocation)
                                            .title("WHERE I BE"));
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(currentLocation, 10));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                        } else {
                            Log.e(TAG, "Location is null ... you are nowhere.");
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            // requestCode: request ID
            case 1: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getLocation();
                }
            }
        }

    }

    private String getGeocoderLocation(Location location) {
        String geoLoc = "";

        // check outliers
        if (location == null) {
            Log.e(TAG, "Null location... Eaten by black hole...?");
            return geoLoc;
        }
        if (!Geocoder.isPresent()) {
            Log.e(TAG, "Lost in space. Geocoder unavailable at this moment.");
            return geoLoc;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                                               location.getLongitude(),
                                                    1);
            StringBuilder sb = new StringBuilder();

            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i) + "\n");
                }

                sb.append(address.getAddressLine(0) + "\n");
                sb.append(address.getLocality() + "\n");
                sb.append(address.getPostalCode() + "\n");
                sb.append(address.getCountryName() + "\n");
            }

            geoLoc = sb.toString();

        } catch (IOException e) {
            Log.e(TAG, "Error fetching data with geocoder. Go home. Sleep it off. ", e);
        }

        return geoLoc;

    }


}
