package com.brainwellnessspa.invoicemodule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InvoiceDetailModel {
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
        @SerializedName("InvoiceNumber")
        @Expose
        var invoiceNumber: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("Qty")
        @Expose
        var qty: String? = null

        @SerializedName("Session")
        @Expose
        var session: String? = null

        @SerializedName("TotalAmount")
        @Expose
        var totalAmount: String? = null

        @SerializedName("TaxAmount")
        @Expose
        var taxAmount: String? = null

        @SerializedName("NetAmount")
        @Expose
        var netAmount: String? = null

        @SerializedName("DiscountedAmount")
        @Expose
        var discountedAmount: String? = null

        @SerializedName("InvoiceTo")
        @Expose
        var invoiceTo: String? = null

        @SerializedName("InvoiceDate")
        @Expose
        var invoiceDate: String? = null

        @SerializedName("Email")
        @Expose
        var email: String? = null

        @SerializedName("CardBrand")
        @Expose
        var cardBrand: String? = null

        @SerializedName("CardDigit")
        @Expose
        var cardDigit: String? = null

        @SerializedName("GSTAmount")
        @Expose
        var gstAmount: String? = null

        @SerializedName("Amount")
        @Expose
        var amount: String? = null

        @SerializedName("InvoiceFrom")
        @Expose
        var invoiceFrom: String? = null
    }
}