package com.rubyproducti9n.unofficialmech;

public class HomeItem1 {

    private String timestamp;
    private String thumbnail;
    private String title;
    private String channelAvatar;
    private String channelName;
    private String date_time;
    private String link;


    public  HomeItem1(String timestamp, String thumbnail, String title, String channelAvatar, String channelName, String date_time, String link){

        this.timestamp = timestamp;
        this.thumbnail = thumbnail;
        this.title = title;
        this.channelAvatar = channelAvatar;
        this.channelName = channelName;
        this.date_time = date_time;
        this.link = link;

    }

    public String getVidTimestamp() {
        return timestamp;
    }

    public String getVidThumbnail() {
        return thumbnail;
    }

    public String getVidTitle() {
        return title;
    }

    public String getVidChannelAvatar() {
        return channelAvatar;
    }

    public String getVidChannelName() {
        return channelName;
    }

    public String getVidDateTime() {
        return date_time;
    }

    public String getLink() {
        return link;
    }
}
