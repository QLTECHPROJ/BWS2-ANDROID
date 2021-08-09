package com.brainwellnessspa.billingOrderModule.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class PlanDetails {
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
        @SerializedName("UserId")
        @Expose
        var userId: String? = ""

        @SerializedName("UserGroupId")
        @Expose
        var userGroupId: String? = ""

        @SerializedName("PlanId")
        @Expose
        var planId: String? = ""

        @SerializedName("PlanPurchaseDate")
        @Expose
        var planPurchaseDate: String? = ""

        @SerializedName("PlanExpireDate")
        @Expose
        var planExpireDate: String? = ""

        @SerializedName("OriginalTransactionId")
        @Expose
        var originalTransactionId: String? = ""

        @SerializedName("TransactionId")
        @Expose
        var transactionId: String? = ""

        @SerializedName("TrialPeriodStart")
        @Expose
        var trialPeriodStart: String? = ""

        @SerializedName("TrialPeriodEnd")
        @Expose
        var trialPeriodEnd: String? = ""

        @SerializedName("PlanStatus")
        @Expose
        var planStatus: String? = ""

        @SerializedName("PlanName")
        @Expose
        var planName: String? = ""

        @SerializedName("Price")
        @Expose
        var price: String? = ""

        @SerializedName("IntervalTime")
        @Expose
        var intervalTime: String? = ""

        @SerializedName("PlanDescription")
        @Expose
        var planDescription: String? = ""

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = ""
    }
}