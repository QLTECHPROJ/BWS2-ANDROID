package com.brainwellnessspa.userModuleTwo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NewSignUpModel {
    @SerializedName("ResponseData")
    @Expose
    private var responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    private var responseCode: String? = ""

    @SerializedName("ResponseMessage")
    @Expose
    private var responseMessage: String? = ""

    @SerializedName("ResponseStatus")
    @Expose
    private var responseStatus: String? = ""

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
        @SerializedName("ID")
        @Expose
        var id: String? = ""

        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("Email")
        @Expose
        var email: String? = ""

        @SerializedName("MobileNo")
        @Expose
        var mobileNo: String? = ""

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = ""
    }
}