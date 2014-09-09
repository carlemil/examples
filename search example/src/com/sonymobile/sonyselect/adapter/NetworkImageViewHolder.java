package com.sonymobile.sonyselect.adapter;

import com.sonymobile.sonyselect.components.RelativeHeightImageView;

import java.util.List;

public interface NetworkImageViewHolder {
    public RelativeHeightImageView getImage();

    public List<ImageLink> getItemImageLinks();

    public void setItemImageLinks(List<ImageLink> itemImageLinks);
}
