
package com.sonymobile.sonyselect.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.api.content.DatabaseConnection;
import com.sonymobile.sonyselect.api.synchronization.SyncListener;
import com.sonymobile.sonyselect.api.synchronization.SyncManager;
import com.sonymobile.sonyselect.api.synchronization.SyncManager.SyncRequest;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.fragment.PrefsFragment;
import com.sonymobile.sonyselect.internal.net.SyncConfiguration;
import com.sonymobile.sonyselect.util.Settings;
import com.sonymobile.sonyselect.util.StringUtil;

public abstract class AbstractSyncAwareActivity extends Activity implements SyncListener {
    private static final String LOG_TAG = AbstractSyncAwareActivity.class.getName();

    private SyncManager syncManager;

    protected final boolean isTablet;

    public AbstractSyncAwareActivity() {
        isTablet = SonySelectApplication.isTablet();
    }

    protected void clearData() {
        DatabaseConnection.reset(this, SonySelectApplication.AUTHORITY);
        SyncConfiguration.reset(this);
        Settings.reset(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncManager = new SyncManager(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncManager.registerSyncListener(this);
    }

    @Override
    protected void onPause() {
        syncManager.unregisterSyncListener(this);
        super.onPause();
    }

    protected abstract void onSyncStarted();

    protected void requestSync(String channel) {
        Log.d(LOG_TAG, "Requesting sync. channel: " + channel);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String serverUri = sp.getString(PrefsFragment.PREF_SERVER_URI_EDITTEXT, null);
        if (TextUtils.isEmpty(serverUri)) {
            serverUri = sp.getString(PrefsFragment.PREF_SERVER_URI_LIST, null);
        }
        if (TextUtils.isEmpty(serverUri)) {
            serverUri = getString(R.string.server_uri);
        }
        Log.d(LOG_TAG, "Server URI: " + serverUri);

        String clientName = sp.getString("pref_client_name", null);
        if (TextUtils.isEmpty(clientName)) {
            clientName = getString(R.string.client_name);
        }

        String clientVersion = sp.getString("pref_client_version", null);
        if (TextUtils.isEmpty(clientVersion)) {
            clientVersion = SonySelectApplication.getVersionName();
        }

        String apiKey = getString(R.string.api_key);
        String[] contentTypes = getResources().getStringArray(R.array.content_types);
        String channelToUse;

        if (StringUtil.isEmpty(channel)) {
            channelToUse = getString(R.string.channel);
        } else {
            channelToUse = channel;
        }

        Log.v(LOG_TAG, "Creating sync request. rootUri: " + serverUri + " apiKey: " + apiKey
                + " clientName: " + clientName + " clientVersion: " + clientVersion + " Auth: "
                + SonySelectApplication.AUTHORITY + " channel: " + channelToUse
                + " Number of contentTypes: " + contentTypes.length);

        SyncRequest request = syncManager.createSyncRequest() //
                .withRootUrl(serverUri) //
                .withApiKey(apiKey) //
                .asClient(clientName, clientVersion) //
                .withAuthority(SonySelectApplication.AUTHORITY) //
                .withChannel(channelToUse) //
                .withContentTypes(contentTypes);

        syncManager.perform(request);
        onSyncStarted();
    }
}
