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

public class SearchKillSwitchResponse {

    public boolean systemShutdown;

    public String message;

    private static final String MORE_INFO_REL = "moreInfo";

    public List<Link> links = new ArrayList<Link>();

    public String getMoreInfo() {
        for (Link link : links) {
            if (MORE_INFO_REL.equals(link.rel)) {
                return link.href;
            }
        }
        return null;
    }

}
