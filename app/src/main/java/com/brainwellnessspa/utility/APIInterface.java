package com.brainwellnessspa.utility;

import com.brainwellnessspa.billingOrderModule.models.BillingAddressSaveModel;
import com.brainwellnessspa.billingOrderModule.models.BillingAddressViewModel;
import com.brainwellnessspa.billingOrderModule.models.CancelPlanModel;
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel;
import com.brainwellnessspa.dashboardModule.models.AudioInterruptionModel;
import com.brainwellnessspa.dashboardModule.models.SucessModel;
import com.brainwellnessspa.dashboardOldModule.models.AppointmentDetailModel;
import com.brainwellnessspa.dashboardOldModule.models.NextSessionViewModel;
import com.brainwellnessspa.dashboardOldModule.models.PreviousAppointmentsModel;
import com.brainwellnessspa.dashboardOldModule.models.SessionListModel;
import com.brainwellnessspa.invoiceModule.models.InvoiceDetailModel;
import com.brainwellnessspa.invoiceModule.models.InvoiceListModel;
import com.brainwellnessspa.referralModule.models.AllContactListModel;
import com.brainwellnessspa.referralModule.models.CheckReferCodeModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {

    /* TODO BillingAddressFragment */
    @POST("billingaddress")
    @FormUrlEncoded
    Call<BillingAddressViewModel> getBillingAddressView(@Field("UserID") String userID);

    /* TODO BillingAddressFragment */
    @POST("billingdetailsave")
    @FormUrlEncoded
    Call<BillingAddressSaveModel> getBillingAddressSave(@Field("UserID") String userID, @Field("Name") String name, @Field("Email") String email, @Field("Country") String country, @Field("AddressLine1") String addressLine1, @Field("AddressLine2") String addressLine2, @Field("Suburb") String suburb, @Field("State") String state, @Field("Postcode") String postcode);

    /* TODO CancelMembershipActivity */
    @POST("cancelplan")
    @FormUrlEncoded
    Call<CancelPlanModel> getCancelPlan(@Field("UserID") String userID, @Field("CancelId") String cancelId, @Field("CancelReason") String cancelReason);

    /* TODO AppointmentFragment */
    @POST("nextsessionview")
    @FormUrlEncoded
    Call<NextSessionViewModel> getNextSessionVIew(@Field("UserID") String userID);

    /* TODO AppointmentFragment */
    @POST("appointmentcategorylist")
    @FormUrlEncoded
    Call<PreviousAppointmentsModel> getAppointmentVIew(@Field("UserID") String userID);

    /* TODO AppointmentFragment */
    @POST("appointmentsession")
    @FormUrlEncoded
    Call<SessionListModel> getAppointmentSession(@Field("UserID") String userID, @Field("AppointmentName") String appointmentName);

    /* TODO AppointmentFragment */
    @POST("appointmentdetail")
    @FormUrlEncoded
    Call<AppointmentDetailModel> getAppointmentDetails(@Field("UserID") String userID, @Field("AppointmentTypeId") String appointmentTypeId);

    /* TODO ContactBookActivity */
    @POST("addnewreferuser")
    @FormUrlEncoded
    Call<AllContactListModel> SetContactList(@Field("UserID") String userID, @Field("ToUser") String toUser, @Field("ReferCode") String referCode);

    /* TODO OrderSummaryActivity */
    @POST("checkrefercode")
    @FormUrlEncoded
    Call<CheckReferCodeModel> CheckReferCode(@Field("ReferCode") String referCode);

    /* TODO PlayWellnessActivity */
    @POST("recentlyplayed")
    @FormUrlEncoded
    Call<SucessModel> getRecentlyplayed(@Field("AudioId") String audioId, @Field("UserID") String userID);

    /* TODO MembershipChangeActivity */
    @POST("planlistonbilling")
    @FormUrlEncoded
    Call<PlanListBillingModel> getPlanListBilling(@Field("UserID") String userID);

    /* TODO InvoiceActivity */
    @POST("invoicelist")
    @FormUrlEncoded
    Call<InvoiceListModel> getInvoicelistPlaylist(@Field("UserID") String userID, @Field("Flag") String flag);

    /* TODO InvoiceReceiptFragment */
    @POST("invoicedetaildownload")
    @FormUrlEncoded
    Call<InvoiceDetailModel> getInvoiceDetailPlaylist(@Field("UserID") String userID, @Field("InvoiceId") String invoiceId, @Field("Flag") String flag);

    /* TODO MiniPlayerFragment & AudioPlayerActivity */
    @POST("audiointerruption")
    @FormUrlEncoded
    Call<AudioInterruptionModel> getAudioInterruption(@Field("userId") String userId, @Field("audioId") String audioId, @Field("audioName") String audioName, @Field("audioDescription") String audioDescription, @Field("directions") String directions, @Field("masterCategory") String masterCategory, @Field("subCategory") String subCategory, @Field("audioDuration") String audioDuration, @Field("bitRate") String bitRate, @Field("audioType") String audioType, @Field("playerType") String playerType, @Field("sound") String sound, @Field("audioService") String audioService, @Field("source") String source, @Field("position") String position, @Field("seekPosition") String seekPosition, @Field("interruptionMethod") String interruptionMethod, @Field("batteryLevel") int batteryLevel, @Field("batteryState") String batteryState, @Field("internetDownSpeed") Float internetDownSpeed, @Field("internetUpSpeed") Float internetUpSpeed);
}