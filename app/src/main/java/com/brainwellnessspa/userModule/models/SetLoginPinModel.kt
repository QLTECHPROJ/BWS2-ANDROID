package com.brainwellnessspa.userModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SetLoginPinModel {
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
        @SerializedName("UserId")
        @Expose
        var UserId: String? = ""

        @SerializedName("Name")
        @Expose
        var Name: String? = ""

        @SerializedName("Email")
        @Expose
        var Email: String? = ""

        @SerializedName("Pin")
        @Expose
        var Pin: String? = ""

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = ""
    }
}

