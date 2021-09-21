package com.brainwellnessspa.billingOrderModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CardListModel {
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

    inner class ResponseData {
        @SerializedName("isDefault")
        @Expose
        var isDefault: String? = null

        @SerializedName("brand")
        @Expose
        var brand: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("customer")
        @Expose
        var customer: String? = null

        @SerializedName("exp_month")
        @Expose
        var expMonth: String? = null

        @SerializedName("exp_year")
        @Expose
        var expYear: String? = null

        @SerializedName("last4")
        @Expose
        var last4: String? = null
    }
}