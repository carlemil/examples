package com.jayway.volleydemo.domain.server;

import java.util.List;

/**
 * <p>
 * A domain object representation of the {@code item} JSON objects passed by the
 * server to the SDK.
 * </p>
 * <p>
 * See the full Sony Select Server API documentation for the details.
 * </p>
 */
public abstract class Item {
    /**
     * <p>
     * A domain object representation of the objects in the {@code links} JSON
     * array passed by the server to the SDK.
     * </p>
     * <p>
     * See the full Sony Select Server API documentation for the details.
     * </p>
     */
    public static final class Link {
        /**
         * The id of the link (this field is <em>optional</em> and not always
         * set by the server).
         */
        public String id;

        /**
         * The relation of the link, like "download", "icon" or so (this field
         * is <em>mandatory</em> and must always be set by the server).
         */
        public String rel;

        /**
         * The actual target address of the link (this field is
         * <em>mandatory</em> and must always be set by the server).
         */
        public String href;

        private Link() {
            // Hide the constructor.
        }
    }

    /**
     * The id of the item (this field is <em>mandatory</em>). This is the local,
     * client side id.
     */
    public long id;

    /**
     * The id of the list this item currently belongs to (this field is
     * <em>optional</em>). This is the local, client side id of the list. The
     * same item can belong to multiple lists, hence, this field is only set
     * when the item is fetched in a context of a list.
     */
    public long listId;

    /**
     * The item type (this field is <em>mandatory</em>). This field gives a hint
     * on what kind of content is stored in the {@link Item#content} field.
     */
    public String type;

    /**
     * The links for an item, such as for images or the actual content download
     * URL (this field is <em>mandatory</em>).
     */
    public List<Link> links;

    protected Item() {
        // Hide the constructor.
    }

//    /**
//     * Gets the first link in the {@link Item#links} list that matches the given
//     * relation.
//     *
//     * @see Item#getLinkUrls(String) for information on how to get <em>all</em>
//     *      links that match a given relation.
//     * @param rel The relation of the link to look for.
//     * @return A string representation of the URL of the link, or an empty
//     *         string if no link is found. Never null.
//     */
//    public String getLinkUrl(String rel) {
//        String href = "";
//
//        if (!TextUtils.isEmpty(rel) && !TextUtils.isEmpty(links)) {
//            for (Link link : links) {
//                if (!TextUtils.isEmpty(link.rel) && rel.equals(link.rel)) {
//                    href = !TextUtils.isEmpty(link.href) ? link.href : "";
//                    break;
//                }
//            }
//        }
//
//        return href;
//    }
//
//    /**
//     * Gets the first link in the {@link Item#links} list that matches the given
//     * relation.
//     *
//     * @see Item#getLinkUrls(String) for information on how to get <em>all</em>
//     *      links that match a given relation.
//     * @param rel The relation of the link to look for.
//     * @return A Link. If not found returning null.
//     */
//    public Link getLink (String rel) {
//        Link linkFound = null;
//
//        if (!TextUtils.isEmpty(rel) && !TextUtils.isEmpty(links)) {
//            for (Link link : links) {
//                if (!TextUtils.isEmpty(link.rel) && rel.equals(link.rel)) {
//                    linkFound = link;
//                    break;
//                }
//            }
//        }
//
//        return linkFound;
//    }
//
//    /**
//     * Gets an array of links from the {@link Item#links} list that match the
//     * given relation.
//     *
//     * @see Item#getLinkUrl(String) for information on how to get only the
//     *      <em>first</em> link that match a given relation.
//     * @param rel The relation of the links to look for.
//     * @return An array of strings representing the URL's of the links, or an
//     *         empty string-array if no links are found. Never null.
//     */
//    public String[] getLinkUrls(String rel) {
//        List<String> hrefs = new ArrayList<String>();
//
//        if (!TextUtils.isEmpty(rel) && !TextUtils.isEmpty(links)) {
//            for (Link link : links) {
//                if (!TextUtils.isEmpty(link.rel) && rel.equals(link.rel)) {
//                    hrefs.add(link.href);
//                }
//            }
//        }
//
//        return hrefs.toArray(new String[0]);
//    }
}
