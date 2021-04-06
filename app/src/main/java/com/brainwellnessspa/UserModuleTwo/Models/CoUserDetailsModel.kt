package com.brainwellnessspa.UserModuleTwo.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CoUserDetailsModel {
    @SerializedName("ResponseData")
    @Expose
    private var responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    private var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    private var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    private var responseStatus: String? = null

    fun getResponseCode(): String? {
        return responseCode
    }

    fun setResponseCode(responseCode: String?) {
        this.responseCode = responseCode
    }

    fun getResponseMessage(): String? {
        return responseMessage
    }

    fun setResponseMessage(responseMessage: String?) {
        this.responseMessage = responseMessage
    }

    fun getResponseStatus(): String? {
        return responseStatus
    }

    fun setResponseStatus(responseStatus: String?) {
        this.responseStatus = responseStatus
    }

    fun getResponseData(): ResponseData? {
        return responseData
    }

    fun setResponseData(responseData: ResponseData?) {
        this.responseData = responseData
    }

    class ResponseData {
        @SerializedName("UserID")
        @Expose
        private var userID: String? = null

        @SerializedName("CoUserId")
        @Expose
        private var coUserId: String? = null

        @SerializedName("Name")
        @Expose
        private var name: String? = null

        @SerializedName("Email")
        @Expose
        private var email: String? = null

        @SerializedName("Mobile")
        @Expose
        private var mobile: String? = null

        @SerializedName("isProfileCompleted")
        @Expose
        private var isProfileCompleted: String? = null

        @SerializedName("isAssessmentCompleted")
        @Expose
        private var isAssessmentCompleted: String? = null

        @SerializedName("indexScore")
        @Expose
        private var indexScore: String? = null

        @SerializedName("planDetails")
        @Expose
        private var planDetails: List<Any>? = null

        @SerializedName("errormsg")
        @Expose
        private var errormsg: String? = null

        fun getUserID(): String? {
            return userID
        }

        fun setUserID(userID: String) {
            this.userID = userID
        }

        fun getCoUserId(): String? {
            return coUserId
        }

        fun setCoUserId(coUserId: String) {
            this.coUserId = coUserId
        }

        fun getName(): String? {
            return name
        }

        fun setName(name: String) {
            this.name = name
        }

        fun getEmail(): String? {
            return email
        }

        fun setEmail(email: String) {
            this.email = email
        }

        fun getMobile(): String? {
            return mobile
        }

        fun setMobile(mobile: String) {
            this.mobile = mobile
        }

        fun getIsProfileCompleted(): String? {
            return isProfileCompleted
        }

        fun setIsProfileCompleted(isProfileCompleted: String) {
            this.isProfileCompleted = isProfileCompleted
        }

        fun getIsAssessmentCompleted(): String? {
            return isAssessmentCompleted
        }

        fun setIsAssessmentCompleted(isAssessmentCompleted: String) {
            this.isAssessmentCompleted = isAssessmentCompleted
        }

        fun getIndexScore(): String? {
            return indexScore
        }

        fun setIndexScore(indexScore: String) {
            this.indexScore = indexScore
        }

        fun getPlanDetails(): List<Any?>? {
            return planDetails
        }

        fun setPlanDetails(planDetails: List<Any?>) {
            this.planDetails = listOf(planDetails)
        }

        fun getErrormsg(): String? {
            return errormsg
        }

        fun setErrormsg(errormsg: String) {
            this.errormsg = errormsg
        }
    }
}