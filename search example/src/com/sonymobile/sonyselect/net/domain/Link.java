/*********************************************************************
 *       ____                      __  __       _     _ _            *
 *      / ___|  ___  _ __  _   _  |  \/  | ___ | |__ (_) | ___       *
 *      \___ \ / _ \| '_ \| | | | | \  / |/ _ \| '_ \| | |/ _ \      *
 *       ___) | (_) | | | | |_| | | |\/| | (_) | |_) | | |  __/      *
 *      |____/ \___/|_| |_|\__, | |_|  |_|\___/|_.__/|_|_|\___|      *
 *                         |___/                                     *
 *                                                                   *
 *********************************************************************
 *      Copyright 2014 Sony Mobile Communications AB.                *
 *      All rights, including trade secret rights, reserved.         *
 *********************************************************************/

package com.sonymobile.sonyselect.net.domain;

import java.util.List;

import com.sonymobile.sonyselect.internal.util.Utils;

public class Link {

    public String rel;

    public String id;

    public String title;

    public String href;

    /**
     * Gets the first link in the {@link Item#links} list that matches the given
     * relation.
     *
     * @see Item#getLinkUrls(String) for information on how to get <em>all</em>
     *      links that match a given relation.
     * @param rel The relation of the link to look for.
     * @return A string representation of the URL of the link, or an empty
     *         string if no link is found. Never null.
     */
    public static String getLinkUrl(String rel, List<Link> links) {
        String href = null;

        if (!Utils.isEmpty(rel) && !Utils.isEmpty(links)) {
            for (Link link : links) {
                if (!Utils.isEmpty(link.rel) && rel.equals(link.rel)) {
                    href = !Utils.isEmpty(link.href) ? link.href : "";
                    break;
                }
            }
        }

        return href;
    }

}
