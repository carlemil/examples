
package com.jayway.volleydemo.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.jayway.volleydemo.R;
import com.jayway.volleydemo.domain.server.ServerModel.Channel;
import com.jayway.volleydemo.domain.server.ServerModel.ChannelList;
import com.jayway.volleydemo.domain.server.ServerModel.JsonLink;
import com.jayway.volleydemo.domain.server.ServerModel.JsonList;
import com.jayway.volleydemo.domain.server.ServerModel.JsonListLink;
import com.jayway.volleydemo.domain.server.ServerModel.Root;
import com.jayway.volleydemo.net.DemoApi;
import com.jayway.volleydemo.net.VolleySingelton;

public class SyncHandler {

    protected static final String LOG_TAG = SyncHandler.class.getCanonicalName();

    private DemoApi api = null;

    private SyncListener syncListener = null;

    private AtomicBoolean syncing = new AtomicBoolean(false);

    private static final Map<String, Integer> orderMap = Collections
            .synchronizedMap(new HashMap<String, Integer>());

    CountDownLatch listRequestInTheAir = null;

    public void Sync(Context context, SyncListener syncListener) {
        if (syncing.compareAndSet(false, true)) {
            if (Repository.gotContent()) {
                syncing.set(false);
                syncListener.syncCompleted();
                return;
            }
            this.syncListener = syncListener;
            api = VolleySingelton.getInstance().getApi();
            String url = context.getResources().getString(R.string.server_uri);
            api.getRoot(rootListener, errorListener, url);
        }
    }

    private Listener<Root> rootListener = new Listener<Root>() {
        @Override
        public void onResponse(Root root) {
            for (JsonLink link : root.links) {
                if ("channels".equals(link.rel)) {
                    Log.d(LOG_TAG, "root, rel: " + link.rel + ", href: " + link.href);
                    api.getChannelList(channelListListener, errorListener, link.href);
                    break;
                }
            }
        }
    };

    private Listener<ChannelList> channelListListener = new Listener<ChannelList>() {
        @Override
        public void onResponse(ChannelList channel) {
            for (JsonLink link : channel.links) {
                if ("channel".equals(link.rel) && "sonyselect".equals(link.id)) {
                    Log.d(LOG_TAG, "channelListListener, rel: " + link.rel + ", id: " + link.id);
                    api.getChannel(channelListener, errorListener, link.href);
                    break;
                }
            }
        }
    };

    private Listener<Channel> channelListener = new Listener<Channel>() {
        @Override
        public void onResponse(Channel channel) {
            int lists = channel.links.size();
            Repository.initLists(lists);
            listRequestInTheAir = new CountDownLatch(lists);
            for (JsonListLink list : channel.lists) {
                orderMap.put(list.title, Integer.valueOf(orderMap.size() + 1));
                for (JsonLink link : list.links) {
                    Log.d(LOG_TAG, "channelListener "+link.href);
                    api.getList(listListener, errorListener, link.href);
                }
            }
        }
    };

    private Listener<JsonList> listListener = new Listener<JsonList>() {
        @Override
        public void onResponse(JsonList list) {
            int order = 0;
            order = orderMap.get(list.title);
            Repository.addList(list, order);
            listRequestInTheAir.countDown();
            Log.d(LOG_TAG, "listRequestInTheAir: " + listRequestInTheAir.getCount());
            if (listRequestInTheAir.getCount() == 0) {
                Log.d(LOG_TAG, "done");
                syncing.set(false);
                syncListener.syncCompleted();
            }
        }
    };

    private ErrorListener errorListener = new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(LOG_TAG, "ERROR:\n" + error.toString() + " - " + error.getMessage());
            for (StackTraceElement e : error.getStackTrace()) {
                Log.d(LOG_TAG, "\n" + e);
            }
            syncListener.syncFailed();
        }
    };

}
