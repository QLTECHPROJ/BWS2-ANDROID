package com.brainwellnessspa.userModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileSaveDataModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<Any?>? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = null
}