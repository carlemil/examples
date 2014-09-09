/*********************************************************************
 *       ____                      __  __       _     _ _            *
 *      / ___|  ___  _ __  _   _  |  \/  | ___ | |__ (_) | ___       *
 *      \___ \ / _ \| '_ \| | | | | \  / |/ _ \| '_ \| | |/ _ \      *
 *       ___) | (_) | | | | |_| | | |\/| | (_) | |_) | | |  __/      *
 *      |____/ \___/|_| |_|\__, | |_|  |_|\___/|_.__/|_|_|\___|      *
 *                         |___/                                     *
 *                                                                   *
 *********************************************************************
 *      Copyright 2014 Sony Mobile Communications AB.                *
 *      All rights, including trade secret rights, reserved.         *
 *********************************************************************/

package com.sonymobile.sonyselect.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.listener.NextPageListener;
import com.sonymobile.sonyselect.net.VolleySingelton;
import com.sonymobile.sonyselect.net.domain.Item;
import com.sonymobile.sonyselect.net.domain.Link;
import com.sonymobile.sonyselect.util.StringUtil;

public class SearchResultAdapter extends BaseAdapter {

    // private static final int MAX_DESC_LENGTH = 160;

    // The amount of items left below screen in items list before we try to
    // pre-load more via the next link.
    private static final int PRELOAD_ITEM_LIMIT = 20;

    private List<Item> items = new ArrayList<Item>();

    protected final LayoutInflater inflater;

    private NextPageListener nextPageListener;

    public SearchResultAdapter(Context context, NextPageListener searchResultListener) {
        this.inflater = LayoutInflater.from(context);
        this.nextPageListener = searchResultListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder vh;
        if (view == null) {
            view = inflater.inflate(R.layout.search_result_item, null);
            vh = new ViewHolder();
            vh.title = (TextView) view.findViewById(R.id.title);
            // vh.description = (TextView) view.findViewById(R.id.description);
            vh.icon = (NetworkImageView) view.findViewById(R.id.image);
            vh.sonySelectAppIcon = (ImageView) view.findViewById(R.id.select_app_icon);
            view.setTag(R.id.search_view_holder_tag, vh);
        } else {
            vh = (ViewHolder) view.getTag(R.id.search_view_holder_tag);
        }

        // Trigger a fetch of the next url if we have less than one page of data
        // before hitting the bottom of the list.
        if (getCount() - position < PRELOAD_ITEM_LIMIT) {
            nextPageListener.getNextPage();
        }

        Item item = (Item) getItem(position);
        if (item != null) {

            vh.title.setText(item.title);

            // TODO to long description causes very bad lag in list while
            // flinging :(
            // if (item.descriptionHtml != null) {
            // int index = item.descriptionHtml.indexOf(" ", MAX_DESC_LENGTH);
            // if (index < item.descriptionHtml.length() + 1 && index != -1) {
            // index = Math.min(item.descriptionHtml.length(), index + 1);
            // item.descriptionHtml = item.descriptionHtml.substring(0, index);
            // }
            // vh.description.setText(item.descriptionHtml + "…");
            // }

            ImageLoader imageLoader = VolleySingelton.getInstance().getImageLoader();
            vh.icon.setDefaultImageResId(R.drawable.ic_launcher);
            String iconUrl = Link.getLinkUrl("iconUrl", item.links);
            if (!StringUtil.isEmpty(iconUrl)) {
                vh.icon.setImageUrl(iconUrl, imageLoader);
            }
            if (item.sonySelect) {
                vh.sonySelectAppIcon.setVisibility(View.VISIBLE);
            } else {
                vh.sonySelectAppIcon.setVisibility(View.INVISIBLE);
            }
            view.setTag(R.id.search_url_key, Link.getLinkUrl("marketUrl", item.links));
        } else {
            vh.title.setText("N/A");
        }
        return view;
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<Item> content) {
        items.addAll(content);
    }

    static class ViewHolder {
        TextView title;

        ImageView sonySelectAppIcon;

        // TextView description;

        NetworkImageView icon;
    }

}
