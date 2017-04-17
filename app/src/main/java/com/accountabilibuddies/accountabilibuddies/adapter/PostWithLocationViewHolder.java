package com.accountabilibuddies.accountabilibuddies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.accountabilibuddies.accountabilibuddies.R;
import com.google.android.gms.maps.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostWithLocationViewHolder extends PostViewHolder {

    @BindView(R.id.mapview)
    MapView mapview;

    @BindView(R.id.tvAddress)
    TextView address;

    public PostWithLocationViewHolder(View itemView) {
        super(itemView);
    }

    public MapView getMapview() {
        return mapview;
    }

    public void setMapview(MapView mapview) {
        this.mapview = mapview;
    }

    public TextView getAddress() {
        return address;
    }

    public void setAddress(TextView address) {
        this.address = address;
    }
}
