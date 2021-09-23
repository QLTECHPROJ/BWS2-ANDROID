package com.brainwellnessspa.addPaymentStripeModule.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddCardModel {
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
        @SerializedName("CardId")
        @Expose
        var cardId: String? = null

        @SerializedName("UserID")
        @Expose
        var userId: String? = null
    }
}