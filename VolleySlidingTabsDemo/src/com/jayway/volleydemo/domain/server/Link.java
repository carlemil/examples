package com.jayway.volleydemo.domain.server;

import java.io.Serializable;

public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The title of the link (this field is <em>mandatory</em> and not always
     * set by the server).
     */
    public String title;

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

    public Link(String title, String rel, String href) {
        this.title = title;
        this.rel = rel;
        this.href = href;
    }
}
