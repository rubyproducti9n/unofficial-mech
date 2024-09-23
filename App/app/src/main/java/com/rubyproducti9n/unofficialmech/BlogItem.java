package com.rubyproducti9n.unofficialmech;

public class BlogItem {
    private String username;
    private String div;
    private String imgUrl;
    private String blogTxt;
    private String link;
    private String date_time;
    private boolean impNotice;

    public BlogItem(String username, String div, String blogTxt, String link, String date_time, String imgUrl, boolean impNotice){
        this.username = username;
        this.div = div;
        this.imgUrl = imgUrl;
        this.blogTxt = blogTxt;
        this.link = link;
        this.date_time = date_time;
        this.impNotice = impNotice;
    }


    public String getUserName() {
        return username;
    }

    public String getBlogTxt() {
        return blogTxt;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getUserDiv() {
        return div;
    }

    public void setUserDiv(String div) {
        this.div = div;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUploadTime() {
        return date_time;
    }

    public boolean isImpNotice() {
        return impNotice;
    }

    public void setImpNotice(boolean impNotice) {
        this.impNotice = impNotice;
    }

    //    public String getImageUrl() {
//        return postUrl;
//    }
//
    public String getDate_time() {
        return date_time;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBlogTxt(String blogTxt) {
        this.blogTxt = blogTxt;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }


}
