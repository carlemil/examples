package com.sonymobile.sonyselect.domain;

import android.content.res.Resources;
import android.util.Log;

import com.sonymobile.sonyselect.adapter.ImageLink;
import com.sonymobile.sonyselect.api.content.Item;
import com.sonymobile.sonyselect.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    private static final String LOG_TAG = ItemUtil.class.getName();

    /**
     * Get the first image url that is not empty from of the item. Ordered by
     * the
     *
     * @param item
     *            A item containing a list of possible urls.
     * @param resources
     *            The string array defining in what order we should pick strings
     *            from the item list.
     * @return A url, hopefully pointing to a bitmap in cache or a image online.
     */
    public static String getImageUrl(final GooglePlayItem item, Resources resources, int relStringArray) {
        if (item == null) {
            return null;
        }
        String[] imageRelArray = resources.getStringArray(relStringArray);
        String url = null;
        int i = 0;
        while (StringUtil.isEmpty(url) && i < imageRelArray.length) {
            url = item.getLinkUrl(imageRelArray[i++]);
        }
        Log.v(LOG_TAG, "Got image url. listId:" + item.listId + " id:" + item.id + " item title:" + item.title + " rel:" + imageRelArray[i - 1] + " url:" + url);
        return url;
    }

    /**
     * Get image links in the (client-side)configured order.
     *
     * @param item
     *            A item containing a list of possible urls.
     * @param imageRelArray An array of image rel's.
     *            The string array defining in what order we should pick strings
     *            from the item list.
     * @return A list of 'imageLink data', that are hopefully pointing to a bitmap in cache or a image online.
     */
    public static List<ImageLink> getImageLinks(final GooglePlayItem item, String[] imageRelArray) {
        List<ImageLink> imageLinks = new ArrayList<ImageLink>();
        if (item == null) {
            Log.w(LOG_TAG, "Tried to fetch image links with no items.");
            return imageLinks;
        }
        Item.Link link;

        for (String imageRel : imageRelArray) {
            link = item.getLink(imageRel);
            if (link != null) {
                Log.v(LOG_TAG, "Got image link. listId:" + item.listId + " id:" + item.id + " item title:" + item.title + " rel:" + link.rel + " url:" + link.href);
                imageLinks.add(new ImageLink(link.href, link.rel));
            }
        }

        Log.v(LOG_TAG, "Got image links. Number:" + imageLinks.size());
        return imageLinks;
    }
}
