package com.sonymobile.sonyselect.fragment;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterViewFlipper;
import android.widget.ExpandableListView;

import com.android.volley.RequestQueue;
import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.activities.LaunchActivity;
import com.sonymobile.sonyselect.adapter.FeaturedAdapter;
import com.sonymobile.sonyselect.adapter.ListsAdapter;
import com.sonymobile.sonyselect.adapter.OnListClickListener;
import com.sonymobile.sonyselect.api.content.ItemListInfo;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.domain.GooglePlayItem;
import com.sonymobile.sonyselect.domain.ItemUtil;
import com.sonymobile.sonyselect.net.VolleySingelton;
import com.sonymobile.sonyselect.receiver.BackgroundColorUpdateReceiver;

public class ListsFragment extends Fragment {
    public static final long ALL_LISTS = -1L;
    private static final String LOG_TAG = ListsFragment.class.getCanonicalName();

    private OnListClickListener itemClickListener;

    private AdapterViewFlipper featuredView;
    private ExpandableListView listsView;
    private FeaturedAdapter featuredAdapter;
    private ListsAdapter listsAdapter;

    private boolean shouldFlipAutomatically;

    private LongSparseArray<Integer> listIdPositionMap;

    public LongSparseArray<Integer> buildListIdPositionMap(ItemListInfo[] lists) {
        listIdPositionMap = new LongSparseArray<Integer>(50);
        listIdPositionMap.put(ListsFragment.ALL_LISTS, -1);

        for (int position = 0; position < lists.length; position++) {
            listIdPositionMap.put(lists[position].id, position);
        }
        return listIdPositionMap;
    }

    public LongSparseArray<Integer> getListIdPositionMap() {
        return listIdPositionMap;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            itemClickListener = (OnListClickListener) activity;
            if (featuredAdapter != null) {
                featuredAdapter.setOnItemClickListener(itemClickListener);
            }
            if (listsAdapter != null) {
                listsAdapter.setOnListClickListener(itemClickListener);
            }
        } catch (ClassCastException e) {
            String parent = activity.getClass().getName();
            String contract = OnListClickListener.class.getName();
            throw new IllegalArgumentException(parent + " doesn't implement " + contract, e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        shouldFlipAutomatically = false;

        Context context = getActivity();
        featuredAdapter = new FeaturedAdapter(context, R.layout.featured_item);
        featuredAdapter.setOnItemClickListener(itemClickListener);

        listsAdapter = new ListsAdapter(context);
        listsAdapter.setOnListClickListener(itemClickListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        featuredView = (AdapterViewFlipper) inflater.inflate(R.layout.featured_flipper, null, false);
        featuredView.setAdapter(featuredAdapter);

        View view = inflater.inflate(R.layout.fragment_lists, null, false);
        listsView = (ExpandableListView) view.findViewById(R.id.listview);
        listsView.addHeaderView(featuredView);
        listsView.setAdapter(listsAdapter);
        listsView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                RequestQueue queue = VolleySingelton.getInstance().getRequestQueue();
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    queue.start();
                } else {
                    queue.stop();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            stopFlipping();
        } else if (shouldFlipAutomatically) {
            startFlipping();
        }
        updateBackgroundColor();
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "Lifecycle: Pausing...");
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "Lifecycle: Resuming...");
        updateBackgroundColor();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        featuredView.setAdapter(null);
    }

    private String getFeaturedImageUrl() {
        if (featuredView != null && featuredView.getChildCount() > 0 && featuredAdapter != null) {
            int currentPosition = featuredView.getDisplayedChild();
            GooglePlayItem item = (GooglePlayItem) featuredAdapter.getItem(currentPosition);
            String url = ItemUtil.getImageUrl(item, SonySelectApplication.get().getResources(), R.array.promo_link_rel);
            return url;
        }
        return null;
    }

    public void setAutoFlip(boolean shouldFlip) {
        shouldFlipAutomatically = shouldFlip;
    }

    public void setExpandItems(long listId, GooglePlayItem[] items) {
        if (listId > 0L && items != null && items.length > 0) {
            listsAdapter.setExpandItems(listId, items);
        }
    }

    public void setFeaturedItems(GooglePlayItem[] items) {
        if (items != null && items.length > 0) {
            featuredAdapter.setItems(items);
            featuredView.setAdapter(featuredAdapter);
        }
    }

    public void setLists(ItemListInfo[] lists) {
        listsAdapter.setLists(lists);
    }

    public void setLists(List<ItemListInfo> lists) {
        if (lists != null) {
            setLists(lists.toArray(new ItemListInfo[0]));
        }
    }

    public void setPreviewItems(long listId, GooglePlayItem[] items) {
        if (listId > 0L && items != null && items.length > 0) {
            listsAdapter.setPreviewItems(listId, items);
        }
    }

    private void startFlipping() {
        if (featuredView != null && !featuredView.isFlipping()) {
            featuredView.startFlipping();
        }
    }

    private void stopFlipping() {
        if (featuredView != null && featuredView.isFlipping()) {
            featuredView.stopFlipping();
        }
    }

    private void updateBackgroundColor() {
        String url = getFeaturedImageUrl();
        if (url != null && url.length() > 0) {

            Intent intent = new Intent(LaunchActivity.BACKGROUND_COLOR_UPDATE_LISTENER);
            // You can also include some extra data.
            intent.putExtra(BackgroundColorUpdateReceiver.BACKGROUND_COLOR_UPDATE_URL, url);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }

    public GooglePlayItem getItem(long listId, long itemId) {
        GooglePlayItem item = listsAdapter.getItem(listId, itemId);
        if (item == null) {
            int currentPosition = featuredView.getDisplayedChild();
            item = (GooglePlayItem) featuredAdapter.getItem(currentPosition);
        }
        return item;
    }

    public boolean needsData() {
        return listsAdapter == null || listsAdapter.getGroupCount() == 0;
    }

}
