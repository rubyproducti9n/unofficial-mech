package com.rubyproducti9n.unofficialmech;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private String postId;
    private String uid;
    private String avatar;
    private String user_name;
//    private String div;
    private String imgUrl;
    private String caption;
    private String uploadTime;
    private String visibility;

    private Map<String, Boolean> likes;

    private boolean isMuted;

//    public Post(){
//
//
//
//    }

    public Post (String postId, String uid, String user_name, String imgUrl, String caption, String uploadTime, String visibility, Map<String, Boolean> likes){
        this.postId = postId;
        this.uid = uid;
        this.user_name = user_name;
//        this.div = div;
        this.imgUrl = imgUrl;
        this.caption = caption;
        this.uploadTime = uploadTime;
        this.visibility = visibility;
        this.likes = likes;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return user_name;
    }

    public String getPostUrl() {
        return imgUrl;
    }

    public String getCaption() {
        return caption;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getStateVisibility() {
        return visibility;
    }

    public void setStateVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }
    public int getLikeCount(){
        return likes != null ? likes.size() : 0;
    }
    public boolean isLikedByCurrentUser(String userId){
        return likes!= null && likes.containsKey(userId);
    }
    public void updateLikes(String userId, boolean isLiked){
        if (likes == null){
            likes = new HashMap<>();
        }
        if (isLiked){
            likes.put(userId, true);
        }else{
            likes.remove(userId);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }
    public void toggleMute() {
        isMuted = !isMuted;
    }
}
