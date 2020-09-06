package com.qltech.bws.UserModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileViewModel {
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
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("PhoneNumber")
        @Expose
        private String phoneNumber;
        @SerializedName("Address")
        @Expose
        private String address;
        @SerializedName("Suburb")
        @Expose
        private String suburb;
        @SerializedName("OrderTotal")
        @Expose
        private String orderTotal;
        @SerializedName("DOB")
        @Expose
        private String dOB;
        @SerializedName("Ethnicity")
        @Expose
        private String ethnicity;
        @SerializedName("Occupation")
        @Expose
        private String occupation;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("Image")
        @Expose
        private String image;
        @SerializedName("IsVerify")
        @Expose
        private String isVerify;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOrderTotal() {
            return orderTotal;
        }

        public void setOrderTotal(String orderTotal) {
            this.orderTotal = orderTotal;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSuburb() {
            return suburb;
        }

        public void setSuburb(String suburb) {
            this.suburb = suburb;
        }

        public String getDOB() {
            return dOB;
        }

        public void setDOB(String dOB) {
            this.dOB = dOB;
        }

        public String getEthnicity() {
            return ethnicity;
        }

        public void setEthnicity(String ethnicity) {
            this.ethnicity = ethnicity;
        }

        public String getOccupation() {
            return occupation;
        }

        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getIsVerify() {
            return isVerify;
        }

        public void setIsVerify(String isVerify) {
            this.isVerify = isVerify;
        }
    }
}
