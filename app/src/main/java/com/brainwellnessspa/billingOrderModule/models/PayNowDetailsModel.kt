package com.brainwellnessspa.billingOrderModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PayNowDetailsModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData>? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = null
    class ResponseData
}