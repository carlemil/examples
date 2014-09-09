
package com.sonymobile.sonyselect.adapter;

public class ImageLink {
    private String imageUrl;

    private String rel;

    private int width;

    public ImageLink(String imageUrl, String rel) {
        this.imageUrl = imageUrl;
        this.rel = rel;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getRel() {
        return rel;
    }

}
