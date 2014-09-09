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

/**
 * {"links":[{"rel":"self","href":
 * "http://api.dev.appmetadata.sonymobile.com/search?q=some&client=asfd&device=asfd&model=sf&return-fields=title{&page,size,sort}"
 * }],"content":[{"id":"111","title":"Some Game","links":[]},{"id":"112","title"
 * :"Some Other Game","links":[]}],"page":{"size":20,"totalElements":2,
 * "totalPages":1,"number":0}}
 */

public class SearchResponse {

    public List<Link> links;

    public List<Item> content;

}
