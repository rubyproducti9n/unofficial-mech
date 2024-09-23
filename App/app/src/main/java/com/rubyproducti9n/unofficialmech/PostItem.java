package com.rubyproducti9n.unofficialmech;

public class PostItem {
    private String username;
    private String postUrl;
    private String caption;
    private String date_time;

    public PostItem(String username, String postUrl, String caption, String date_time){
        this.username = username;
        this.postUrl = postUrl;
        this.caption = caption;
        this.date_time = date_time;
    }


    public String getUserName() {
        return username;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getCaption() {
        return caption;
    }

    public String getUploadTime() {
        return date_time;
    }

//    public String getImageUrl() {
//        return postUrl;
//    }
//
    public String getTags() {
        return date_time;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}
