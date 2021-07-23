package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AudioInterruptionModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<Any>? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = ""

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = ""

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = ""
}