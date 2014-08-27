
package com.jayway.volleydemo.activitys;

import java.io.File;
import java.io.IOException;

import com.jayway.volleydemo.R;
import com.jayway.volleydemo.adapters.SectionsPagerAdapter;
import com.jayway.volleydemo.data.SyncHandler;
import com.jayway.volleydemo.data.SyncListener;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;

public class SlidingTabsActivity extends FragmentActivity implements SyncListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter sectionsPagerAdapter= new SectionsPagerAdapter(getSupportFragmentManager());

    private String LOG_TAG = SlidingTabsActivity.class.getCanonicalName();

    private SyncHandler syncHandler = new SyncHandler();

    long HTTP_CACHE_SIZE = 50 * 1024 * 1024;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_tabs_activity);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(sectionsPagerAdapter);

        PagerTabStrip titleStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
        titleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        try {
            File httpCacheDir = new File(getCacheDir(), "http_cache");
            HttpResponseCache.install(httpCacheDir, HTTP_CACHE_SIZE);
        } catch (IOException e) {
            Log.d(LOG_TAG, "Http response cache init failed: " + e);
        }

        syncHandler.Sync(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStop() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
        super.onStop();
    }

    @Override
    public void syncCompleted() {
        sectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void syncFailed() {
        Log.e(LOG_TAG, "Sync Failed!");
    }
}
