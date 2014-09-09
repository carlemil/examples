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

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;

import com.google.analytics.tracking.android.EasyTracker;
import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.adapter.OnListClickListener;
import com.sonymobile.sonyselect.api.content.DatabaseConnection;
import com.sonymobile.sonyselect.api.content.DatabaseConnection.OnCursorLoadListener;
import com.sonymobile.sonyselect.api.synchronization.SyncError;
import com.sonymobile.sonyselect.api.synchronization.SyncResult;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.bi.Tracker;
import com.sonymobile.sonyselect.components.BackgroundColorHelper;
import com.sonymobile.sonyselect.domain.GooglePlayItem;
import com.sonymobile.sonyselect.fragment.CameraFragment;
import com.sonymobile.sonyselect.fragment.ErrorDialogFragment;
import com.sonymobile.sonyselect.fragment.OnErrorDialogEventListener;
import com.sonymobile.sonyselect.fragment.PrivacyDialogFragment;
import com.sonymobile.sonyselect.util.CursorUtils;
import com.sonymobile.sonyselect.util.Settings;
import com.sonymobile.sonyselect.util.StringUtil;
import com.sonymobile.sonyselect.util.UiUtils;

public class ExternalActivity extends AbstractSyncAwareActivity implements OnListClickListener, OnCursorLoadListener, PrivacyDialogFragment.OnPrivacyDialogEventListener, OnErrorDialogEventListener {

    public static final String EXTRA_CHANNEL_NAME = "ExternalActivity.CHANNEL_NAME";
    public static final String EXTRA_FIRST_LIST = "ExternalActivity.FIRST_LIST";

    private static final String LOG_TAG = ExternalActivity.class.getName();

    private boolean hasContent = false;
    private int ticket;
    private String channel;
    private View progressBar;
    private View errorMessage;
    private ImageButton mRetryButton;
    private CameraFragment cameraFragment;
    private DatabaseConnection databaseConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We inflate the view ourself because we need it further down to set translucent status
        // and navigation bar on it.
        View view = View.inflate(this, R.layout.activity_external, null);
        setContentView(view);

        // Set the status and navigation bar translucent.
        UiUtils.setSystemUiTranslucent(view, this);

        // With translucent bars the root view will cover the whole screen and the bars will be
        // drawn on top of the root view. We want the root view (background) to cover the whole
        // screen and be drawn behind the translucent bars but all other view objects within the
        // root view should not be drawn behind the bars so we set top and bottom margins on the
        // root view here.
        int statusBarHeight = UiUtils.getStatusBarHeight();
        int navigationBarHeight = UiUtils.getNavigationBarHeight();
        int actionBarHeight = UiUtils.getActionBarHeight(this);
        MarginLayoutParams mpLp = (MarginLayoutParams)view.getLayoutParams();
        mpLp.setMargins(0, statusBarHeight+actionBarHeight, 0, navigationBarHeight);

        progressBar = findViewById(R.id.progressbar);
        errorMessage = findViewById(R.id.errormessage);
        mRetryButton = (ImageButton) findViewById(R.id.retrybutton);

        mRetryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessage.setVisibility(View.GONE);
                mRetryButton.setVisibility(View.GONE);
                requestSync(channel);
            }
        });

        View root = findViewById(R.id.external_parent);
        BackgroundColorHelper backgroundColorHelper = new BackgroundColorHelper(root);
        backgroundColorHelper.setDefaultBackgroundColor();

        FragmentManager fragmentManager = getFragmentManager();
        cameraFragment = (CameraFragment) fragmentManager.findFragmentById(R.id.camera);

        databaseConnection = new DatabaseConnection(this, SonySelectApplication.AUTHORITY, getLoaderManager(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
        Intent intent = getIntent();
        channel = intent.getStringExtra(EXTRA_CHANNEL_NAME);
        Tracker.getTracker().trackScreenView(channel);
    }

    @Override
    protected void onStop(){
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onCursorLoaded(int id, Cursor cursor) {
        showCameraFragment();

        if (id == ticket) {
            ticket = -1;
            hasContent = CursorUtils.notEmpty(cursor);
            GooglePlayItem[] items = DatabaseConnection.parseAllItems(cursor, GooglePlayItem.class);
            cameraFragment.setItems(items);
        }
    }

    @Override
    public void onPrivacyDialogContinued() {
        requestSync(channel);
    }

    @Override
    public void onErrorDialogClose() {
        clearData();
    }

    @Override
    public void onItemClick(long listId, long itemId, int itemPosition, int numberOfItems) {
        GooglePlayItem item = cameraFragment.getItem(itemId);
        String url = item != null ? item.getLinkUrl("download") : null;

        if (!StringUtil.isEmpty(url)) {
            Uri uri = Uri.parse(url);

            if (StringUtil.isEmpty(uri.getScheme())) {
                uri = Uri.parse("http://" + url);
            }

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Tracker.getTracker().trackItemGetButtonClick(channel, item);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Couldn't get item: " + uri.toString(), e);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        errorMessage.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);

        Intent intent = getIntent();
        channel = intent.getStringExtra(EXTRA_CHANNEL_NAME);
        boolean isFirst = intent.getBooleanExtra(EXTRA_FIRST_LIST, false);

        if (Settings.shareUsageDataExists(this)) {
            if (isFirst) {
                startLoadingContent();
                requestSync(channel);
                // Execution continued in onSyncFinished or onSyncError
            } else {
                finish();
            }
        } else {
            PrivacyDialogFragment fragment = new PrivacyDialogFragment();
            fragment.show(getFragmentManager(), "dialog");
        }
    }

    @Override
    public void onSyncError(SyncError error) {
        progressBar.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);

        String systemShutdown = error.getSystemShutdown();
        if ("true".equals(systemShutdown)) {
            ErrorDialogFragment dialogFragment = ErrorDialogFragment.newInstance(error);
            dialogFragment.show(getFragmentManager(), "dialog");
        } else {
            errorMessage.setVisibility(View.VISIBLE);
            mRetryButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSyncFinished(SyncResult result) {
        if (result == SyncResult.SUCCESS) {
            if (result.getDelay() == -1) {
                startLoadingContent();
            }
        } else if (!hasContent && (result == SyncResult.ERROR || result == SyncResult.FAILURE)) {
            showErrorMessage();
        }
    }

    @Override
    protected void onSyncStarted() {
        if (!hasContent) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void showCameraFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager //
                .beginTransaction() //
                .show(cameraFragment) //
                .commitAllowingStateLoss();

        progressBar.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);
    }

    private void showErrorMessage() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager //
                .beginTransaction() //
                .hide(cameraFragment) //
                .commitAllowingStateLoss();

        progressBar.setVisibility(View.GONE);
        errorMessage.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.VISIBLE);
    }

    private void startLoadingContent() {
        ticket = databaseConnection.loadFirstListItemsCursor(channel);
        // Execution continued in onCursorLoaded
    }
}
