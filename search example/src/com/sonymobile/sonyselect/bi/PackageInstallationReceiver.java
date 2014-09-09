
package com.sonymobile.sonyselect.bi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * A broadcast listener that listens for Installs of applications in the phone.
 */
public class PackageInstallationReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = PackageInstallationReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context c, Intent i) {
        final int NOTHING_INSTALLED = -1;
        final int uid = i.getIntExtra(Intent.EXTRA_UID, NOTHING_INSTALLED);
        final boolean isInstallation = uid != NOTHING_INSTALLED;
        final boolean isReplacing = i.getBooleanExtra(Intent.EXTRA_REPLACING, false);

        if (isInstallation) {
            PackageManager pm = c.getPackageManager();
            String name = pm.getNameForUid(uid);
            if (!isReplacing) {
                Log.d(LOG_TAG, "New package installed with name '" + name + "'");
                startDeviceUsageService(c, uid, name);
            } else {
                Log.d(LOG_TAG, "Ignoring installation of '" + name
                        + "' since it is replacing an existing package.");
            }
        } else {
            Log.d(LOG_TAG, "Ignoring installation since package could not be identified.");
        }
    }

    private void startDeviceUsageService(Context context, int packageUid, String name) {
        try {
            Intent service = new Intent(context, PackageInstallationService.class);
            service.putExtra(PackageInstallationService.ACTION_NAME,
                    PackageInstallationService.PACKAGE_INSTALLED_ACTION);
            service.putExtra(PackageInstallationService.PACKAGE_UID_EXTRA, packageUid);
            service.putExtra(PackageInstallationService.PACKAGE_NAME, name);
            context.startService(service);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception: " + e.getMessage(), e);
        }

    }

}
