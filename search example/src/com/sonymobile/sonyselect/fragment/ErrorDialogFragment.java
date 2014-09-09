package com.sonymobile.sonyselect.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.api.content.Link;
import com.sonymobile.sonyselect.api.synchronization.SyncError;
import com.sonymobile.sonyselect.util.StringUtil;
import com.sonymobile.sonyselect.util.ThemeFinder;

public final class ErrorDialogFragment extends DialogFragment {

    private static final String EXTRA_SYNCERROR = "ErrorDialogFragment.EXTRA_SYNCERROR";
    private OnErrorDialogEventListener listener;

    public static ErrorDialogFragment newInstance(SyncError error) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(EXTRA_SYNCERROR, error);

        ErrorDialogFragment fragment = new ErrorDialogFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_error, null);
        final SyncError syncError = (SyncError) getArguments().getSerializable(EXTRA_SYNCERROR);
        String title = syncError.getEndUserTitle();
        String endUserMessage = syncError.getEndUserMessage();
        String errorMessage = syncError.getErrorMessage();
        TextView endUserMessageView = (TextView) view.findViewById(R.id.end_user_message);
        endUserMessageView.setText(endUserMessage);
        TextView errorMessageView = (TextView) view.findViewById(R.id.error_message);
        errorMessageView.setText(errorMessage);
        endUserMessageView.setMovementMethod(LinkMovementMethod.getInstance());
        errorMessageView.setMovementMethod(LinkMovementMethod.getInstance());
        ViewGroup contentView = (ViewGroup) view.findViewById(R.id.content_view);
        List<Link> links = syncError.getLinks();
        for (final Link link : links) {
            Button button = new Button(context);
            button.setText(link.title);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String url = link.href;
                    openUrl(url);
                }
            });
            contentView.addView(button);
        }

        int theme = ThemeFinder.getDialogThemeId(getResources());
        AlertDialog.Builder builder = new AlertDialog.Builder(context, theme);
        builder.setView(view);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.StorePickerOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ErrorDialogFragment.this.dismiss();
                listener.onErrorDialogClose();
            }
        });

        return builder.create();
    }

    protected void openUrl(String url) {
        if (!StringUtil.isEmpty(url)) {
            Uri uri = Uri.parse(url);

            if (StringUtil.isEmpty(uri.getScheme())) {
                uri = Uri.parse("http://" + url);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            dismiss();
            listener.onErrorDialogClose();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnErrorDialogEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDialogEventListener");
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
        listener.onErrorDialogClose();
    }
}
