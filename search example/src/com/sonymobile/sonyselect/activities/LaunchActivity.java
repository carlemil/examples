/*********************************************************************
 *       ____                      __  __       _     _ _            *
 *      / ___|  ___  _ __  _   _  |  \/  | ___ | |__ (_) | ___       *
 *      \___ \ / _ \| '_ \| | | | | \  / |/ _ \| '_ \| | |/ _ \      *
 *       ___) | (_) | | | | |_| | | |\/| | (_) | |_) | | |  __/      *
 *      |____/ \___/|_| |_|\__, | |_|  |_|\___/|_.__/|_|_|\___|      *
 *                         |___/                                     *
 *                                                                   *
 *********************************************************************
 *      Copyright 2013 Sony Mobile Communications AB.                *
 *      All rights, including trade secret rights, reserved.         *
 *********************************************************************/

package com.sonymobile.sonyselect.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;

import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.adapter.OnListClickListener;
import com.sonymobile.sonyselect.api.content.Contract;
import com.sonymobile.sonyselect.api.content.DatabaseConnection;
import com.sonymobile.sonyselect.api.content.DatabaseConnection.OnCursorLoadListener;
import com.sonymobile.sonyselect.api.content.ItemListInfo;
import com.sonymobile.sonyselect.api.synchronization.SyncError;
import com.sonymobile.sonyselect.api.synchronization.SyncResult;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.bi.TrackableScreens;
import com.sonymobile.sonyselect.bi.Tracker;
import com.sonymobile.sonyselect.components.BackgroundColorHelper;
import com.sonymobile.sonyselect.domain.GooglePlayItem;
import com.sonymobile.sonyselect.domain.ItemUtil;
import com.sonymobile.sonyselect.fragment.DetailsFragment;
import com.sonymobile.sonyselect.fragment.ErrorDialogFragment;
import com.sonymobile.sonyselect.fragment.LicenseDialogFragment;
import com.sonymobile.sonyselect.fragment.ListsFragment;
import com.sonymobile.sonyselect.fragment.OnErrorDialogEventListener;
import com.sonymobile.sonyselect.fragment.PrivacyDialogFragment;
import com.sonymobile.sonyselect.internal.util.Utils;
import com.sonymobile.sonyselect.listener.BGColorImageLoaded;
import com.sonymobile.sonyselect.receiver.BackgroundColorUpdateReceiver;
import com.sonymobile.sonyselect.util.CursorUtils;
import com.sonymobile.sonyselect.util.Settings;
import com.sonymobile.sonyselect.util.StringUtil;
import com.sonymobile.sonyselect.util.UiUtils;

public class LaunchActivity extends AbstractSearchActivity implements OnListClickListener,
        OnCursorLoadListener, PrivacyDialogFragment.OnPrivacyDialogEventListener,
        OnErrorDialogEventListener {
    private static final String EXTRA_LIST_FRAGMENT_VISIBILITY = "LaunchActivity.LIST_FRAGMENT_VISIBILITY";

    private static final String EXTRA_DETAILS_FRAGMENT_VISIBILITY = "LaunchActivity.DETAILS_FRAGMENT_VISIBILITY";

    private static final String EXTRA_LAST_REQUESTED_ITEM = "LaunchActivity.LAST_REQUESTED_ITEM";

    private static final String EXTRA_LAST_REQUESTED_LIST = "LaunchActivity.LAST_REQUESTED_LIST";

    private static final String LOG_TAG = LaunchActivity.class.getName();

    private long lastRequestedItem;

    private long lastRequestedList;

    private boolean wasListFragmentVisible;

    private boolean wasDetailsFragmentVisible;

    private DatabaseConnection databaseConnection;

    private View progressBar;

    private View errorMessage;

    private ImageButton retrybutton;

    private int groupTicket;

    private int featuredTicket;

    private int itemsTicket;

    private ListsFragment listsFragment;

    private DetailsFragment detailsFragment;

    private BackgroundColorHelper backgroundColorHelper;

    private ItemListInfo[] lists;

    private List<Integer> previewTickets;

    private List<Integer> expandTickets;

    private String channel;

    private int maxListCount;

    private int previewItemCount;

    private boolean showMoreMenuAlt;

    public static final String BACKGROUND_COLOR_UPDATE_LISTENER = "BACKGROUND_COLOR_UPDATE_LISTENER";

    private BackgroundColorUpdateReceiver mBackgroundColorUpdateReceiver;

    /**
     * This method is referred to directly from the XML-layout. Don't change the
     * signature of this method without making the corresponding changes to the
     * detail layout as well.
     */
    public void getItemButtonClicked(View view) {
        String url = detailsFragment.getCurrentItemDownloadUrl();

        if (!StringUtil.isEmpty(url)) {
            Uri uri = Uri.parse(url);

            if (StringUtil.isEmpty(uri.getScheme())) {
                uri = Uri.parse("http://" + url);
            }

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                GooglePlayItem item = detailsFragment.getCurrentItem();
                long listId = detailsFragment.getCurrentListId();
                int position = listsFragment.getListIdPositionMap().get(listId);
                String trackingName = lists[position].trackingName;
                Tracker.getTracker().trackItemGetButtonClick(trackingName, item);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Couldn't get item: " + uri.toString(), e);
            }
        }
    }

    /**
     * This method is referred to directly from the XML-layout. Don't change the
     * signature of this method without making the corresponding changes to the
     * detail layout as well.
     */
    public void shareItemButtonClicked(View view) {
        String url = detailsFragment.getCurrentItemDownloadUrl();

        if (!StringUtil.isEmpty(url)) {
            try {
                String shareText = getResources().getString(R.string.Share);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("text/plain");
                final String MARKET = "market://";
                if (url.startsWith(MARKET)) {
                    intent.putExtra(Intent.EXTRA_TEXT,
                            "https://play.google.com/store/apps/" + url.substring(MARKET.length()));
                } else {
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                }
                Intent chooserIntent = Intent.createChooser(intent, shareText);
                startActivity(chooserIntent);
                Log.d(LOG_TAG, "### share: -" + url + "-\n-" + shareText + "-\n");
                GooglePlayItem item = detailsFragment.getCurrentItem();
                long listId = detailsFragment.getCurrentListId();
                int position = listsFragment.getListIdPositionMap().get(listId);
                Tracker.getTracker().trackItemShareButtonClick(lists[position].trackingName, item);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Couldn't share item: " + url, e);
            }
        }
    }

    private void setupContentView() {
        // We inflate the view ourself because we need it further down to set
        // translucent status
        // and navigation bar on it.
        View view = View.inflate(this, R.layout.activity_launch, null);
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
        if (mpLp == null) {
            view.setLayoutParams((new MarginLayoutParams(this, null)));
            mpLp = (MarginLayoutParams) view.getLayoutParams();
        }
        mpLp.setMargins(0, statusBarHeight + actionBarHeight, 0, navigationBarHeight);

        backgroundColorHelper = new BackgroundColorHelper(view);
        backgroundColorHelper.setDefaultBackgroundColor();
        ImageListener mImageLoadedListener = new BGColorImageLoaded(getResources(), backgroundColorHelper);
        mBackgroundColorUpdateReceiver = new BackgroundColorUpdateReceiver();
        mBackgroundColorUpdateReceiver.setImageLoadedListener(mImageLoadedListener);

        progressBar = findViewById(R.id.progressbar);
        errorMessage = findViewById(R.id.errormessage);
        retrybutton = (ImageButton) findViewById(R.id.retrybutton);

        FragmentManager fragmentManager = getFragmentManager();
        listsFragment = (ListsFragment) fragmentManager.findFragmentById(R.id.lists);
        listsFragment.setAutoFlip(true);

        detailsFragment = (DetailsFragment) fragmentManager.findFragmentById(R.id.details);
        detailsFragment.startFlipping();

        databaseConnection = new DatabaseConnection(this, SonySelectApplication.AUTHORITY,
                getLoaderManager(), this);
        previewTickets = new ArrayList<Integer>();
        expandTickets = new ArrayList<Integer>();

        Resources resources = getResources();
        channel = resources.getString(R.string.channel);
        maxListCount = resources.getInteger(R.integer.list_count);
        previewItemCount = resources.getInteger(R.integer.preview_count);

        if (isTablet) {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            if (detailsFragment.isVisible()) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    @Override
    public void onPrivacyDialogContinued() {
        Log.v(LOG_TAG, "Privacy dialog continued");
        requestSync(channel);
    }

    @Override
    public void onBackPressed() {
        if (detailsFragment.isVisible() && !listsFragment.isVisible()) {
            showListsFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR + ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (savedInstanceState != null) {
            wasListFragmentVisible = savedInstanceState.getBoolean(EXTRA_LIST_FRAGMENT_VISIBILITY);
            wasDetailsFragmentVisible = savedInstanceState
                    .getBoolean(EXTRA_DETAILS_FRAGMENT_VISIBILITY);
            lastRequestedList = savedInstanceState.getLong(EXTRA_LAST_REQUESTED_LIST,
                    ListsFragment.ALL_LISTS);
            lastRequestedItem = savedInstanceState.getLong(EXTRA_LAST_REQUESTED_ITEM,
                    DetailsFragment.ALL_ITEMS);
        } else {
            wasListFragmentVisible = wasDetailsFragmentVisible = false;
            lastRequestedList = lastRequestedItem = 0;
        }

        setupContentView();

        retrybutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadContent();
            }
        });

        if (listsFragment.needsData()) {
            loadContent();
        } else {
            requestSync(channel);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
        Tracker.getTracker().trackScreenView(TrackableScreens.LAUNCH);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "Lifecycle: Resuming...");
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "BACKGROUND_COLOR_UPDATE_LISTENER".
        LocalBroadcastManager.getInstance(this).registerReceiver(mBackgroundColorUpdateReceiver,
                new IntentFilter(BACKGROUND_COLOR_UPDATE_LISTENER));

    }

    private void loadContent() {
        Log.d(LOG_TAG, "Loading content... channel: " + channel);
        errorMessage.setVisibility(View.GONE);
        retrybutton.setVisibility(View.GONE);

        if (Settings.shareUsageDataExists(this)) {
            startLoadingLists();
            requestSync(channel);
            // Execution continued in onSyncFinished or onSyncError
        } else {
            String fragmentTag = "dialog";
            PrivacyDialogFragment fragment = (PrivacyDialogFragment) getFragmentManager()
                    .findFragmentByTag(fragmentTag);
            // If the dialog is already visible, don't create a new fragment.
            if (fragment == null) {
                fragment = new PrivacyDialogFragment();
                fragment.show(getFragmentManager(), fragmentTag);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EXTRA_LIST_FRAGMENT_VISIBILITY, listsFragment.isVisible());
        outState.putBoolean(EXTRA_DETAILS_FRAGMENT_VISIBILITY, detailsFragment.isVisible());
        outState.putLong(EXTRA_LAST_REQUESTED_LIST, detailsFragment.getCurrentListId());
        outState.putLong(EXTRA_LAST_REQUESTED_ITEM, detailsFragment.getCurrentItemId());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG, "Lifecycle: Pausing...");
        super.onPause();
        progressBar.setVisibility(View.GONE);
        backgroundColorHelper.stop();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBackgroundColorUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        /**
         * This statement controls when the "more" menu alternative is shown.
         * For the "more" menu to be shown, we need either: to be on a phone and
         * be viewing the list fragment (the details fragment should hide the
         * "more" menu) to be a tablet. we must have more lists than the
         * maxListCount, regardless of being a phone or tablet.
         */
        boolean shouldPopulateOverflow = (lists != null && lists.length > maxListCount && showMoreMenuAlt);

        MenuItem listsOverflowAction = menu.getItem(0);
        listsOverflowAction.setVisible(shouldPopulateOverflow);
        SubMenu subMenu = listsOverflowAction.getSubMenu();
        subMenu.clear();

        if (shouldPopulateOverflow) {
            int size = lists.length;
            for (int i = 0; i < size; i++) {
                subMenu.addSubMenu(Menu.NONE, R.id.overflow, i, lists[i].title);
            }
        }

        new HandleSettingsAltInMenuTask().setMenu(menu).execute();

        super.onCreateOptionsMenu(menu);

        return true;
    }

    /**
     * Show the Settings alternative in the menu if "pref_settings_in_actionbar"
     * set to true.
     */
    class HandleSettingsAltInMenuTask extends AsyncTask<Void, Void, Boolean> {
        private Menu menu = null;

        public HandleSettingsAltInMenuTask setMenu(Menu menu) {
            this.menu = menu;
            return this;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(LaunchActivity.this);
            boolean showSettings = sp.getBoolean("pref_settings_in_actionbar", false);
            return showSettings;
        }

        @Override
        protected void onPostExecute(Boolean showSettings) {
            if (showSettings) {
                menu.add(Menu.NONE, R.id.settings, Menu.NONE, "Debug settings");
            }
            super.onPostExecute(showSettings);
        }
    }

    @Override
    public void onCursorLoaded(int loaderId, Cursor cursor) {
        if (!CursorUtils.isEmpty(cursor)) {
            Log.v(LOG_TAG, "CursorLoaded");
            progressBar.setVisibility(View.GONE);
            if (isTablet) {
                showTabletUI();
            } else {
                showListsFragment();
            }
        }
        try {
            if (loaderId == groupTicket) {
                groupTicket = -1;
                prepareLists(cursor);
            } else if (loaderId == featuredTicket) {
                featuredTicket = -1;
                prepareFeaturedItems(cursor);
            } else if (loaderId == itemsTicket) {
                itemsTicket = -1;
                prepareDetailItems(cursor);
            } else if (previewTickets.remove(Integer.valueOf(loaderId))) {
                preparePreviewItems(cursor);
            } else if (expandTickets.remove(Integer.valueOf(loaderId))) {
                prepareExpandItems(cursor);
            } else {
                Log.w(LOG_TAG, "Unexpected Loader ID: " + loaderId);
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed loading cursor in launch activity. ", e);
        } finally {
            Utils.closeSilently(cursor);
        }
    }

    @Override
    public void onItemClick(long listId, long itemId, int itemPosition, int numberOfItems) {
        detailsFragment.stopFlipping();
        showList(listId, itemId, itemPosition, numberOfItems);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showListsFragment();
                return true;
            case R.id.overflow:
                int position = item.getOrder();
                if (position >= 0 && position < lists.length) {
                    long id = lists[position].id;
                    if (id > 0) {
                        startLoadingItems(id);
                        detailsFragment.startFlipping();
                    } else if (id == ListsFragment.ALL_LISTS) {
                        showListsFragment();
                    }
                }
                return true;
            case R.id.settings:
                Intent i = new Intent(this, SecretCodeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                return true;
            case R.id.license:
                LicenseDialogFragment licenseFragment = new LicenseDialogFragment();
                licenseFragment.show(getFragmentManager(), "dialog");
                return true;
            case R.id.disclaimer:
                PrivacyDialogFragment fragment = new PrivacyDialogFragment();
                fragment.show(getFragmentManager(), "dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSyncFinished(SyncResult result) {
        Log.d(LOG_TAG, "SyncFinished: Time to next pending list resolution: " + result.getDelay()
                + " second(s), SyncResult: " + result);
        if (result == SyncResult.SUCCESS) {
            if (result.getDelay() == -1) {
                startLoadingLists();
            }
        } else if (listsFragment.needsData()
                && (result == SyncResult.ERROR || result == SyncResult.FAILURE)) {
            showErrorMessage();
        }
    }

    @Override
    public void onSyncError(SyncError error) {
        progressBar.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        retrybutton.setVisibility(View.GONE);

        Log.w(LOG_TAG, "SyncError, getErrorMessage: " + error.getErrorMessage()
                + ", getEndUserMessage: " + error.getEndUserMessage() + ", getEndUserTitle: "
                + error.getEndUserTitle() + ", getSystemShutdown: " + error.getSystemShutdown());

        String systemShutdown = error.getSystemShutdown();
        if ("true".equals(systemShutdown)) {
            ErrorDialogFragment dialogFragment = ErrorDialogFragment.newInstance(error);
            dialogFragment.show(getFragmentManager(), "dialog");
        } else if (lists == null || lists.length == 0) {
            showErrorMessage();
        }
    }

    @Override
    public void onErrorDialogClose() {
        clearData();
    }

    @Override
    public void onSyncStarted() {
        Log.d(LOG_TAG, "SyncStarted");
        if (listsFragment.needsData()) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void prepareLists(Cursor cursor) {
        if (CursorUtils.notEmpty(cursor) && cursor.moveToFirst()) {
            lists = DatabaseConnection.parseAllItemLists(cursor);
            listsFragment.buildListIdPositionMap(lists);

            List<ItemListInfo> allLists = Arrays.asList(lists);

            int count = Math.min(maxListCount, allLists.size());
            int startAt = isTablet ? 0 : 1;

            if (count > startAt) {
                ItemListInfo[] visibleLists = new ItemListInfo[count - startAt];
                int size = visibleLists.length;
                for (int i = startAt, j = 0; i < count && j < size; i++, j++) {
                    visibleLists[j] = allLists.get(i);
                }
                listsFragment.setLists(visibleLists);
            }

            ItemListInfo featured = lists[0];
            featuredTicket = databaseConnection.loadItemsCursor(featured.id);
            // Execution continued after a while in onCursorLoaded.
        }
    }

    private void prepareFeaturedItems(Cursor cursor) {
        if (CursorUtils.notEmpty(cursor) && cursor.moveToFirst()) {
            int size = lists.length;
            int firstList;

            GooglePlayItem[] items = DatabaseConnection.parseAllItems(cursor, GooglePlayItem.class);
            if (isTablet) {
                firstList = 0;
                detailsFragment.setItems(items);
            } else {
                firstList = 1;
                listsFragment.setFeaturedItems(items);
            }

            for (int i = firstList; i < size && i < maxListCount; i++) {
                long listId = lists[i].id;
                int ticket = databaseConnection.loadSubsetItemsCursor(listId, previewItemCount, 0);
                previewTickets.add(Integer.valueOf(ticket));
                // Execution continued after a while in onCursorLoaded.
            }
        }
    }

    private void preparePreviewItems(Cursor cursor) {
        if (CursorUtils.notEmpty(cursor) && cursor.moveToFirst()) {
            int column = cursor.getColumnIndex(Contract.ItemTable.Projection.LIST_ID);
            long listId = column != -1 ? cursor.getLong(column) : -1L;

            if (listId != -1L) {
                GooglePlayItem[] items = DatabaseConnection.parseAllItems(cursor,
                        GooglePlayItem.class);
                listsFragment.setPreviewItems(listId, items);

                int ticket = databaseConnection.loadSubsetItemsCursor(listId, -1, previewItemCount);
                expandTickets.add(Integer.valueOf(ticket));
                // Execution continued in onCursorLoaded.
            }
        }
    }

    private void prepareExpandItems(Cursor cursor) {
        if (CursorUtils.notEmpty(cursor) && cursor.moveToFirst()) {
            int column = cursor.getColumnIndex(Contract.ItemTable.Projection.LIST_ID);
            long listId = column != -1 ? cursor.getLong(column) : -1L;

            if (listId != -1L) {
                GooglePlayItem[] items = DatabaseConnection.parseAllItems(cursor,
                        GooglePlayItem.class);
                listsFragment.setExpandItems(listId, items);
            }
        }

        if (previewTickets.isEmpty() && expandTickets.isEmpty()) {
            if (wasDetailsFragmentVisible) {
                startLoadingItems(lastRequestedList);
            } else if (wasListFragmentVisible) {
                showListsFragment();
            }
        }
    }

    private void prepareDetailItems(Cursor cursor) {
        if (CursorUtils.notEmpty(cursor) && cursor.moveToFirst()) {
            GooglePlayItem[] items = DatabaseConnection.parseAllItems(cursor, GooglePlayItem.class);
            detailsFragment.setItems(items);
            detailsFragment.setCurrentItem(lastRequestedItem);
            showDetailsFragment();
        }
    }

    private void startLoadingLists() {
        Log.d(LOG_TAG, "Start loading lists");

        groupTicket = databaseConnection.loadNonEmptyListsCursor(channel);
        // Execution continued in onCursorLoaded
    }

    private void startLoadingItems(long listId) {
        Log.d(LOG_TAG, "Start loading items. listId: " + listId);
        itemsTicket = databaseConnection.loadItemsCursor(listId);
        // Execution continued in onCursorLoaded
    }

    private void showTabletUI() {
        progressBar.setVisibility(View.GONE);
        Log.d(LOG_TAG, "showTabletUI");

        if (isTablet && (detailsFragment.isHidden() || listsFragment.isHidden())) {
            showMoreMenuAlt = true;
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager //
                    .beginTransaction() //
                    .show(listsFragment) //
                    .show(detailsFragment) //
                    .commitAllowingStateLoss();

            invalidateOptionsMenu();
        }
    }

    private void showDetailsFragment() {
        Log.d(LOG_TAG, "showDetailsFragment");
        progressBar.setVisibility(View.GONE);

        if (!isTablet && (detailsFragment.isHidden() || listsFragment.isVisible())) {
            showMoreMenuAlt = false;
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager //
                    .beginTransaction() //
                    .hide(listsFragment) //
                    .show(detailsFragment) //
                    .commitAllowingStateLoss();

            getActionBar().setDisplayHomeAsUpEnabled(true);
            invalidateOptionsMenu();
        }
    }

    private void showListsFragment() {
        progressBar.setVisibility(View.GONE);
        Log.d(LOG_TAG, "showListsFragment");

        if (!isTablet && (detailsFragment.isVisible() || listsFragment.isHidden())) {
            showMoreMenuAlt = true;
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager //
                    .beginTransaction() //
                    .hide(detailsFragment) //
                    .show(listsFragment) //
                    .commitAllowingStateLoss();

            getActionBar().setDisplayHomeAsUpEnabled(false);
            invalidateOptionsMenu();
        }
    }

    private void showErrorMessage() {
        Log.d(LOG_TAG, "Showing error message and retry button.");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager //
                .beginTransaction() //
                .hide(listsFragment) //
                .hide(detailsFragment) //
                .commitAllowingStateLoss();

        getActionBar().setDisplayHomeAsUpEnabled(false);
        invalidateOptionsMenu();

        progressBar.setVisibility(View.GONE);
        errorMessage.setVisibility(View.VISIBLE);
        retrybutton.setVisibility(View.VISIBLE);
    }

    private void showList(long listId, long itemId, int itemPosition, int numberOfItems) {
        if (listsFragment.getListIdPositionMap().indexOfKey(listId) >= 0) {
            lastRequestedItem = itemId;
            if (lastRequestedList != listId) {
                lastRequestedList = listId;
                startLoadingItems(listId);
            } else {
                detailsFragment.setCurrentItem(lastRequestedItem);
                showDetailsFragment();
            }
            updateBackgroundColor(listId, itemId);

            if (itemPosition > -1 && listsFragment.getListIdPositionMap() != null && lists != null) {
                int listPosition = listsFragment.getListIdPositionMap().get(listId);
                GooglePlayItem currentItem = listsFragment.getItem(listId, itemId);
                Tracker.getTracker().trackItemClickedInList(lists[listPosition].trackingName,
                        currentItem, listPosition, itemPosition, lists.length, numberOfItems);
            }
        }
    }

    private void updateBackgroundColor(long listId, long itemId) {
        GooglePlayItem item = listsFragment.getItem(listId, itemId);
        String url = ItemUtil.getImageUrl(item, SonySelectApplication.get().getResources(),
                R.array.promo_link_rel);
        Log.d(LOG_TAG, "DetailsFragment.updateBackgroundColor() url: " + url);

        if (url != null && url.length() > 0) {

            Intent intent = new Intent(LaunchActivity.BACKGROUND_COLOR_UPDATE_LISTENER);
            // You can also include some extra data.
            intent.putExtra(BackgroundColorUpdateReceiver.BACKGROUND_COLOR_UPDATE_URL, url);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
