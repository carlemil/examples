package com.sonymobile.sonyselect.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.bi.TrackableScreens;
import com.sonymobile.sonyselect.bi.Tracker;
import com.sonymobile.sonyselect.util.ThemeFinder;

public final class LicenseDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_license, null);
        TextView t = (TextView) view.findViewById(R.id.licenseView);

        t.setText(R.string.EasyTrackerLicenseText);
        t.setMovementMethod(LinkMovementMethod.getInstance());

        Tracker.getTracker().trackScreenView(TrackableScreens.LICENSE);

        int theme = ThemeFinder.getDialogThemeId(getResources());
        AlertDialog.Builder builder = new AlertDialog.Builder(context, theme);
        builder.setView(view);
        builder.setTitle(R.string.License);
        builder.setPositiveButton(R.string.StorePickerOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LicenseDialogFragment.this.dismiss();
            }
        });

        return builder.create();
    }
}
