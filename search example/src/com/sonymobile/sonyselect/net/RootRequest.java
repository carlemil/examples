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

import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.sonymobile.sonyselect.net.domain.RootResponse;

public class RootRequest extends AbstractRequest<RootResponse> {

    private static final String LOG_TAG = RootRequest.class.getCanonicalName();

    private final Gson mGson = new Gson();

    public RootRequest(String url, Response.Listener<RootResponse> listener,
            Response.ErrorListener errorListener) {
        super(Method.GET, putParamsOnUrl(url), listener, errorListener);
    }

    @Override
    protected Response<RootResponse> parseNetworkResponse(NetworkResponse response) {
        Log.d(LOG_TAG, "RootResponse: " + response.statusCode);
        String jsonString = new String(response.data);
        RootResponse root = mGson.fromJson(jsonString, RootResponse.class);
        return Response.success(root, getCacheEntry());
    }

    private static String putParamsOnUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Builder uri = Uri.parse(url).buildUpon();
        putModelParamOnUri(uri);
        putMccParamOnUri(uri);
        putMncParamOnUri(uri);
        putSpnParamOnUri(uri);

        return uri.build().toString();
    }

}
