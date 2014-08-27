/*********************************************************************
 *       ____                      __  __       _     _ _            *
 *      / ___|  ___  _ __  _   _  |  \/  | ___ | |__ (_) | ___       *
 *      \___ \ / _ \| '_ \| | | | | \  / |/ _ \| '_ \| | |/ _ \      *
 *       ___) | (_) | | | | |_| | | |\/| | (_) | |_) | | |  __/      *
 *      |____/ \___/|_| |_|\__, | |_|  |_|\___/|_.__/|_|_|\___|      *
 *                         |___/                                     *
 *                                                                   *
 *********************************************************************
 *      Copyright 2013 Sony Mobile Communications AB.                *
 *      All rights, including trade secret rights, reserved.         *
 *********************************************************************/

package com.jayway.volleydemo.domain.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

public final class ServerModel {

    public static final String CHANNELS_LINK = "channels";

    public static final String LOG_TAG = ServerModel.class.getCanonicalName();

    static final boolean hasLinkWithRel(List<JsonLink> links, String rel) {
        if (links != null && !TextUtils.isEmpty(rel)) {
            for (JsonLink link : links) {
                if (rel.equals(link.rel)) {
                    return true;
                }
            }
        }
        return false;
    }

    static final JsonLink getFirstLink(List<JsonLink> links, String rel) {
        if (links != null && !TextUtils.isEmpty(rel)) {
            for (JsonLink link : links) {
                if (rel.equals(link.rel) && !TextUtils.isEmpty(link.href)) {
                    return link;
                }
            }
        }

        return new JsonLink();
    }

    public static final class Root {
        public List<JsonLink> links;

        public JsonLink getLink(String rel) {
            if (links == null) {
                return null;
            }
            for (JsonLink link : links) {
                if (rel.equals(link.rel)) {
                    return link;
                }
            }
            return null;
        }
    }

    public class Config {

        public boolean personalDataAllowed;

        public List<JsonLink> links;

    }

    public static final class ChannelList {
        public List<JsonLink> links;

        public String etag;

        public JsonLink getChannelLinkById(String id) {
            if (links == null) {
                return null;
            }
            for (JsonLink link : links) {
                if (id.equals(link.id)) {
                    return link;
                }
            }
            return null;
        }
    }

    public static final class Channel {
        public String name;

        public List<JsonLink> links;

        public List<JsonListLink> lists;

        public String etag;

        public boolean modified;
    }

    public static final class JsonListLink {
        public int order;

        public List<JsonLink> links;

        public String trackingName;

        public String title;

        public List<String> types;

        public String etag;

        private String key;

        public JsonLink getLinkByRel(String rel) {
            if (TextUtils.isEmpty(rel) || links == null) {
                return null;
            }

            for (JsonLink link : links) {
                if (link != null && rel.equals(link.rel)) {
                    return link;
                }
            }

            return null;
        }

        public String getKey() {
            if (key == null) {
                JsonLink link = getLinkByRel("items");

                if (link == null) {
                    link = getLinkByRel("personalitems");
                }

                if (link != null) {
                    key = calculateHash(link.href);
                }
            }

            return key;
        }

        /**
         * Calculates a MD5-hash on the given input string.
         *
         * @param content The string to calculate the the hash on.
         * @return The MD5 check sum.
         */
        private static String calculateHash(String content) {
            String result = null;
            if (!TextUtils.isEmpty(content)) {
                try {
                    byte[] rawBytes = content.getBytes();
                    MessageDigest digest = MessageDigest.getInstance("SHA1");
                    digest.update(rawBytes, 0, rawBytes.length);
                    byte[] digestedBytes = digest.digest();
                    BigInteger bigInt = new BigInteger(1, digestedBytes);
                    result = bigInt.toString(16);
                } catch (NoSuchAlgorithmException e) {
                    Log.e(LOG_TAG, "Failed to calculate hash", e);
                    result = content;
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, "Failed to calculate hash", e);
                    result = content;
                }
            }
            return result;
        }

    }

    public static final class JsonList {
        public boolean failed = false;

        public boolean changed = false;

        public int order;

        public int maxAge;

        public int retryAfter;

        public String trackingName;

        public String key;

        public String etag;

        public String title;

        public List<JsonLink> links = new ArrayList<ServerModel.JsonLink>();

        public List<String> types = new ArrayList<String>();

        public List<JsonItem> items = new ArrayList<ServerModel.JsonItem>();

        JsonList(JsonListLink listLink) {
            if (listLink != null) {
                order = listLink.order;
                trackingName = listLink.trackingName;
                title = listLink.title;
                links = listLink.links;
                types = listLink.types;
                key = listLink.getKey();
            }
        }
    }

    public static final class JsonItem {
        public String id;

        public String type;

        public String json;

        public List<JsonLink> links = new ArrayList<ServerModel.JsonLink>();

        public String title;

        public String description;

        public String genre;

        public String provider;

        public String packageName;

        public float rating;
    }

    public static final class JsonLink {
        public String rel;

        public String id;

        public String title;

        public String href;
    }

    public static final class JsonErrorMessage {
        public String systemShutdown;

        public String errorMessage;

        public String endUserTitle;

        public String endUserMessage;

        public List<JsonLink> errorMessageLinks;
    }

    public static final class ServerResponse {
        final boolean isAuthorized;

        final boolean hasChanged;

        final boolean isReady;

        int maxAge = 123;

        int retryAfter = 0;

        String etag = null;

        String json = null;

        ServerResponse(boolean isAuthorized, boolean hasChanged, boolean isReady) {
            this.isAuthorized = isAuthorized;
            this.hasChanged = hasChanged;
            this.isReady = isReady;
        }
    }
}
