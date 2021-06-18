package com.brainwellnessspa.membershipModule.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterModel { @SerializedName("ResponseData")
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
        @SerializedName("CardId")
        @Expose
        private String cardId;
        @SerializedName("error")
        @Expose
        private String error;
        @SerializedName("UserID")
        @Expose
        private String userID;
        @SerializedName("CustomerId")
        @Expose
        private String customerId;
        @SerializedName("Plan")
        @Expose
        private String plan;
        @SerializedName("PlanStatus")
        @Expose
        private String planStatus;
        @SerializedName("PlanStartDt")
        @Expose
        private String planStartDt;
        @SerializedName("PlanExpiryDate")
        @Expose
        private String planExpiryDate;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("PhoneNumber")
        @Expose
        private String phoneNumber;
        @SerializedName("Address1")
        @Expose
        private String address1;
        @SerializedName("Address2")
        @Expose
        private String address2;
        @SerializedName("Suburb")
        @Expose
        private String suburb;
        @SerializedName("FirstLogin")
        @Expose
        private String firstLogin;
        @SerializedName("Image")
        @Expose
        private String image;
        @SerializedName("clinikoId")
        @Expose
        private String clinikoId;
        @SerializedName("CountryCode")
        @Expose
        private String countryCode;
        @SerializedName("CountryName")
        @Expose
        private String countryName;
        @SerializedName("IsPromocode")
        @Expose
        private String IsPromocode;

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }

        public String getPlanStatus() {
            return planStatus;
        }

        public void setPlanStatus(String planStatus) {
            this.planStatus = planStatus;
        }

        public String getPlanStartDt() {
            return planStartDt;
        }

        public void setPlanStartDt(String planStartDt) {
            this.planStartDt = planStartDt;
        }

        public String getPlanExpiryDate() {
            return planExpiryDate;
        }

        public void setPlanExpiryDate(String planExpiryDate) {
            this.planExpiryDate = planExpiryDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getSuburb() {
            return suburb;
        }

        public void setSuburb(String suburb) {
            this.suburb = suburb;
        }

        public String getFirstLogin() {
            return firstLogin;
        }

        public void setFirstLogin(String firstLogin) {
            this.firstLogin = firstLogin;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getClinikoId() {
            return clinikoId;
        }

        public void setClinikoId(String clinikoId) {
            this.clinikoId = clinikoId;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public String getIsPromocode() {
            return IsPromocode;
        }

        public void setIsPromocode(String isPromocode) {
            IsPromocode = isPromocode;
        }
    }
}