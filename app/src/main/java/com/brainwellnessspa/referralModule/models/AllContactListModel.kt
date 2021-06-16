package com.brainwellnessspa.referralModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AllContactListModel {
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

    inner class ResponseData {
        @SerializedName("Isinsert")
        @Expose
        var isinsert: String? = null
    }
}