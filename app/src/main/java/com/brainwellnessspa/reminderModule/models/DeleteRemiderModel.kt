package com.brainwellnessspa.reminderModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeleteRemiderModel {
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