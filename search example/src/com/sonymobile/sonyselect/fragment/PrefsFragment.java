
package com.sonymobile.sonyselect.fragment;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.api.content.DatabaseConnection;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.internal.util.DeviceInfo;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

public class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    private static final String INPUT_A_SERVER_URL = "Input a server url to use when syncing, this will override other server url selections.";

    private static final String LOG_TAG = PrefsFragment.class.getCanonicalName();

    private static final String PREF_TYPE = "pref_type";

    private static final String PREF_MODEL = "pref_model";

    private static final String PREF_SPN = "pref_spn";

    private static final String PREF_MNC = "pref_mnc";

    private static final String PREF_MCC = "pref_mcc";

    private static final String PREF_CLIENT_VERSION = "pref_client_version";

    private static final String PREF_CLIENT_NAME = "pref_client_name";

    public static final String PREF_SERVER_URI_LIST = "pref_server_uri_list";

    public static final String PREF_SERVER_URI_EDITTEXT = "pref_server_uri_edittext";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(com.sonymobile.sonyselect.R.xml.preferences);

        ListPreference serverUrlPreference = (ListPreference) findPreference(PREF_SERVER_URI_LIST);
        updateServerUriListPref(serverUrlPreference, PREF_SERVER_URI_LIST);

        EditTextPreference serverUrlEdittextPreference = (EditTextPreference) findPreference(PREF_SERVER_URI_EDITTEXT);
        updateEditTextPreference(serverUrlEdittextPreference,
                INPUT_A_SERVER_URL);

        EditTextPreference clientNamePreference = (EditTextPreference) findPreference(PREF_CLIENT_NAME);
        updateEditTextPreference(clientNamePreference, getString(R.string.client_name));

        EditTextPreference clientVersionPreference = (EditTextPreference) findPreference(PREF_CLIENT_VERSION);
        updateEditTextPreference(clientVersionPreference, SonySelectApplication.getVersionName());

        DeviceInfo deviceInfo = new DeviceInfo(getActivity());

        EditTextPreference mccPreference = (EditTextPreference) findPreference(PREF_MCC);
        updateEditTextPreference(mccPreference, deviceInfo.getMcc());

        EditTextPreference mncPreference = (EditTextPreference) findPreference(PREF_MNC);
        updateEditTextPreference(mncPreference, deviceInfo.getMnc());

        EditTextPreference spnPreference = (EditTextPreference) findPreference(PREF_SPN);
        updateEditTextPreference(spnPreference, deviceInfo.getSpn());

        EditTextPreference modelPreference = (EditTextPreference) findPreference(PREF_MODEL);
        updateEditTextPreference(modelPreference, deviceInfo.getModel());

        EditTextPreference typePreference = (EditTextPreference) findPreference(PREF_TYPE);
        updateEditTextPreference(typePreference, deviceInfo.getType());
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        ListPreference listPreference = (ListPreference) findPreference(PREF_SERVER_URI_LIST);
        if (listPreference.getKey().equals(key)) {
            updateServerUriListPref(listPreference, PREF_SERVER_URI_LIST);
            // Clear DB to force a sync on next startup.
            DatabaseConnection.reset(getActivity(), SonySelectApplication.AUTHORITY);
        }

        EditTextPreference serverUrlEdittextPreference = (EditTextPreference) findPreference(PREF_SERVER_URI_EDITTEXT);
        if (serverUrlEdittextPreference.getKey().equals(key)) {
            updateEditTextPreference(serverUrlEdittextPreference, INPUT_A_SERVER_URL);
        }

        EditTextPreference clientNamePreference = (EditTextPreference) findPreference(PREF_CLIENT_NAME);
        if (clientNamePreference.getKey().equals(key)) {
            updateEditTextPreference(clientNamePreference, getString(R.string.client_name));
        }

        EditTextPreference clientVersionPreference = (EditTextPreference) findPreference(PREF_CLIENT_VERSION);
        if (clientVersionPreference.getKey().equals(key)) {
            updateEditTextPreference(clientVersionPreference,
                    SonySelectApplication.getVersionName());
        }

        DeviceInfo deviceInfo = new DeviceInfo(getActivity());

        EditTextPreference mccPreference = (EditTextPreference) findPreference(PREF_MCC);
        if (mccPreference.getKey().equals(key)) {
            updateEditTextPreference(mccPreference, deviceInfo.getMcc());
        }

        EditTextPreference mncPreference = (EditTextPreference) findPreference(PREF_MNC);
        if (mncPreference.getKey().equals(key)) {
            updateEditTextPreference(mncPreference, deviceInfo.getMnc());
        }

        EditTextPreference spnPreference = (EditTextPreference) findPreference(PREF_SPN);
        if (spnPreference.getKey().equals(key)) {
            updateEditTextPreference(spnPreference, deviceInfo.getSpn());
        }

        EditTextPreference modelPreference = (EditTextPreference) findPreference(PREF_MODEL);
        if (modelPreference.getKey().equals(key)) {
            updateEditTextPreference(modelPreference, deviceInfo.getModel());
        }

        EditTextPreference typePreference = (EditTextPreference) findPreference(PREF_TYPE);
        if (typePreference.getKey().equals(key)) {
            updateEditTextPreference(typePreference, deviceInfo.getType());
        }
    }

    private void updateEditTextPreference(EditTextPreference editTextPreference, String defaultValue) {
        if (!TextUtils.isEmpty(editTextPreference.getText())) {
            editTextPreference.setSummary(editTextPreference.getText());
        } else {
            editTextPreference.setSummary(defaultValue);
        }
    }

    private void updateServerUriListPref(ListPreference listPreference, String key) {
        if (!TextUtils.isEmpty(listPreference.getEntry())) {
            // Set the stored uri.
            String value = getPreferenceManager().getSharedPreferences().getString(key, "");
            listPreference.setSummary(value);
        } else {
            // Set the default uri from config.xml
            listPreference.setSummary(INPUT_A_SERVER_URL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()//
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().//
                unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
