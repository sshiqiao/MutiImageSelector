package com.qiaoshi.mutiimageselector.model;

public class GalleryItem {
    public String firstImagePath;
    public String galleryName;
    public int galleryNum;
    public GalleryItem(String firstImagePath, String galleryName, int galleryNum) {
        this.firstImagePath = firstImagePath;
        this.galleryName = galleryName;
        this.galleryNum = galleryNum;
    }
}
