package com.jayway.volleydemo.net;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.jayway.volleydemo.domain.server.ServerModel.ChannelList;


import java.util.HashMap;

public class ChannelListRequest extends Request<ChannelList> {

    private final Gson mGson = new Gson();

    private final Response.Listener<ChannelList> mListener;

    public ChannelListRequest(String url, Response.Listener<ChannelList> listener,
            Response.ErrorListener errorListener) {
        super(Method.GET, putParamsOnUrl(url), errorListener);
        mListener = listener;
    }

    @Override
    protected Response<ChannelList> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        ChannelList channelResponse = mGson.fromJson(jsonString, ChannelList.class);
        return Response.success(channelResponse, getCacheEntry());
    }

    @Override
    protected void deliverResponse(ChannelList response) {
        mListener.onResponse(response);
    }

    private static String putParamsOnUrl(String url) {
        url += "?client=SonySelectSDK%2F999.999.999%20SonySelect%2F888.888.888&"
                + "androidbuildtype=user&model=C6903";
        return url;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Api-Key", "c42992635ffc752872dd2b19f1a79cc771d5ace8dd7319a476ead679dff6d301");
        params.put("Accept-Language", "en_UK");
        params.put("Accept", "application/vnd.sonymobile.select+json");
        params.put("User-Agent", "Dalvik/1.6.0(Linux; U; Android 4.3; C6903 Sony/14.2.C.0.63)");
        return params;
    }


}
