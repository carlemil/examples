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

package com.sonymobile.sonyselect.activities;

import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.adapter.SearchResultAdapter;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.listener.NextPageListener;
import com.sonymobile.sonyselect.net.VolleySingelton;
import com.sonymobile.sonyselect.net.domain.Link;
import com.sonymobile.sonyselect.net.domain.RootResponse;
import com.sonymobile.sonyselect.net.domain.SearchKillSwitchResponse;
import com.sonymobile.sonyselect.net.domain.SearchResponse;
import com.sonymobile.sonyselect.util.StringUtil;
import com.sonymobile.sonyselect.util.TagManagerContainerConstants;
import com.sonymobile.sonyselect.util.UiUtils;

public class SearchResultsActivity extends Activity implements NextPageListener {

    private static final String LOG_TAG = SearchResultsActivity.class.getCanonicalName();

    private GridView gridView;

    private String query;

    private String nextUrl = null;

    private AtomicBoolean readyToFetchNextPage = new AtomicBoolean(false);

    private final Gson mGson = new Gson();

    public SearchResultAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        setupContentView();

        if (!SonySelectApplication.isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
                    + ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }

        gridView = (GridView) findViewById(R.id.searchResultGridView);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                Log.d(LOG_TAG, "Clicked in list");
                String url = (String) view.getTag(R.id.search_url_key);
                if (!StringUtil.isEmpty(url)) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Url for item is empty.");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupContentView() {
        // We inflate the view ourself because we need it further down to set
        // translucent status and navigation bar on it.
        View view = View.inflate(this, R.layout.activity_search_results, null);
        setContentView(view);

        // Set the status and navigation bar translucent.
        UiUtils.setSystemUiTranslucent(view, this);

        // With translucent bars the root view will cover the whole screen and
        // the bars will be drawn on top of the root view. We want the root view
        // (background) to cover the whole screen and be drawn behind the
        // translucent bars but all other view objects within the root view
        // should not be drawn behind the bars so we set top and bottom margins
        // on the root view here.
        int statusBarHeight = UiUtils.getStatusBarHeight();
        int navigationBarHeight = UiUtils.getNavigationBarHeight();
        int actionBarHeight = UiUtils.getActionBarHeight(this);
        MarginLayoutParams mpLp = (MarginLayoutParams) view.getLayoutParams();
        mpLp.setMargins(0, statusBarHeight + actionBarHeight, 0, navigationBarHeight);

        // Set the background gradient to the default blue color
        Drawable drawable = view.getResources().getDrawable(R.drawable.bg);
        int color = getResources().getColor(R.color.default_background_color);
        drawable.setColorFilter(color, Mode.OVERLAY);
        view.setBackground(drawable);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            if (adapter == null) {
                adapter = new SearchResultAdapter(SearchResultsActivity.this,
                        SearchResultsActivity.this);
            }

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String clientName = sp.getString("pref_client_name", null);
            if (TextUtils.isEmpty(clientName)) {
                clientName = getString(R.string.client_name);
            }
            String clientVersion = sp.getString("pref_client_version", null);
            if (TextUtils.isEmpty(clientVersion)) {
                clientVersion = SonySelectApplication.getVersionName();
            }
            String client = clientName + ":" + clientVersion;
            String model = Build.MODEL;

            searchFor(client, model);
        }
    }

    public void searchFor(String client, String model) {
        adapter.clearItems();
        String url = AbstractSearchActivity.getContainer().getString(
                TagManagerContainerConstants.SEARCH_ROOT_URL);
        doRootRequest(url);
    }

    private void doRootRequest(String url) {
        VolleySingelton.getInstance().getSearchApi()
                .getRoot(rootListener, searchErrorListener, url);
    }

    Listener<RootResponse> rootListener = new Listener<RootResponse>() {
        @Override
        public void onResponse(RootResponse root) {
            Log.d(LOG_TAG, "got search root response");
            doSearchRequest(root.getSearchUrl());
            hideErrorMessage();
            findViewById(R.id.search_progress).setVisibility(View.GONE);
        }
    };

    ErrorListener searchErrorListener = new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if (volleyError != null && volleyError.networkResponse != null) {
                if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_GONE) {
                    // If we got a 410 / HTTP_GONE response, the search service
                    // have been discontinued for some reason and we should not
                    // show the search symbol in the options menu anymore.
                    String jsonString = new String(volleyError.networkResponse.data);
                    SearchKillSwitchResponse searchKillSwitch = mGson.fromJson(jsonString,
                            SearchKillSwitchResponse.class);

                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(SearchResultsActivity.this);
                    sp.edit().putBoolean(AbstractSearchActivity.PREF_SEARCH_ENABLED,//
                            !searchKillSwitch.systemShutdown).commit();

                    // Since we might have a new value set for the kill switch,
                    // we will ask the options menu to re-create itself.
                    SearchResultsActivity.this.invalidateOptionsMenu();

                    Log.d(LOG_TAG, "Search ErrorResponse: " + jsonString);
                } else {
                    String jsonString = new String(volleyError.networkResponse.data);
                    Log.d(LOG_TAG, "Search ErrorResponse: " + jsonString);
                }
            }

            // Unless the response code is 410 / HTTP_GONE show a toast telling
            // the user something went wrong and check if the adapter have any
            // content, if the adapter is empty just finish the activity and
            // drop back to the previous activity.
            if (!(volleyError != null && volleyError.networkResponse != null && volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_GONE)) {
                // Toast.makeText(
                // SearchResultsActivity.this,
                // SearchResultsActivity.this.getResources()
                // .getString(R.string.NoContentError),
                // Toast.LENGTH_LONG).show();
                showErrormessage(SearchResultsActivity.this.getResources().getString(
                        R.string.NoContentError));
            }
            findViewById(R.id.search_progress).setVisibility(View.GONE);
        }
    };

    private void showErrormessage(String message) {
        TextView responseText = (TextView) findViewById(R.id.search_response_text);
        responseText.setText(message);
        responseText.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        TextView responseText = (TextView) findViewById(R.id.search_response_text);
        responseText.setVisibility(View.GONE);
    }

    private void doSearchRequest(String searchUrl) {
        VolleySingelton.getInstance().getSearchApi()
                .getSearch(searchListener, searchErrorListener, searchUrl, query);
    }

    Listener<SearchResponse> searchListener = new Listener<SearchResponse>() {
        @Override
        public void onResponse(SearchResponse response) {
            hideErrorMessage();
            if (response.content.isEmpty()) {
                showErrormessage(SearchResultsActivity.this.getResources().getString(
                        R.string.EmptyResultError));
            } else {
                nextUrl = Link.getLinkUrl("next", response.links);
                if (!StringUtil.isEmpty(nextUrl)) {
                    readyToFetchNextPage.lazySet(true);
                }

                findViewById(R.id.search_progress).setVisibility(View.GONE);

                if (gridView.getAdapter() == null) {
                    gridView.setAdapter(adapter);
                }
                gridView.setVisibility(View.VISIBLE);

                adapter.addItems(response.content);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void getNextPage() {
        if (readyToFetchNextPage.compareAndSet(true, false)) {
            Log.d(LOG_TAG, "getNextPage: " + nextUrl);
            VolleySingelton.getInstance().getSearchApi()
                    .getNextPage(searchListener, searchErrorListener, nextUrl, query);
            nextUrl = null;
        }
    }

}
