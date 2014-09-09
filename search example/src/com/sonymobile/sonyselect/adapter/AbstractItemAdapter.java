package com.sonymobile.sonyselect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sonymobile.sonyselect.domain.GooglePlayItem;

abstract class AbstractItemAdapter extends BaseAdapter {
    protected final LayoutInflater inflater;
    protected GooglePlayItem[] items;

    public AbstractItemAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.items = new GooglePlayItem[] {};
    }

    abstract public void bindView(View view, GooglePlayItem item);

    @Override
    public int getCount() {
        return items != null ? items.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return position >= 0 && position < getCount() ? items[position] : null;
    }

    @Override
    public long getItemId(int position) {
        GooglePlayItem item = (GooglePlayItem) getItem(position);
        return item != null ? item.id : -1L;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(parent);
        }

        GooglePlayItem item = (GooglePlayItem) getItem(position);
        bindView(convertView, item);

        return convertView;
    }

    abstract public View newView(ViewGroup parent);

    public void setItems(GooglePlayItem[] items) {
        this.items = items;
        notifyDataSetChanged();
    }
    
    public int getNumberOfItems(){
        return items.length;
    }
}
