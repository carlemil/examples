package com.jayway.volleydemo.net;


import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.jayway.volleydemo.domain.server.ServerModel.Channel;
import com.jayway.volleydemo.domain.server.ServerModel.ChannelList;
import com.jayway.volleydemo.domain.server.ServerModel.JsonList;
import com.jayway.volleydemo.domain.server.ServerModel.Root;

/**
 * Created by erbsman on 7/25/13.
 */
public class DemoApi {

    private static final String LOG_TAG = DemoApi.class.getCanonicalName();
    private final RequestQueue mQueue;

    public DemoApi(RequestQueue queue) {
        mQueue = queue;
    }

    @SuppressWarnings("unchecked")
    public Request<Root> getRoot(Listener<Root> listener,
                             Response.ErrorListener errorListener, String url) {
        Log.d(LOG_TAG, "getRoot");
        return mQueue.add(new RootRequest(url, listener, errorListener));
    }

    @SuppressWarnings("unchecked")
    public Request<ChannelList> getChannelList(Listener<ChannelList> listener,
                             Response.ErrorListener errorListener, String url) {
        Log.d(LOG_TAG, "getChannelList");
        return mQueue.add(new ChannelListRequest(url, listener, errorListener));
    }

    @SuppressWarnings("unchecked")
    public Request<Channel> getChannel(Listener<Channel> listener,
                             Response.ErrorListener errorListener, String url) {
        Log.d(LOG_TAG, "getChannel");
        return mQueue.add(new ChannelRequest(url, listener, errorListener));
    }

    @SuppressWarnings("unchecked")
    public Request<JsonList> getList(Listener<JsonList> listener,
                             Response.ErrorListener errorListener, String url) {
        Log.d(LOG_TAG, "getList");
        return mQueue.add(new ListRequest(url, listener, errorListener));
    }


}
