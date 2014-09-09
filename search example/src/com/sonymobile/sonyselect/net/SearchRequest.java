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
import com.sonymobile.sonyselect.net.domain.SearchResponse;

public class SearchRequest extends AbstractRequest<SearchResponse> {

    private static final String LOG_TAG = SearchRequest.class.getCanonicalName();

    private final Gson mGson = new Gson();

    public SearchRequest(String url, String q, Response.Listener<SearchResponse> listener,
            Response.ErrorListener errorListener, boolean isANextRequest) {
        super(Method.GET, putParamsOnUrl(url, q, isANextRequest), listener, errorListener);
    }

    @Override
    protected Response<SearchResponse> parseNetworkResponse(NetworkResponse response) {
        Log.d(LOG_TAG, "SearchResponse: " + response.statusCode + ", response.data: "
                + new String(response.data));

        String jsonString = new String(response.data);
        SearchResponse result = mGson.fromJson(jsonString, SearchResponse.class);
        return Response.success(result, getCacheEntry());
    }

    private static String putParamsOnUrl(String url, String query, boolean isFirstPage) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Builder uri = Uri.parse(url).buildUpon();
        if (isFirstPage) {
            putQueryParamOnUri(uri, query);
            putStrategyParamOnUri(uri);
            putReturnParamOnUri(uri, "title,iconUrl,sonySelect,marketUrl"); // descriptionHtml,
        }
        return uri.build().toString();

    }

}
