package com.brainwellnessspa.utility

import com.brainwellnessspa.billingOrderModule.models.BillingAddressSaveModel
import com.brainwellnessspa.billingOrderModule.models.BillingAddressViewModel
import com.brainwellnessspa.billingOrderModule.models.CancelPlanModel
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel
import com.brainwellnessspa.dashboardModule.models.AudioInterruptionModel
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.dashboardOldModule.models.AppointmentDetailModel
import com.brainwellnessspa.dashboardOldModule.models.NextSessionViewModel
import com.brainwellnessspa.dashboardOldModule.models.PreviousAppointmentsModel
import com.brainwellnessspa.dashboardOldModule.models.SessionListModel
import com.brainwellnessspa.invoiceModule.models.InvoiceDetailModel
import com.brainwellnessspa.invoiceModule.models.InvoiceListModel
import com.brainwellnessspa.referralModule.models.AllContactListModel
import com.brainwellnessspa.referralModule.models.CheckReferCodeModel
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

    /* TODO AppointmentFragment */
    @POST("nextsessionview")
    @FormUrlEncoded
    fun getNextSessionVIew(
        @Field("UserID")
        userID: String?): Call<NextSessionViewModel?>?

    /* TODO AppointmentFragment */
    @POST("appointmentcategorylist")
    @FormUrlEncoded
    fun getAppointmentVIew(
        @Field("UserID")
        userID: String?): Call<PreviousAppointmentsModel?>?

    /* TODO AppointmentFragment */
    @POST("appointmentsession")
    @FormUrlEncoded
    fun getAppointmentSession(
        @Field("UserID")
        userID: String?,
        @Field("AppointmentName")
        appointmentName: String?): Call<SessionListModel?>?

    /* TODO AppointmentFragment */
    @POST("appointmentdetail")
    @FormUrlEncoded
    fun getAppointmentDetails(
        @Field("UserID")
        userID: String?,
        @Field("AppointmentTypeId")
        appointmentTypeId: String?): Call<AppointmentDetailModel?>?

    /* TODO ContactBookActivity */
    @POST("addnewreferuser")
    @FormUrlEncoded
    fun setContactList(
        @Field("UserID")
        userID: String?,
        @Field("ToUser")
        toUser: String?,
        @Field("ReferCode")
        referCode: String?): Call<AllContactListModel?>?

    /* TODO OrderSummaryActivity */
    @POST("checkrefercode")
    @FormUrlEncoded
    fun checkReferCode(
        @Field("ReferCode")
        referCode: String?): Call<CheckReferCodeModel?>?

    /* TODO PlayWellnessActivity */
    @POST("recentlyplayed")
    @FormUrlEncoded
    fun getRecentlyplayed(
        @Field("AudioId")
        audioId: String?,
        @Field("UserID")
        userID: String?): Call<SucessModel?>?

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

    /* TODO MiniPlayerFragment & AudioPlayerActivity */
    @POST("audiointerruption")
    @FormUrlEncoded
    fun getAudioInterruption(
        @Field("userId")
        userId: String?,
        @Field("audioId")
        audioId: String?,
        @Field("audioName")
        audioName: String?,
        @Field("audioDescription")
        audioDescription: String?,
        @Field("directions")
        directions: String?,
        @Field("masterCategory")
        masterCategory: String?,
        @Field("subCategory")
        subCategory: String?,
        @Field("audioDuration")
        audioDuration: String?,
        @Field("bitRate")
        bitRate: String?,
        @Field("audioType")
        audioType: String?,
        @Field("playerType")
        playerType: String?,
        @Field("sound")
        sound: String?,
        @Field("audioService")
        audioService: String?,
        @Field("source")
        source: String?,
        @Field("position")
        position: String?,
        @Field("seekPosition")
        seekPosition: String?,
        @Field("interruptionMethod")
        interruptionMethod: String?,
        @Field("batteryLevel")
        batteryLevel: Int,
        @Field("batteryState")
        batteryState: String?,
        @Field("internetDownSpeed")
        internetDownSpeed: Float?,
        @Field("internetUpSpeed")
        internetUpSpeed: Float?): Call<AudioInterruptionModel?>?
}