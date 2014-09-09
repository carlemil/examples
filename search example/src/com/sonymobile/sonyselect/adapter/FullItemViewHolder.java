package com.sonymobile.sonyselect.adapter;

import android.view.View;
import android.widget.TextView;

import com.sonymobile.sonyselect.components.RelativeHeightImageView;

public class FullItemViewHolder extends AbstractItemViewHolder {
    private final TextView genre;
    private final TextView description;

    public FullItemViewHolder(RelativeHeightImageView image, TextView title, View color, TextView genre,
                       TextView description) {
        super(image, title, color);
        this.genre = genre;
        this.description = description;
        if (this.description != null) {
            this.description.setSelected(false);
        }
    }

    public TextView getGenre() {
        return genre;
    }

    public TextView getDescription() {
        return description;
    }
}

