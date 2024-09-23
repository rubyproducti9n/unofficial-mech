package com.rubyproducti9n.unofficialmech;

public class video_item {

    private String vidUrl, username;

    public video_item(String vidUrl, String username){
        this.username = username;
        this.vidUrl = vidUrl;
    }

    public String getVidUrl() {
        return vidUrl;
    }

    public String getUserName() {
        return username;
    }
}
