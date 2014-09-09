
package com.sonymobile.sonyselect.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sonymobile.sonyselect.activities.SecretCodeActivity;
import com.sonymobile.sonyselect.internal.util.DeviceInfo;

public class SecretCodeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DeviceInfo.isDebugEnabled()) {
            if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {
                Intent i = new Intent(context, SecretCodeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
