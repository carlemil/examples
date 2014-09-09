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
import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SearchView.OnSuggestionListener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.tagmanager.Container;
import com.google.tagmanager.ContainerOpener;
import com.google.tagmanager.ContainerOpener.OpenType;
import com.google.tagmanager.TagManager;
import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.adapter.SuggestionsAdapter;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.net.VolleySingelton;
import com.sonymobile.sonyselect.net.domain.RootResponse;
import com.sonymobile.sonyselect.net.domain.SearchKillSwitchResponse;
import com.sonymobile.sonyselect.net.domain.SuggestionsResponse;
import com.sonymobile.sonyselect.util.StringUtil;
import com.sonymobile.sonyselect.util.TagManagerContainerConstants;

public abstract class AbstractSearchActivity extends AbstractSyncAwareActivity {

    private static final String LOG_TAG = AbstractSearchActivity.class.getCanonicalName();

    private final Gson mGson = new Gson();

    volatile private static Container container = null;

    private SuggestionsAdapter suggestionsAdapter = null;

    private SearchView searchView;

    public static final String PREF_SEARCH_ENABLED = "pref_search_kill_switch";

    private static final String[] COLUMNS = {
            BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
    };

    protected String suggestionsUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TagManager tagManager = TagManager.getInstance(this);

        ContainerOpener.openContainer(tagManager, SonySelectApplication.CONTAINER_ID,
                OpenType.PREFER_NON_DEFAULT, null, new ContainerOpener.Notifier() {
                    @Override
                    public void containerAvailable(Container container) {
                        Log.d(LOG_TAG, "Got container data");
                        AbstractSearchActivity.container = container;
                        invalidateOptionsMenu();
                        if (container.getBoolean(TagManagerContainerConstants.SEARCH_ENABLED)) {
                            requestSearchRoot();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean searchEnabled = true;
        if (sp.contains(PREF_SEARCH_ENABLED)) {
            searchEnabled = sp.getBoolean(PREF_SEARCH_ENABLED, true);
        }
        Log.d(LOG_TAG, "onCreateOptionsMenu searchEnabled: " + searchEnabled);
        if (searchEnabled) {
            /**
             * Only add the search button to the menu if we already got a
             * response from the TagManager and search should be enabled.
             */
            if (container != null
                    && container.getBoolean(TagManagerContainerConstants.SEARCH_ENABLED)) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.actionbar_menu, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean searchEnabled = true;
        if (sp.contains(PREF_SEARCH_ENABLED)) {
            searchEnabled = sp.getBoolean(PREF_SEARCH_ENABLED, true);
        }
        Log.d(LOG_TAG, "onPrepareOptionsMenu searchEnabled: " + searchEnabled);
        if (searchEnabled) {
            /**
             * Only add the search button to the menu if we already got a
             * response from the TagManager and search should be enabled.
             */
            if (container != null
                    && container.getBoolean(TagManagerContainerConstants.SEARCH_ENABLED)) {
                searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                MatrixCursor cursor = getCursor(new ArrayList<String>());
                suggestionsAdapter = new SuggestionsAdapter(getActionBar().getThemedContext(),
                        cursor);
                searchView.setSuggestionsAdapter(suggestionsAdapter);
                searchView.setOnSuggestionListener(onSuggestionListener);
                searchView.setOnQueryTextListener(onQueryTextListener);
            }
        }
        return true;
    }

    private void requestSearchRoot() {
        String url = getContainer().getString(TagManagerContainerConstants.SEARCH_ROOT_URL);
        VolleySingelton.getInstance().getSearchApi().getRoot(rootListener, rootErrorListener, url);
    }

    Listener<RootResponse> rootListener = new Listener<RootResponse>() {
        @Override
        public void onResponse(RootResponse root) {
            suggestionsUrl = root.getSuggestionUrl();
        }
    };

    ErrorListener rootErrorListener = new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.w(LOG_TAG, "Failed to fetch search root (no suggestions will be given)");

            if (volleyError != null && volleyError.networkResponse != null) {
                if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_GONE) {
                    /**
                     * If we got a 410 / HTTP_GONE response, the search service
                     * have been discontinued for some reason and we should not
                     * show the search symbol in the options menu anymore.
                     */
                    String jsonString = new String(volleyError.networkResponse.data);
                    SearchKillSwitchResponse searchKillSwitch = mGson.fromJson(jsonString,
                            SearchKillSwitchResponse.class);

                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(AbstractSearchActivity.this);
                    sp.edit().putBoolean(AbstractSearchActivity.PREF_SEARCH_ENABLED,//
                            !searchKillSwitch.systemShutdown).commit();

                    /**
                     * Since we might have a new value set for the kill switch,
                     * we will ask the options menu to re-create itself.
                     */
                    AbstractSearchActivity.this.invalidateOptionsMenu();

                    Log.d(LOG_TAG, "Search ErrorResponse: " + jsonString);
                } else {
                    String jsonString = new String(volleyError.networkResponse.data);
                    Log.d(LOG_TAG, "Search ErrorResponse: " + jsonString);
                }
            }

        }
    };

    OnSuggestionListener onSuggestionListener = new OnSuggestionListener() {
        @Override
        public boolean onSuggestionSelect(int position) {
            return false;
        }

        @Override
        public boolean onSuggestionClick(int position) {
            Cursor c = (Cursor) suggestionsAdapter.getItem(position);
            String query = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
//            Toast.makeText(AbstractSearchActivity.this, "Suggestion clicked: " + query,
//                    Toast.LENGTH_LONG).show();
            searchView.setQuery(query, false);
            return true;
        }
    };

    OnQueryTextListener onQueryTextListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            long minSuggestionsLength = getContainer().getLong(
                    TagManagerContainerConstants.SEARCH_MIN_SUGGESTION_LENGTH);
            if (query.length() >= minSuggestionsLength && !StringUtil.isEmpty(suggestionsUrl)) {
                ErrorListener errorListener = new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, "Error while fetching suggestions", error);
                    }
                };
                Listener<SuggestionsResponse> successListener = new Listener<SuggestionsResponse>() {
                    @Override
                    public void onResponse(SuggestionsResponse suggestionsResponse) {
                        MatrixCursor cursor = getCursor(suggestionsResponse.suggestions);
                        suggestionsAdapter.changeCursor(cursor);
                        suggestionsAdapter.notifyDataSetChanged();
                    }
                };

                VolleySingelton.getInstance().getSearchApi()
                        .getSuggestions(successListener, errorListener, suggestionsUrl, query);
            }
            return false;
        }
    };

    private MatrixCursor getCursor(List<String> suggestions) {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        int i = 1;
        for (String suggestion : suggestions) {
            cursor.addRow(new String[] {
                    String.valueOf(i), suggestion
            });
        }
        return cursor;
    }

    public static Container getContainer() {
        return container;
    }

}
