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

import java.util.ArrayList;
import java.util.List;

public class RootResponse {

    private static final String SEARCH_REL = "searchApps";

    private static final String SUGGERSTION_REL = "suggestApps";

    public List<Link> links = new ArrayList<Link>();

    public String getSuggestionUrl() {
        for (Link link : links) {
            if (SUGGERSTION_REL.equals(link.rel)) {
                return link.href;
            }
        }
        return null;
    }

    public String getSearchUrl() {
        for (Link link : links) {
            if (SEARCH_REL.equals(link.rel)) {
                return link.href;
            }
        }
        return null;
    }
}
