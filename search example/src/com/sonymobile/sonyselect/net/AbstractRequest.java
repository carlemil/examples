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

package com.sonymobile.sonyselect.net;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.sonymobile.sonyselect.activities.AbstractSearchActivity;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.internal.util.DeviceInfo;
import com.sonymobile.sonyselect.internal.util.Utils;
import com.sonymobile.sonyselect.util.StringUtil;
import com.sonymobile.sonyselect.util.TagManagerContainerConstants;

public abstract class AbstractRequest<T> extends Request<T> {

    private static final String LOG_TAG = AbstractRequest.class.getCanonicalName();

    private static final String JAVA_VERSION = System.getProperty("java.vm.version", "");

    private static final String ANDROID_VERSION = Build.VERSION.RELEASE;

    private static final String DEVICE = Build.MODEL;

    private static final String MANUFACTURER = Build.MANUFACTURER.replace(" ", "");

    private static final String BUILD_ID = Build.ID;

    private static final String USER_AGENT = "Dalvik/" + JAVA_VERSION + "(Linux; U; Android "
            + ANDROID_VERSION + "; " + DEVICE + " " + MANUFACTURER + "/" + BUILD_ID + ")";

    private static final String ACCEPT = "application/";

    private static DeviceInfo di;

    private final Response.Listener<T> mListener;

    public AbstractRequest(int method, String url, Response.Listener<T> listener,
            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public HashMap<String, String> getHeaders() {
        HashMap<String, String> params = new HashMap<String, String>();

        setAcceptLanguageHeader(params, Locale.getDefault());
        setAcceptHeader(params, "json");
        setUserAgentHeader(params);
        setAuthorizationHeader(params);

        return params;
    }

    private void setUserAgentHeader(Map<String, String> headers) {
        headers.put("User-Agent", USER_AGENT);
    }

    private void setAcceptHeader(Map<String, String> headers, String contentTypes) {
        headers.put("Accept", ACCEPT + contentTypes);
    }

    private void setContentTypeHeader(Map<String, String> headers, String contentTypes) {
        headers.put("Content-Type", contentTypes);
    }

    private void setAcceptLanguageHeader(Map<String, String> headers, Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String localeHeader = null;

        if (!Utils.isEmpty(language)) {
            localeHeader = language;
        }

        if (!Utils.isEmpty(localeHeader)) {
            if (!Utils.isEmpty(country)) {
                localeHeader += ("_" + country);
            }
            headers.put("Accept-Language", localeHeader);
        }
    }

    private void setAuthorizationHeader(Map<String, String> headers) {
        if (AbstractSearchActivity.getContainer() != null) {
            String apiKey = AbstractSearchActivity.getContainer().getString(
                    TagManagerContainerConstants.SEARCH_API_KEY);
            headers.put("Api-Key", apiKey);
        }
    }

    private static Uri.Builder putParamOnUri(Uri.Builder uriBuilder, String key, String value) {
        Log.v(LOG_TAG, "putParamOnUri: " + key + " : " + value);
        return uriBuilder.appendQueryParameter(key, value);
    }

    protected static Uri.Builder putQueryParamOnUri(Uri.Builder uri, String query) {
        return putParamOnUri(uri, "q", query);
    }

    private static DeviceInfo getDeviceInfo() {
        if (di == null) {
            di = new DeviceInfo(SonySelectApplication.get());
        }
        return di;
    }

    protected static Uri.Builder putModelParamOnUri(Uri.Builder uri) {
        String model = getDeviceInfo().getModel();
        return putParamOnUri(uri, "model", model);
    }

    protected static Uri.Builder putReturnParamOnUri(Uri.Builder uri, String ret) {
        return putParamOnUri(uri, "return", ret);
    }

    protected static Uri.Builder putStrategyParamOnUri(Uri.Builder uri) {
        return putParamOnUri(uri, "strategy", "sonySelect" );
    }

    protected static Uri.Builder putMccParamOnUri(Uri.Builder uri) {
        String mcc = getDeviceInfo().getMcc();
        if (StringUtil.isEmpty(mcc)) {
            return uri;
        }
        return putParamOnUri(uri, "mcc", mcc);
    }

    protected static Uri.Builder putMncParamOnUri(Uri.Builder uri) {
        String mnc = getDeviceInfo().getMnc();
        if (StringUtil.isEmpty(mnc)) {
            return uri;
        }
        return putParamOnUri(uri, "mnc", mnc);
    }

    protected static Uri.Builder putSpnParamOnUri(Uri.Builder uri) {
        String spn = getDeviceInfo().getSpn();
        if (StringUtil.isEmpty(spn)) {
            return uri;
        }
        return putParamOnUri(uri, "spn", spn);
    }

}
