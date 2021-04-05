package com.brainwellnessspa.UserModuleTwo.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddedUserListModel {
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

    fun getResponseData(): ResponseData? {
        return responseData
    }

    fun setResponseData(responseData: ResponseData?) {
        this.responseData = responseData
    }

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
    class ResponseData {
        @SerializedName("Maxuseradd")
        @Expose
        var maxuseradd: String? = null

        @SerializedName("CoUserList")
        @Expose
        var coUserList: List<CoUser>? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

        class CoUser {
            @SerializedName("UserID")
            @Expose
            var userID: String? = null

            @SerializedName("CoUserId")
            @Expose
            var coUserId: String? = null

            @SerializedName("Name")
            @Expose
            var name: String? = null

            @SerializedName("Email")
            @Expose
            var email: String? = null

            @SerializedName("Mobile")
            @Expose
            var mobile: String? = null
        }

        fun getResponseMessage(): String? {
            return maxuseradd
        }

        fun setResponseMessage(maxuseradd: String?) {
            this.maxuseradd = maxuseradd
        }

        fun getCoUser(): List<CoUser>? {
            return coUserList
        }

        fun setCoUser(coUserList: List<CoUser>?) {
            this.coUserList = coUserList
        }
    }
}