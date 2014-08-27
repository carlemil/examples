
package com.jayway.volleydemo.net;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.jayway.volleydemo.domain.server.ServerModel.JsonList;

import java.util.HashMap;

public class ListRequest extends Request<JsonList> {

    private final Gson mGson = new Gson();

    private final Response.Listener<JsonList> mListener;

    public ListRequest(String url, Response.Listener<JsonList> listener,
            Response.ErrorListener errorListener) {
        super(Method.GET, putParamsOnUrl(url), errorListener);
        mListener = listener;
    }

    @Override
    protected Response<JsonList> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        JsonList channelResponse = mGson.fromJson(jsonString, JsonList.class);
        return Response.success(channelResponse, getCacheEntry());
    }

    @Override
    protected void deliverResponse(JsonList response) {
        mListener.onResponse(response);
    }

    private static String putParamsOnUrl(String url) {
        return url;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Api-Key", "c42992635ffc752872dd2b19f1a79cc771d5ace8dd7319a476ead679dff6d301");
        params.put("Accept-Language", "en_US");
        params.put("Accept", "application/vnd.sonymobile.select+app+game+json");
        params.put("User-Agent", "Dalvik/1.6.0(Linux; U; Android 4.3; D6903 Sony/17.1.A.0.287)");
        return params;
    }

}
