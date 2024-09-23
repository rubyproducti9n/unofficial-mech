package com.rubyproducti9n.unofficialmech;

public class Item {

    private String imageUrl;
    private String tags;
    private String type;

    public Item(String imageUrl, String tags, String type){
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public String getTags() {
        return tags;
    }
}
