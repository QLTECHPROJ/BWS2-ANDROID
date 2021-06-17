package com.brainwellnessspa.userModuleTwo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VersionModel {

    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = ""

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = ""

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = ""

    class ResponseData {
        @SerializedName("IsForce")
        @Expose
        var isForce: String? = ""

        @SerializedName("displayRegister")
        @Expose
        var displayRegister: String? = ""

        @SerializedName("segmentKey")
        @Expose
        var segmentKey: String? = ""
    }
}