package com.rubyproducti9n.unofficialmech;

import java.util.HashMap;
import java.util.Map;

public class ProjectItem {

    private String projectId;
    private String avatar;
    private String user_name;
    private String div;
    private String imgUrl;
    private String caption;
    private String uploadTime;
    private Boolean patent;

    private String resources;

    private boolean isMuted;

//    public Post(){
//
//
//
//    }

    public ProjectItem(String projectId, String user_name, String div, String imgUrl, String caption, String uploadTime, Boolean patent, String resources){
        this.projectId = projectId;
        this.user_name = user_name;
        this.div = div;
        this.imgUrl = imgUrl;
        this.caption = caption;
        this.uploadTime = uploadTime;
        this.patent = patent;
        this.resources = resources;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDiv() {
        return div;
    }

    public void setDiv(String div) {
        this.div = div;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Boolean getPatent() {
        return patent;
    }

    public void setPatent(Boolean patent) {
        this.patent = patent;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }
}
