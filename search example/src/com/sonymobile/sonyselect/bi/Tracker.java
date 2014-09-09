
package com.sonymobile.sonyselect.bi;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.sonymobile.sonyselect.bi.db.SqliteLogDb;
import com.sonymobile.sonyselect.bi.gahelper.GaHelper;
import com.sonymobile.sonyselect.bi.gahelper.GaHelperExceptionParser;
import com.sonymobile.sonyselect.domain.GooglePlayItem;
import com.sonymobile.sonyselect.internal.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Handles tracking of application data, to be sent to Google Analytics. An
 * instance of this is always created at application startup; use
 * {@link #getTracker()} to access the singleton instance throughout the code.
 * The actual dispatching of data is in turn handled by
 * {@link AnalyticsDispatcher}.
 *
 * @note Always use this class instead of directly interfacing with EasyTracker.
 */
public class Tracker {

    private static final String LOG_TAG = Tracker.class.getCanonicalName();

    private static final String DOWNLOAD_REL = "download";

    private static Tracker instance;

    private final Context context;

    public Tracker(final Context context, final EasyTracker easyTrackerInstance,
            final com.google.analytics.tracking.android.Tracker gaTracker) {
        this.context = context;

        instance = this;

        // First check if the user has opted out of tracking. This will result
        // in GA not sending any data.
        GaHelper.readAndSetGaEnabled(context);
        GaHelperExceptionParser.enableExceptionParsing(gaTracker, context);

        setGlobalCustomDimensionsToTrack(context);
    }

    public static Tracker getTracker() {
        return instance;
    }

    private void setGlobalCustomDimensionsToTrack(final Context context) {
        /* Log the platform build version */
        EasyTracker.getInstance(context).send(
                MapBuilder.createAppView()
                        .set(Fields.customDimension(CustomDimension.BUILD.getValue()), Build.ID)
                        .build());

        /* Log the device model name */
        EasyTracker.getInstance(context).send(
                MapBuilder.createAppView()
                        .set(Fields.customDimension(CustomDimension.MODEL.getValue()), Build.MODEL)
                        .build());

        /* Log the sample rate */
        EasyTracker.getInstance(context).send(
                MapBuilder
                        .createAppView()
                        .set(Fields.customDimension(CustomDimension.SAMPLE_RATE.getValue()),
                                EasyTracker.getInstance(context).get(Fields.SAMPLE_RATE)).build());

        /* Log the SIM MCC and MNC */
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            String network = tm.getSimOperator();
            if (network != null && network.length() >= 5) {
                EasyTracker.getInstance(context).send(
                        MapBuilder
                                .createAppView()
                                .set(Fields.customDimension(CustomDimension.NETWORK.getValue()),
                                        network.substring(0, 3) + '/' + network.substring(3))
                                .build());

            }
        }
    }

    /**
     * Tracks a click on the get button to open the item in external store.
     *
     * @param item the item to track.
     */
    public void trackItemGetButtonClick(String trackingName, GooglePlayItem item) {
        Log.d(LOG_TAG, "trackItemGetButtonClick");
        if (item != null) {
            Log.d(LOG_TAG, "trackingName: " + trackingName + " item: " + item.title);
            String type = item.type;
            String provider = item.provider;
            String title = item.title;
            String queryParam = composeDownloadQueryParam(item);

            trackScreenView(TrackableScreens.DETAIL, trackingName, type, TrackableEvents.VIEW,
                    queryParam);

            String packageName = item.packageName;
            if (packageName == null) {
                String downloadLink = item.getLinkUrl("download");
                int start = downloadLink.indexOf("?id=") + 4;
                int end = downloadLink.indexOf("&", start);
                if (end == -1) {
                    end = downloadLink.length();
                }
                packageName = downloadLink.substring(start, end);
                Log.i(LOG_TAG, "packageName from URL: " + packageName);
            } else {
                Log.i(LOG_TAG, "packageName from item: " + packageName);
            }

            new WriteGetClickToDatabase().execute(packageName);
            trackEvent(TrackableEvents.VIEW, trackingName, type, provider, title, queryParam);

            trackGetInstall(TrackableEvents.GET, packageName, 0L);
        }
    }

    /**
     * Perform the db write off the UI-thread.
     */
    class WriteGetClickToDatabase extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String packageName = params[0];
            SqliteLogDb db = new SqliteLogDb(context);
            db.open();
            long now = System.currentTimeMillis();
            db.insert(packageName, now);
            int n = db.deleteAllOldRows(now - TimeUnit.DAYS.toMillis(1l));
            Log.d(LOG_TAG, "Deleted " + n + " logentries that where getting old.");
            db.close();
            return null;
        }
    }

    /**
     * Tracks a click on the get button to open the item in external store.
     */
    public void trackApplicationInstall(String packageName, long timediff) {
        trackGetInstall(TrackableEvents.INSTALL, packageName, timediff);
    }

    /**
     * Tracks a click of an item in a list.
     *
     * @param base A conceptual name for the activity.
     * @param item The item being shown.
     */
    public void trackItemClickedInList(String trackingName, GooglePlayItem item, int listPosition,
            int itemPosition, int numberOfLists, int numberOfItems) {
        Log.d(LOG_TAG, "trackItemClickedInList");
        if (item != null) {
            Log.d(LOG_TAG, "trackingName: " + trackingName + " item: " + item.title + " provider: "
                    + item.provider + " listPosition: " + listPosition + " itemPosition: "
                    + itemPosition + " numberOfLists: " + numberOfLists + " numberOfItems: "
                    + numberOfItems);
            String type = item.type;
            String provider = item.provider;
            String title = item.title;

            String queryParam = composeDownloadQueryParam(item);

            trackScreenView(TrackableScreens.DETAIL, trackingName, type, TrackableEvents.DETAIL,
                    queryParam);

            // Don't log events if running in monkey tests.
            if (!ActivityManager.isUserAMonkey()) {
                StringBuilder category = new StringBuilder();
                category.append(trackingName);
                category.append('/').append(type);

                StringBuilder label = new StringBuilder();
                label.append(provider).append('/').append(title);
                String event = TrackableEvents.DETAIL;

                String action = fixString(event);
                MapBuilder mapBuilder = MapBuilder.createEvent(category.toString(), action,
                        label.toString(), null);

                mapBuilder
                        .set(Fields.customDimension(CustomDimension.LIST_NAME.getValue()),
                                trackingName)
                        .set(Fields.customDimension(CustomDimension.LIST_POSITION.getValue()),
                                Integer.toString(listPosition))
                        .set(Fields.customDimension(CustomDimension.ITEM_POSITION.getValue()),
                                Integer.toString(itemPosition))
                        .set(Fields.customDimension(CustomDimension.NUMBER_OF_LISTS.getValue()),
                                Integer.toString(numberOfLists))
                        .set(Fields.customDimension(CustomDimension.NUMBER_OF_ITEMS_IN_LIST
                                .getValue()), Integer.toString(numberOfItems));

                EasyTracker.getInstance(context).send(mapBuilder.build());
            }
        }
    }

    /**
     * Tracks a share of an item.
     *
     * @param base A conceptual name for the activity.
     * @param item The item being shown.
     */
    public void trackItemShareButtonClick(String trackingName, GooglePlayItem item) {
        Log.d(LOG_TAG, "trackItemShareButtonClick");
        if (item != null) {
            Log.d(LOG_TAG, "trackingName: " + trackingName + " item: " + item.title);
            String type = item.type;
            String provider = item.provider;
            String title = item.title;

            String queryParam = composeDownloadQueryParam(item);

            trackScreenView(TrackableScreens.DETAIL, trackingName, type, TrackableEvents.SHARE,
                    queryParam);

            trackEvent(TrackableEvents.SHARE, trackingName, type, provider, title, queryParam);
        }
    }

    private void trackEvent(String event, String trackingName, String type, String provider,
            String title, String queryParam) {
        StringBuilder category = new StringBuilder();
        category.append(trackingName);
        category.append('/').append(type);

        StringBuilder label = new StringBuilder();
        label.append(provider).append('/').append(title);
        // Don't log events if running in monkey tests.
        if (!ActivityManager.isUserAMonkey()) {
            Log.d(LOG_TAG, "trackEvent: " + event + ", " + fixString(category.toString()) + ", "
                    + fixString(event) + ", " + fixString(label.toString()));
            Long value = null;
            String action = fixString(event);
            EasyTracker.getInstance(context).send(
                    MapBuilder.createEvent(category.toString(), action, label.toString(), value)
                            .build());
        }
    }

    private void trackGetInstall(String event, String packageName, long eventValue) {
        // Don't log events if running in monkey tests.
        if (!ActivityManager.isUserAMonkey()) {
            Log.d(LOG_TAG, "trackGetInstall: " + packageName + ", " + event);
            EasyTracker.getInstance(context).send(
                    MapBuilder.createEvent(fixString("item"), fixString(packageName),
                            fixString(event), eventValue).build());
        }
    }

    private void trackScreenView(String screen, String trackingName, String type, String event,
            String queryParam) {
        StringBuilder pagename = new StringBuilder(screen);
        pagename.append('/').append(trackingName);
        pagename.append('/').append(type);
        pagename.append('/').append(event);
        if (queryParam != null && !queryParam.isEmpty()) {
            pagename.append("?").append(queryParam);
        }
        trackScreenView(pagename.toString());
    }

    /**
     * Tracks a screen view.
     *
     * @param screen The screen viewed.
     */
    public void trackScreenView(final String screen) {
        // Don't log views if running in monkey tests.
        if (!ActivityManager.isUserAMonkey()) {
            Log.d(LOG_TAG, "trackScreenView: " + screen);
            EasyTracker.getInstance(context).set(Fields.SCREEN_NAME, screen);
            EasyTracker.getInstance(context).send(MapBuilder.createAppView().build());
        }
    }

    /**
     * Fixes/replaces/removes characters that can't be handled by the google
     * analytics sdk
     *
     * @param string The string that should be checked and fixed
     * @return The "corrected" string
     */
    private String fixString(String string) {
        if (string == null) {
            return null;
        }
        return string.replace('(', '[').replace(')', ']');
    }

    /**
     * Creates a query param segment for the supplied item, intended to be
     * appended to the page used to show the item.
     *
     * @param item An item.
     * @return A string, or null.
     */
    private String composeDownloadQueryParam(GooglePlayItem item) {
        return "url=" + item.getLinkUrl(DOWNLOAD_REL);
    }

    /**
     * Performs a dispatch if the screen is turned on and the user is not a
     * monkey user(test automation).
     */
    public void dispatchNow() {
        if (!ActivityManager.isUserAMonkey()) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null && pm.isScreenOn()) {
                // First check if the user has opted out of tracking. This will
                // result in GA not sending any data.
                GaHelper.readAndSetGaEnabled(context);

                EasyTracker.getInstance(context).dispatchLocalHits();
            }
        }
    }

}
