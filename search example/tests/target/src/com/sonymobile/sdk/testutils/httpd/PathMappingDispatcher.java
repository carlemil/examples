/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sdk.testutils.httpd;

import com.google.mockwebserver.Dispatcher;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.RecordedRequest;
import com.sonymobile.sdk.testutils.ResourceUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PathMappingDispatcher extends Dispatcher {

    private Map<String, RequestResponse> responses = new HashMap<String, RequestResponse>();

    private List<DispatchedRequest> requests = new ArrayList<DispatchedRequest>();

    public RequestResponse on(String path) {
        RequestResponse req = responses.get(path);
        if (req == null) {
            req = new RequestResponse();
            responses.put(path, req);
        }
        return req;
    }

    public List<DispatchedRequest> getRequests() {
        return requests;
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        DispatchedRequest req = new DispatchedRequest(request);
        requests.add(req);
        RequestResponse requestResponse = responses.get(req.getPath());
        if (requestResponse == null) {
            return new MockResponse().setBody("").setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        return requestResponse.getNextResponse();
    }

    public class RequestResponse {

        private LinkedList<MockResponse> responses = new LinkedList<MockResponse>();

        public RequestResponse() {
        }

        private MockResponse getNextResponse() {
            MockResponse response = responses.getFirst();
            if (responses.size() > 1) {
                responses.removeFirst();
            }
            return response;
        }

        public RequestResponse send(MockResponse response) {
            responses.add(response);
            return this;
        }

        public RequestResponse send(String data) {
            return send(new MockResponse().setBody(data));
        }

        public RequestResponse send(int statusCode) {
            return send(new MockResponse().setResponseCode(statusCode));
        }

        public RequestResponse sendResource(String resource) {
            return send(ResourceUtil.toString(resource));
        }

    }

}
