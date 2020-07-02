package com.example.deya.seawatch;


import android.content.Context;
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
    private Context context;
    private ClickListener clickListener ;
    private LongClickListener longClickListener;

    public SDOTCameraAdapter(TrafficCamera[] trafficCameras) {
        this.trafficCameras = trafficCameras;
    }

    @NonNull
    @Override
    public SDOTCameraAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_recycler, viewGroup, false);
        return new ViewHolder(cardView, i);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int pos) {
        CardView cardView = viewHolder.cameraSelected;
        Context context = cardView.getContext();

        TextView cameraInfo = (TextView)cardView.findViewById(R.id.text_results);
        ImageView imageView = (ImageView)cardView.findViewById(R.id.image_camera);

        final TrafficCamera trafficCamera = trafficCameras[pos];

        cameraInfo.setText(trafficCamera.getDescription());
        Picasso.get().load(trafficCamera.getImageUrl()).into(imageView);
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

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private CardView cameraSelected;
        private int position;

        public ViewHolder(View itemView, int position) {
            super(itemView);
            cameraSelected = itemView.findViewById(R.id.card_view);
            this.position = position;

            if (clickListener != null) {
                itemView.setOnClickListener(this);
            }
            if (longClickListener != null) {
                itemView.setOnLongClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                position = getAdapterPosition();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (longClickListener != null) {
                position = getAdapterPosition();
                return true;
            } else {
                return false;
            }
        }
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnLongClickListener(LongClickListener longItemClickListener) {
        this.longClickListener = longItemClickListener;
    }

    public interface ClickListener {
        void onClick(int pos);
    }

    public interface LongClickListener {
        void onLongClick(int pos);
    }
}
