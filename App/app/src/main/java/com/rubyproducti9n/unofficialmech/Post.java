package com.rubyproducti9n.unofficialmech;

import java.security.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post {
    public String postId;
    public String title;
    public String type;
    public String company;
    public String location;
    public boolean isRemote;
    public List<String> skills;
    public String applyLink;
    public String description;
    public String postedBy;
    public long postDate;
    public long deadline;
    public int upvotes;
    public int commentsCount;

    public Post() {} // Required for Firebase

    public Post(String postId, String title, String type, String company, String location,
                boolean isRemote, List<String> skills, String applyLink, String description,
                String postedBy, long postDate, long deadline) {
        this.postId = postId;
        this.title = title;
        this.type = type;
        this.company = company;
        this.location = location;
        this.isRemote = isRemote;
        this.skills = skills;
        this.applyLink = applyLink;
        this.description = description;
        this.postedBy = postedBy;
        this.postDate = postDate;
        this.deadline = deadline;
        this.upvotes = 0;
        this.commentsCount = 0;
    }
}
