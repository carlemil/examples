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
import com.sonymobile.sonyselect.net.domain.SuggestionsResponse;

public class SuggestionsRequest extends AbstractRequest<SuggestionsResponse> {

    private static final String LOG_TAG = SuggestionsRequest.class.getCanonicalName();

    private final Gson mGson = new Gson();

    public SuggestionsRequest(String url, String q,
            Response.Listener<SuggestionsResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, putParamsOnUrl(url, q), listener, errorListener);
    }

    @Override
    protected Response<SuggestionsResponse> parseNetworkResponse(NetworkResponse response) {
        Log.d(LOG_TAG, "SuggestionsResponse: " + response.statusCode);
        String jsonString = new String(response.data);
        SuggestionsResponse suggestions = mGson.fromJson(jsonString, SuggestionsResponse.class);
        return Response.success(suggestions, getCacheEntry());
    }

    private static String putParamsOnUrl(String url, String query) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(query)) {
            return null;
        }

        Builder uri = Uri.parse(url).buildUpon();
        putQueryParamOnUri(uri, query);

        return uri.build().toString();
    }

}
