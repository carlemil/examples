/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.test;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import org.json.JSONException;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

public class JsonTest extends TestCase {

    public void testStreamingJson() throws IOException, JSONException {
        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        writer.setLenient(true);
        writer.beginObject();
        writer.name("data");
        writer.beginArray();
        new Gson().toJson(new JsonParser().parse("{\"banan\":123}"), writer);
        new Gson().toJson(new JsonParser().parse("{\"banan\":123}"), writer);
        new Gson().toJson(new JsonParser().parse("{\"banan\":123}"), writer);
        writer.endArray();
        writer.endObject();
        writer.close();
        System.out.println(out.getBuffer());
    }

}
