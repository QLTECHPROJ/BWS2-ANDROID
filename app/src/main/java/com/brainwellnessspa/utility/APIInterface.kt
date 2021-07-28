package com.brainwellnessspa.utility

import com.brainwellnessspa.billingOrderModule.models.BillingAddressSaveModel
import com.brainwellnessspa.billingOrderModule.models.BillingAddressViewModel
import com.brainwellnessspa.billingOrderModule.models.CancelPlanModel
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel
import com.brainwellnessspa.invoiceModule.models.InvoiceDetailModel
import com.brainwellnessspa.invoiceModule.models.InvoiceListModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface APIInterface {
    /* TODO BillingAddressFragment */
    @POST("billingaddress")
    @FormUrlEncoded
    fun getBillingAddressView(
        @Field("UserID")
        userID: String?): Call<BillingAddressViewModel?>?

    /* TODO BillingAddressFragment */
    @POST("billingdetailsave")
    @FormUrlEncoded
    fun getBillingAddressSave(
        @Field("UserID")
        userID: String?,
        @Field("Name")
        name: String?,
        @Field("Email")
        email: String?,
        @Field("Country")
        country: String?,
        @Field("AddressLine1")
        addressLine1: String?,
        @Field("AddressLine2")
        addressLine2: String?,
        @Field("Suburb")
        suburb: String?,
        @Field("State")
        state: String?,
        @Field("Postcode")
        postcode: String?): Call<BillingAddressSaveModel?>?

    /* TODO CancelMembershipActivity */
    @POST("cancelplan")
    @FormUrlEncoded
    fun getCancelPlan(
        @Field("UserID")
        userID: String?,
        @Field("CancelId")
        cancelId: String?,
        @Field("CancelReason")
        cancelReason: String?): Call<CancelPlanModel?>?

    /* TODO MembershipChangeActivity */
    @POST("planlistonbilling")
    @FormUrlEncoded
    fun getPlanListBilling(
        @Field("UserID")
        userID: String?): Call<PlanListBillingModel?>?

    /* TODO InvoiceActivity */
    @POST("invoicelist")
    @FormUrlEncoded
    fun getInvoicelistPlaylist(
        @Field("UserID")
        userID: String?,
        @Field("Flag")
        flag: String?): Call<InvoiceListModel?>?

    /* TODO InvoiceReceiptFragment */
    @POST("invoicedetaildownload")
    @FormUrlEncoded
    fun getInvoiceDetailPlaylist(
        @Field("UserID")
        userID: String?,
        @Field("InvoiceId")
        invoiceId: String?,
        @Field("Flag")
        flag: String?): Call<InvoiceDetailModel?>?

}