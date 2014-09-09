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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.sonymobile.sonyselect.net.domain.RootResponse;
import com.sonymobile.sonyselect.net.domain.SearchResponse;
import com.sonymobile.sonyselect.net.domain.SuggestionsResponse;

public class SearchApi {

    private final RequestQueue mQueue;

    public SearchApi(RequestQueue queue) {
        mQueue = queue;
    }

    @SuppressWarnings("unchecked")
    public Request<RootResponse> getRoot(Listener<RootResponse> listener,
            Response.ErrorListener errorListener, String url) {
        return mQueue.add(new RootRequest(url, listener, errorListener));
    }

    @SuppressWarnings("unchecked")
    public Request<SuggestionsResponse> getSuggestions(Listener<SuggestionsResponse> listener,
            Response.ErrorListener errorListener, String url, String q) {
        return mQueue.add(new SuggestionsRequest(url, q, listener, errorListener));
    }

    @SuppressWarnings("unchecked")
    public Request<SearchResponse> getSearch(Listener<SearchResponse> listener,
            Response.ErrorListener errorListener, String url, String q) {
        return mQueue.add(new SearchRequest(url, q, listener, errorListener, true));
    }

    @SuppressWarnings("unchecked")
    public Request<SearchResponse> getNextPage(Listener<SearchResponse> listener,
            Response.ErrorListener errorListener, String url, String q) {
        return mQueue.add(new SearchRequest(url, q, listener, errorListener, false));
    }

}
