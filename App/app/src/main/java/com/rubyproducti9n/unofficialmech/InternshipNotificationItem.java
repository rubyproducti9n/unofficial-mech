package com.rubyproducti9n.unofficialmech;

public class InternshipNotificationItem {

    private String internshipId;
    private String imgUrl;
    private String internship_name;
    private String description;
    private String url;

    public InternshipNotificationItem(String internshipId, String url, String intern_name, String description, String destinationUrl){
        this.internshipId = internshipId;
        this.imgUrl = url;
        this.internship_name = intern_name;
        this.description = description;
        this.url = destinationUrl;
    }

    public String getInternshipId() {
        return internshipId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getInternship_name() {
        return internship_name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}
