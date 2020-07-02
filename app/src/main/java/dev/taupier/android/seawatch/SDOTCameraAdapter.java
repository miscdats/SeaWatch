package dev.taupier.android.seawatch;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SDOTCameraAdapter extends RecyclerView.Adapter<SDOTCameraAdapter.ViewHolder>
        implements LoaderManager.LoaderCallbacks<String> {

    private final static String TAG = "CAM_ADAPTER: ";
    private String SDOTBaseUrl = "http://www.seattle.gov/trafficcams/images/";
    private String WSDOTBaseUrl = "http://images.wsdot.wa.gov/nw/";
    private TrafficCamera[] trafficCameras;
    private Listener listener ;

    public SDOTCameraAdapter(TrafficCamera[] trafficCameras) {
        this.trafficCameras = trafficCameras;
    }

    @NonNull
    @Override
    public SDOTCameraAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_recycler, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int pos) {
        CardView cardView = viewHolder.cameraSelected;

        TextView cameraInfo = (TextView)cardView.findViewById(R.id.text_results);
        ImageView imageView = (ImageView)cardView.findViewById(R.id.image_camera);

        TrafficCamera trafficCamera = trafficCameras[pos];
        cameraInfo.setText(trafficCamera.getDescription());
        Picasso.get().load(trafficCamera.getImageUrl()).into(imageView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return trafficCameras.length;
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cameraSelected;

        public ViewHolder(CardView view) {
            super(view);
            cameraSelected = itemView.findViewById(R.id.card_view);

        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onClick(int pos);
    }

}
