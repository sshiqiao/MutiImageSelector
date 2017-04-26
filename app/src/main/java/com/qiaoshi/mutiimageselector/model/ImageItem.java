package com.qiaoshi.mutiimageselector.model;

public class ImageItem {
    public String imagePath;
    public boolean isSelected;
    public ImageItem (String imagePath, boolean isSelected) {
        this.imagePath = imagePath;
        this.isSelected = isSelected;
    }
}
