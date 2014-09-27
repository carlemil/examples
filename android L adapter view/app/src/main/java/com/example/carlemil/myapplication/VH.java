package com.example.carlemil.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by carlemil on 2014-09-26.
 */
public class VH extends RecyclerView.ViewHolder {

    public TextView cv;

    public VH(View itemView) {
        super(itemView);
        cv = (TextView)itemView.findViewById(R.id.item_view);
    }
}
