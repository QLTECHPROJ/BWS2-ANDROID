package com.brainwellnessspa.billingOrderModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CurrentPlanVieViewModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData?  = null

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
        @SerializedName("Plan")
        @Expose
        var plan: String? = null

        @SerializedName("CardId")
        @Expose
        var cardId: String? = null

        @SerializedName("PlanId")
        @Expose
        var planId: String? = null

        @SerializedName("PlanFlag")
        @Expose
        var planFlag: String? = null

        @SerializedName("invoicePayId")
        @Expose
        var invoicePayId: String? = null

        @SerializedName("PlanStr")
        @Expose
        var planStr: String? = null

        @SerializedName("Activate")
        @Expose
        var activate: String? = null

        @SerializedName("Status")
        @Expose
        var status: String? = null

        @SerializedName("Subtitle")
        @Expose
        var subtitle: String? = null

        @SerializedName("CardDigit")
        @Expose
        var cardDigit: String? = null

        @SerializedName("OrderTotal")
        @Expose
        var orderTotal: String? = null

        @SerializedName("IsActive")
        @Expose
        var isActive: String? = null

        @SerializedName("expireDate")
        @Expose
        var expireDate: String? = null

        @SerializedName("Reattempt")
        @Expose
        var reattempt: String? = null

        @SerializedName("Feature")
        @Expose
        var feature: List<Feature>? = null

        inner class Feature {
            @SerializedName("Feature")
            @Expose
            var feature: String? = null
        }
    }
}