package com.brainwellnessspa.userModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SetLoginPinModel {
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
        @SerializedName("UserId")
        @Expose
        var userId: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("Email")
        @Expose
        var email: String? = null

        @SerializedName("Pin")
        @Expose
        var pin: String? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null
    }
}

