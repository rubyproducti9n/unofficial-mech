package com.rubyproducti9n.unofficialmech;

public class CreateUser{

    private String userId;
    private String avatar;
    private String firstName;
    private String lastName;
    private String clgEmail;
    private String gender;
    private String prn;
    private String rollNo;
    private String div;
    private String personalEmail;
    private String password;
    private String role;
    private String mNum;
    private String altPassword;
    private boolean suspended;
    private String dateCreated;
    private String lastPaymentDate;
    private String dept;

    public CreateUser(String userId, String avatar, String firstName, String lastName, String clgEmail, String gender, String prn, String rollNo, String div, String personalEmail, String password, String role, String mNum, String altPassword, boolean suspended, String dateCreated, String lastPaymentDate, String dept){
        this.userId = userId;
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.clgEmail = clgEmail;
        this.gender = gender;
        this.prn = prn;
        this.rollNo = rollNo;
        this.div =div;
        this.personalEmail = personalEmail;
        this.password = password;
        this.role = role;
        this.mNum = mNum;
        this.altPassword = altPassword;
        this.suspended = suspended;
        this.dateCreated = dateCreated;
        this.lastPaymentDate = lastPaymentDate;
        this.dept = dept;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getClgEmail() {
        return clgEmail;
    }

    public void setClgEmail(String clgEmail) {
        this.clgEmail = clgEmail;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getDiv() {
        return div;
    }

    public void setDiv(String div) {
        this.div = div;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getmNum() {
        return mNum;
    }

    public void setmNum(String mNum) {
        this.mNum = mNum;
    }

    public String getAltPassword() {
        return altPassword;
    }

    public void setAltPassword(String altPassword) {
        this.altPassword = altPassword;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
