package com.sonymobile.sonyselect.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.api.content.UsageManager;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.bi.TrackableScreens;
import com.sonymobile.sonyselect.bi.Tracker;
import com.sonymobile.sonyselect.util.Settings;
import com.sonymobile.sonyselect.util.ThemeFinder;

public class PrivacyDialogFragment extends DialogFragment {
    private static final String LOG_TAG = PrivacyDialogFragment.class.getCanonicalName();

    private OnPrivacyDialogEventListener listener;

    private void setAcceptUsage(boolean doAccept) {
        if (isAdded()) {
            String serverUrl = getString(R.string.server_uri);
            String apiKey = getString(R.string.api_key);
            String clientName = getString(R.string.client_name);
            String clientVersion = SonySelectApplication.getVersionName();

            UsageManager setUsageManager = new UsageManager(getActivity());
            UsageManager.AcceptRequest request = setUsageManager //
                    .createSetAcceptRequest() //
                    .withRootUrl(serverUrl) //
                    .withApiKey(apiKey) //
                    .asClient(clientName, clientVersion) //
                    .setAcceptUsage(doAccept);

            setUsageManager.perform(request);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();

        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.dialog_privacy, null);

        CheckBox shareUsageData = (CheckBox) view.findViewById(R.id.info_share_usage_data);
        if (Settings.shareUsageDataExists(getActivity())) {
            shareUsageData.setChecked(Settings.isShareUsageDataAccepted(getActivity()));
        } else {
            shareUsageData.setChecked(false); // default should be false, the first time.
        }


        TextView infoShareUsageData = (TextView) view.findViewById(R.id.info_share_usage_data_text);
        Spanned shareUsageDataText = Html.fromHtml(context.getString(R.string.InfoShareUsageData));
        infoShareUsageData.setText(shareUsageDataText);
        enableLinksIfNetworkIsAvailable(context, infoShareUsageData);

        int theme = ThemeFinder.getDialogThemeId(getResources());

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, theme);
        builder.setView(view);
        builder.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CheckBox shareUsageData = (CheckBox) view.findViewById(R.id.info_share_usage_data);
                if (shareUsageData.isChecked()) {
                    Log.d(LOG_TAG, "Sharing usage data.");
                    Settings.setShareUsageDataAccepted(getActivity(), true);
                    setAcceptUsage(true);
                    Tracker.getTracker().trackScreenView(TrackableScreens.DISCLAIMER_ACCEPTED);
                } else {
                    Log.d(LOG_TAG, "Not sharing usage data.");
                    Settings.setShareUsageDataAccepted(getActivity(), false);
                    setAcceptUsage(false);
                    Tracker.getTracker().trackScreenView(TrackableScreens.DISCLAIMER_DECLINED);
                }
               PrivacyDialogFragment.this.dismiss();
               listener.onPrivacyDialogContinued();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!Settings.shareUsageDataExists(getActivity())) {
                        getActivity().finish();
                    }
                }
                return false;
            }

        });
        return dialog;
    }

    private void enableLinksIfNetworkIsAvailable(final Context context, TextView generalAgreement) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            generalAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            generalAgreement.setAutoLinkMask(0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnPrivacyDialogEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnPrivacyDialogEventListener");
        }
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        PrivacyDialogFragment.this.dismiss();
    }

    public interface OnPrivacyDialogEventListener {
        public void onPrivacyDialogContinued();
    }
}
