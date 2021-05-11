package com.brainwellnessspa.UserModuleTwo.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VersionModel {

    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = null

    class ResponseData {
        @SerializedName("IsForce")
        @Expose
        var isForce: String? = null

        @SerializedName("displayRegister")
        @Expose
        var displayRegister: String? = null

        @SerializedName("segmentKey")
        @Expose
        var segmentKey: String? = null
    }
}