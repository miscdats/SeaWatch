package com.example.deya.seawatch;

import android.widget.TextView;

import static com.example.deya.seawatch.MainActivity.context;

public class TrafficCamera {

    private double latitude;
    private double longitude;
    private String id;
    private String description;
    private String imageUrl;
    private String type;


    public TrafficCamera(double latitude, double longitude,
                         String id, String description,
                         String imageUrl, String type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.description = description;
        this.type = type;
        setImageUrl(imageUrl);
    }

    public void setImageUrl(String imageUrl) {
        String SDOTBaseUrl = "http://www.seattle.gov/trafficcams/images/";
        String WSDOTBaseUrl = "http://images.wsdot.wa.gov/nw/";

        if (type.equals("sdot")) {
            this.imageUrl = SDOTBaseUrl + imageUrl;
        } else if (type.equals("wsdot")) {
            this.imageUrl = WSDOTBaseUrl + imageUrl;
        }
    }

    public void viewCoordinatesString(TextView textLocation) {
        String coordinatesString = context.getResources().getString(R.string.located_at);
        coordinatesString = String.format(coordinatesString, getLatitude(), getLongitude());
        textLocation.setText(coordinatesString);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
