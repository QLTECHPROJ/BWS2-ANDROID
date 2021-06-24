package com.brainwellnessspa.billingOrderModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BillingAddressViewModel {
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
        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("Email")
        @Expose
        var email: String? = null

        @SerializedName("PhoneNumber")
        @Expose
        var phoneNumber: String? = null

        @SerializedName("Address1")
        @Expose
        var address1: String? = null

        @SerializedName("Address2")
        @Expose
        var address2: String? = null

        @SerializedName("Suburb")
        @Expose
        var suburb: String? = null

        @SerializedName("State")
        @Expose
        var state: String? = null

        @SerializedName("Postcode")
        @Expose
        var postcode: String? = null

        @SerializedName("Country")
        @Expose
        var country: String? = null

        @SerializedName("OrderTotal")
        @Expose
        var orderTotal: String? = null

        @SerializedName("OrderDiscount")
        @Expose
        var orderDiscount: String? = null

        @SerializedName("Plan")
        @Expose
        var plan: String? = null
    }
}