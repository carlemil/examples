package com.sonymobile.sonyselect.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.activities.LaunchActivity;
import com.sonymobile.sonyselect.adapter.FeaturedAdapter;
import com.sonymobile.sonyselect.adapter.OnListClickListener;
import com.sonymobile.sonyselect.domain.GooglePlayItem;
import com.sonymobile.sonyselect.domain.ItemUtil;
import com.sonymobile.sonyselect.receiver.BackgroundColorUpdateReceiver;
import com.sonymobile.sonyselect.util.StringUtil;

public class CameraFragment extends Fragment {

    private GridView gridView;
    private GooglePlayItem[] items;
    private FeaturedAdapter adapter;
    private OnListClickListener itemClickListener;

    public GooglePlayItem getItem(long itemId) {
        if (items != null) {
            for (GooglePlayItem item : items) {
                if (item.id == itemId) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            itemClickListener = (OnListClickListener) activity;
        } catch (ClassCastException e) {
            String parent = activity.getClass().getName();
            String contract = OnListClickListener.class.getName();
            throw new IllegalArgumentException(parent + " doesn't implement " + contract, e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        adapter = new FeaturedAdapter(context, R.layout.featured_camera_item, false);
        adapter.setOnItemClickListener(itemClickListener);

        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        gridView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    updateBackgroundColor();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        return view;
    }

    public void setItems(GooglePlayItem[] items) {
        this.items = items;
        adapter.setItems(this.items);
        gridView.setAdapter(adapter);
    }

    public void updateBackgroundColor() {
        if (gridView != null && gridView.getChildCount() > 0 && adapter != null) {
            int currentTopPosition = gridView.getFirstVisiblePosition();
            if (currentTopPosition >= 0 && currentTopPosition < adapter.getCount()) {
                GooglePlayItem item = (GooglePlayItem) adapter.getItem(currentTopPosition);
                String url = ItemUtil.getImageUrl(item, getResources(), R.array.promo_link_rel);
                if (url != null && url.length() > 0) {

                    Intent intent = new Intent(LaunchActivity.BACKGROUND_COLOR_UPDATE_LISTENER);
                    // You can also include some extra data.
                    intent.putExtra(BackgroundColorUpdateReceiver.BACKGROUND_COLOR_UPDATE_URL, url);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                }
            }
        }
    }
}
