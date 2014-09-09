/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sdk.testutils.httpd;

import com.google.mockwebserver.RecordedRequest;

import android.net.Uri;

import java.util.List;

public class DispatchedRequest {

    private final RecordedRequest request;

    private final Uri uri;

    public DispatchedRequest(RecordedRequest request) {
        this.request = request;
        uri = Uri.parse(request.getPath());
    }

    public boolean equals(Object o) {
        return request.equals(o);
    }

    public String getRequestLine() {
        return request.getRequestLine();
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getParameter(String param) {
        return uri.getQueryParameter(param);
    }

    public List<String> getHeaders() {
        return request.getHeaders();
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public List<String> getHeaders(String name) {
        return request.getHeaders(name);
    }

    public List<Integer> getChunkSizes() {
        return request.getChunkSizes();
    }

    public long getBodySize() {
        return request.getBodySize();
    }

    public byte[] getBody() {
        return request.getBody();
    }

    public String getUtf8Body() {
        return request.getUtf8Body();
    }

    public int getSequenceNumber() {
        return request.getSequenceNumber();
    }

    public String getSslProtocol() {
        return request.getSslProtocol();
    }

    public int hashCode() {
        return request.hashCode();
    }

    public String toString() {
        return request.toString();
    }

}
