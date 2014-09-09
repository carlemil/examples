package com.sonymobile.sonyselect.receiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.sonymobile.sonyselect.activities.ExternalActivity;

public final class CustomRequestReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = CustomRequestReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri contentUri = intent.getData();
        List<String> segments = contentUri.getPathSegments();
        Log.d(LOG_TAG, "Received intent for custom request. requestUrl:" + contentUri.toString());

        if (segments.size() >= 2) {
            String name = segments.get(0);

            if ("channel".equalsIgnoreCase(name)) {
                String channel = segments.get(1);

                Intent detailIntent = new Intent(context, ExternalActivity.class);
                detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                detailIntent.putExtra(ExternalActivity.EXTRA_CHANNEL_NAME, channel);

                if (segments.size() == 3) {
                    name = segments.get(2);

                    if ("first".equalsIgnoreCase(name)) {
                        detailIntent.putExtra(ExternalActivity.EXTRA_FIRST_LIST, true);
                    }
                }

                context.startActivity(detailIntent);
            }
        }
    }
}
