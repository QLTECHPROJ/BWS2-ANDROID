package com.brainwellnessspa.invoicemodule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InvoiceDetailModel {
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

    inner class ResponseData {
        @SerializedName("InvoiceNumber")
        @Expose
        var invoiceNumber: String? = ""

        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("Qty")
        @Expose
        var qty: String? = ""

        @SerializedName("Session")
        @Expose
        var session: String? = ""

        @SerializedName("TotalAmount")
        @Expose
        var totalAmount: String? = ""

        @SerializedName("TaxAmount")
        @Expose
        var taxAmount: String? = ""

        @SerializedName("NetAmount")
        @Expose
        var netAmount: String? = ""

        @SerializedName("DiscountedAmount")
        @Expose
        var discountedAmount: String? = ""

        @SerializedName("InvoiceTo")
        @Expose
        var invoiceTo: String? = ""

        @SerializedName("InvoiceDate")
        @Expose
        var invoiceDate: String? = ""

        @SerializedName("Email")
        @Expose
        var email: String? = ""

        @SerializedName("CardBrand")
        @Expose
        var cardBrand: String? = ""

        @SerializedName("CardDigit")
        @Expose
        var cardDigit: String? = ""

        @SerializedName("GSTAmount")
        @Expose
        var gstAmount: String? = ""

        @SerializedName("Amount")
        @Expose
        var amount: String? = ""

        @SerializedName("InvoiceFrom")
        @Expose
        var invoiceFrom: String? = ""
    }
}