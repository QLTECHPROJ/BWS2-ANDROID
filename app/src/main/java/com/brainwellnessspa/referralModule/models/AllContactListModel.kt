package com.brainwellnessspa.referralModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AllContactListModel {
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

    inner class ResponseData {
        @SerializedName("Isinsert")
        @Expose
        var isinsert: String? = ""
    }
}