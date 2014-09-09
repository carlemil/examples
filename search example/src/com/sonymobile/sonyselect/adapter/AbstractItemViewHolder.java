package com.sonymobile.sonyselect.adapter;

import android.view.View;
import android.widget.TextView;

import com.sonymobile.sonyselect.components.RelativeHeightImageView;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractItemViewHolder implements NetworkImageViewHolder, ItemViewHolder {
    private final RelativeHeightImageView image;
    private final TextView title;
    private final View color;
    private List<ImageLink> itemImageLinks = new ArrayList<ImageLink>();

    protected AbstractItemViewHolder(RelativeHeightImageView image, TextView title, View color) {
        this.image = image;
        this.title = title;
        if (this.title != null) {
            // force selection to force scrolling.
            this.title.setSelected(false);
        }
        this.color = color;
    }

    @Override
    public RelativeHeightImageView getImage() {
        return image;
    }

    @Override
    public TextView getTitle() {
        return title;
    }

    @Override
    public View getColor() {
        return color;
    }

    @Override
    public List<ImageLink> getItemImageLinks() {
        return itemImageLinks;
    }

    @Override
    public void setItemImageLinks(List<ImageLink> itemImageLinks) {
        this.itemImageLinks = itemImageLinks;
    }

}
