package com.sonymobile.sonyselect.fragment;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.activities.LaunchActivity;
import com.sonymobile.sonyselect.adapter.DetailPagerAdapter;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.domain.GooglePlayItem;
import com.sonymobile.sonyselect.domain.ItemUtil;
import com.sonymobile.sonyselect.receiver.BackgroundColorUpdateReceiver;
import com.sonymobile.sonyselect.util.StringUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

public class DetailsFragment extends Fragment {
    public static final String EXTRA_ITEM_ID = "com.sonymobile.sonyselect.EXTRA_ITEM_ID";
    public static final String EXTRA_LIST_ID = "com.sonymobile.sonyselect.EXTRA_LIST_ID";
    public static final String EXTRA_CHANNEL_NAME = "com.sonymobile.sonyselect.EXTRA_CHANNEL_NAME";
    public static final String EXTRA_FIRST_LIST = "com.sonymobile.sonyselect.EXTRA_FIRST_LIST";
    public static final long ALL_ITEMS = -1L;

    private static final String LOG_TAG = DetailsFragment.class.getName();

    private int currentPage = 0;
    private Timer timer;
    private ViewPager viewPager;
    private DetailPagerAdapter adapter;
    private int flip_delay;

    public long getCurrentItemId() {
        int position = viewPager.getCurrentItem();
        GooglePlayItem item = adapter.getItemAt(position);
        return item != null ? item.id : -1L;
    }

    public long getCurrentListId() {
        int position = viewPager.getCurrentItem();
        GooglePlayItem item = adapter.getItemAt(position);
        return item != null ? item.listId : -1L;
    }

    public String getCurrentItemDownloadUrl() {
        int position = viewPager.getCurrentItem();
        GooglePlayItem item = adapter.getItemAt(position);
        return item != null ? item.getLinkUrl("download") : null;
    }

    public GooglePlayItem getCurrentItem() {
        int position = viewPager.getCurrentItem();
        GooglePlayItem item = adapter.getItemAt(position);
        return item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Context context = getActivity();

        adapter = new DetailPagerAdapter(context);

        Resources resources = context.getResources();
        flip_delay = resources.getInteger(R.integer.flip_delay_millis);
    }

    public void setItems(GooglePlayItem[] items) {
        adapter.setItems(items);
        viewPager.removeAllViews();
        viewPager.setAdapter(adapter);
        updateBackgroundColor();
    }

    public int getItemCount() {
        return adapter.getCount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Resources resources = getActivity().getResources();
        int marginInPixels = resources.getDimensionPixelSize(R.dimen.detail_pager_image_margin);
        final int marginInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginInPixels * 2, resources.getDisplayMetrics());
        final float minAlpha = 0.4f;

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setPageMargin(-marginInDp);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageTransformer(false, new PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                View body = view.findViewById(R.id.body);
                body.setTranslationX(marginInDp * position);

                View image = view.findViewById(R.id.image);
                if (position < -1) {
                    image.setAlpha(0.0f);
                    body.setAlpha(0.0f);
                } else if (position <= 1) {
                    image.setAlpha(0.8f - Math.abs(position));
                    body.setAlpha(1.0f - Math.abs(position));
                } else {
                    image.setAlpha(minAlpha);
                    body.setAlpha(0.0f);
                }
            }
        });
        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    stopFlipping();
                }
                return false;
            }
        });
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                changeColorIfPageChanged(page);
            }

            @Override
            public void onPageScrolled(int page, float positionOffset, int arg2) {
                changeColorIfPageChanged((int) (page + 0.5f + positionOffset));
            }

            private void changeColorIfPageChanged(int page) {
                if (page != currentPage) {
                    Log.d(LOG_TAG, "Current page: " + currentPage + " new page: " + page);
                    currentPage = page;
                    updateBackgroundColor();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setAdapter(adapter);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        updateBackgroundColor();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPager.setAdapter(null);
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "Pausing app");
        stopFlipping();
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "Resuming app");
        super.onResume();
        startFlipping();
        updateBackgroundColor();
    }

    public void setCurrentItem(long itemId) {
        int position = 0;
        int maxPosition = 0;

        if (adapter != null) {
            maxPosition = adapter.getCount() - 1;
            long currentId = adapter.getIdForPosition(position);
            long requestedId = itemId;

            if (requestedId != ALL_ITEMS) {
                while (position <= maxPosition && currentId != requestedId) {
                    currentId = adapter.getIdForPosition(++position);
                }
            }

            if (viewPager != null) {
                currentPage = position > maxPosition ? 0 : position;
                viewPager.setCurrentItem(currentPage, true);
                viewPager.setSelected(true);
                updateBackgroundColor();
            }
        }
    }

    public void startFlipping() {
        Log.d(LOG_TAG, "Start flipping");
        if (SonySelectApplication.isTablet()) {
            if (timer == null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        gotoNextItem();
                    }
                }, flip_delay, flip_delay);
            }
        }
    }

    public void stopFlipping() {
        Log.d(LOG_TAG, "Stop flipping");
        if (SonySelectApplication.isTablet()) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
        }
    }

    private void gotoNextItem() {
        if (adapter != null && viewPager != null) {
            Log.d(LOG_TAG, "Goto next item. (flipping!)");
            Activity activity = getActivity();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int position = viewPager.getCurrentItem() + 1;
                    if (position >= adapter.getCount()) {
                        position = 0;
                    }
                    viewPager.setCurrentItem(position, true);
                }
            });
        }
    }

    private void updateBackgroundColor() {
        GooglePlayItem item = adapter.getItemAt(currentPage);
        String url = ItemUtil.getImageUrl(item, SonySelectApplication.get().getResources(), R.array.promo_link_rel);
        Log.d(LOG_TAG, "DetailsFragment.updateBackgroundColor() (sending intent) url: " + url);

        if (url != null && url.length() > 0) {

            Intent intent = new Intent(LaunchActivity.BACKGROUND_COLOR_UPDATE_LISTENER);
            // You can also include some extra data.
            intent.putExtra(BackgroundColorUpdateReceiver.BACKGROUND_COLOR_UPDATE_URL, url);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }

}
