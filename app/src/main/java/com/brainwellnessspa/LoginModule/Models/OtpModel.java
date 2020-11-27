package com.brainwellnessspa.LoginModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpModel {
    @SerializedName("ResponseData")
    @Expose
    private ResponseData responseData;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("ResponseStatus")
    @Expose
    private String responseStatus;

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public class ResponseData {
        @SerializedName("UserID")
        @Expose
        private String userID;
        @SerializedName("FirstName")
        @Expose
        private String firstName;
        @SerializedName("error")
        @Expose
        private String error;
        @SerializedName("LastName")
        @Expose
        private String lastName;
        @SerializedName("FirstLogin")
        @Expose
        private String firstLogin;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("IsLock")
        @Expose
        private String isLock;
        @SerializedName("PhoneNumber")
        @Expose
        private String phoneNumber;
        @SerializedName("Address1")
        @Expose
        private String address1;
        @SerializedName("Address2")
        @Expose
        private String address2;
        @SerializedName("ProfileImg")
        @Expose
        private String profileImg;

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getFirstLogin() {
            return firstLogin;
        }

        public void setFirstLogin(String firstLogin) {
            this.firstLogin = firstLogin;
        }

        public String getIsLock() {
            return isLock;
        }

        public void setIsLock(String isLock) {
            this.isLock = isLock;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getProfileImg() {
            return profileImg;
        }

        public void setProfileImg(String profileImg) {
            this.profileImg = profileImg;
        }
    }
}
