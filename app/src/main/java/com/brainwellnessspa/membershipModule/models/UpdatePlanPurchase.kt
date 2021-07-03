package com.brainwellnessspa.membershipModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
class UpdatePlanPurchase {

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
        @SerializedName("status")
        @Expose
        var status: String? = ""
    }
}