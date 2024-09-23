package com.rubyproducti9n.unofficialmech;

public class DevAccountsItems {

    String userId;
    String userAvatar;
    String accountName;
    String userDivision;
    String userYear;
    String userRole;
    String clgEmail;
    Boolean verified;
    Boolean suspended;


    public DevAccountsItems(String userId, String userAvatar, String accountName, String userDivision, String userYear, String userRole, String clgEmail, Boolean verified, Boolean suspended){
        this.userId = userId;
        this.userAvatar = userAvatar;
        this.accountName =accountName;
        this.userDivision = userDivision;
        this.userYear = userYear;
        this.userRole = userRole;
        this.clgEmail = clgEmail;
        this.verified = verified;
        this.suspended = suspended;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserDivision() {
        return userDivision;
    }

    public void setUserDivision(String userDivision) {
        this.userDivision = userDivision;
    }

    public String getUserYear() {
        return userYear;
    }

    public void setUserYear(String userYear) {
        this.userYear = userYear;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getClgEmail() {
        return clgEmail;
    }

    public void setClgEmail(String clgEmail) {
        this.clgEmail = clgEmail;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }
}
