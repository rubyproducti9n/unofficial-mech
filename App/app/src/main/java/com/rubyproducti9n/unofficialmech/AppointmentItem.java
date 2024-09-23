package com.rubyproducti9n.unofficialmech;

public class AppointmentItem {

    private String appointmentId;
    private String facultyId;
    private String user_name;
    private String div;
    private String caption;
    private String uploadTime;
    private String appointmentTime;
    private boolean status;

//    public Post(){
//
//
//
//    }

    public AppointmentItem(String appointmentId, String facultyId, String user_name, String div, String caption, String uploadTime, String appointmentTime, boolean status){
        this.appointmentId = appointmentId;
        this.facultyId = facultyId;
        this.user_name = user_name;
        this.div = div;
        this.caption = caption;
        this.uploadTime = uploadTime;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
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

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
