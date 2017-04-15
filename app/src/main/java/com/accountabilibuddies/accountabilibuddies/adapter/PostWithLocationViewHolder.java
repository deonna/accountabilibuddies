package com.accountabilibuddies.accountabilibuddies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.accountabilibuddies.accountabilibuddies.R;
import com.google.android.gms.maps.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostWithLocationViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.mapview)
    MapView mapview;

    @BindView(R.id.tvAddress)
    TextView address;

    @BindView(R.id.ibLike)
    ImageButton postLike;

    @BindView(R.id.ibComment)
    ImageButton postComment;

    @BindView(R.id.tvLikes)
    TextView likesCount;

    public PostWithLocationViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public MapView getMapview() {
        return mapview;
    }

    public void setMapview(MapView mapview) {
        this.mapview = mapview;
    }

    public ImageButton getPostLike() {
        return postLike;
    }

    public void setPostLike(ImageButton postLike) {
        this.postLike = postLike;
    }

    public ImageButton getPostComment() {
        return postComment;
    }

    public void setPostComment(ImageButton postComment) {
        this.postComment = postComment;
    }

    public TextView getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(TextView likesCount) {
        this.likesCount = likesCount;
    }

    public TextView getAddress() {
        return address;
    }

    public void setAddress(TextView address) {
        this.address = address;
    }
}
