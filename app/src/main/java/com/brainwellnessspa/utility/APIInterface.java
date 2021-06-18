package com.brainwellnessspa.utility;

import com.brainwellnessspa.billingOrderModule.models.BillingAddressSaveModel;
import com.brainwellnessspa.billingOrderModule.models.BillingAddressViewModel;
import com.brainwellnessspa.billingOrderModule.models.CancelPlanModel;
import com.brainwellnessspa.billingOrderModule.models.CardModel;
import com.brainwellnessspa.billingOrderModule.models.CurrentPlanVieViewModel;
import com.brainwellnessspa.billingOrderModule.models.PayNowDetailsModel;
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel;
import com.brainwellnessspa.dashboardOldModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.dashboardOldModule.Models.AudioInterruptionModel;
import com.brainwellnessspa.dashboardOldModule.Models.AudioLikeModel;
import com.brainwellnessspa.dashboardOldModule.Models.CreatePlaylistModel;
import com.brainwellnessspa.dashboardOldModule.Models.DirectionModel;
import com.brainwellnessspa.dashboardOldModule.Models.LogoutModel;
import com.brainwellnessspa.dashboardOldModule.Models.MainAudioModel;
import com.brainwellnessspa.dashboardOldModule.Models.MainPlayListModel;
import com.brainwellnessspa.dashboardOldModule.Models.NextSessionViewModel;
import com.brainwellnessspa.dashboardOldModule.Models.PlaylistLikeModel;
import com.brainwellnessspa.dashboardOldModule.Models.PreviousAppointmentsModel;
import com.brainwellnessspa.dashboardOldModule.Models.ReminderStatusPlaylistModel;
import com.brainwellnessspa.dashboardOldModule.Models.RenamePlaylistModel;
import com.brainwellnessspa.dashboardOldModule.Models.SessionListModel;
import com.brainwellnessspa.dashboardOldModule.Models.SubPlayListModel;
import com.brainwellnessspa.dashboardModule.models.SucessModel;
import com.brainwellnessspa.dashboardOldModule.Models.SuggestionAudiosModel;
import com.brainwellnessspa.dashboardOldModule.Models.UnlockAudioList;
import com.brainwellnessspa.faqModule.models.FaqListModel;
import com.brainwellnessspa.invoicemodule.models.InvoiceDetailModel;
import com.brainwellnessspa.invoicemodule.models.InvoiceListModel;
import com.brainwellnessspa.membershipModule.Models.MembershipPlanListModel;
import com.brainwellnessspa.membershipModule.Models.RegisterModel;
import com.brainwellnessspa.referralModule.models.AllContactListModel;
import com.brainwellnessspa.referralModule.models.CheckReferCodeModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {

    /* TODO Membership Payment */
    @POST("payment")
    @FormUrlEncoded
    Call<RegisterModel> getMembershipPayment(
            @Field("PlanId") String planId,
            @Field("PlanFlag") String planFlag,
            @Field("TokenId") String CardId,
            @Field("MobileNo") String MobileNo,
            @Field("CountryCode") String CountryCode,
            @Field("Token") String token,
            @Field("DeviceType") String deviceType,
            @Field("DeviceID") String deviceID,
            @Field("ReferCode") String referCode);

    /* TODO AccountFragment */
    @POST("logout")
    @FormUrlEncoded
    Call<LogoutModel> getLogout(@Field("UserID") String otp,
                                @Field("Token") String token,
                                @Field("Type") String type);

    /* TODO AudioFaqActivity */
    @GET("faqlistmembership")
    Call<FaqListModel> getFaqListings();

    /* TODO Membership Plan List */
    @GET("planlist")
    Call<MembershipPlanListModel> getMembershipPlanList();

    /* TODO AudioFragment */
    @POST("homeaudioscreen")
    @FormUrlEncoded
    Call<MainAudioModel> getMainAudioLists(@Field("UserID") String userID);

    /* TODO AudioFragment */
    @POST("unlockaudiolist")
    @FormUrlEncoded
    Call<UnlockAudioList> getUnLockAudioList(@Field("UserID") String userID,
                                             @Field("Token") String token,
                                             @Field("DeviceType") String deviceType,
                                             @Field("DeviceID") String deviceID,
                                             @Field("VersionCode") String versionCode);


    /* TODO PlaylistFragment */
    @POST("getlibrary")
    @FormUrlEncoded
    Call<MainPlayListModel> getMainPlayLists(@Field("UserID") String userID);

    /* TODO MyPlaylistsFragment */
    @POST("playlistdetails")
    @FormUrlEncoded
    Call<SubPlayListModel> getSubPlayLists(@Field("UserID") String userID,
                                           @Field("PlaylistId") String playlistId);

    /* TODO MyPlaylistsFragment */
    @POST("addaudiosearch")
    @FormUrlEncoded
    Call<SuggestionAudiosModel> getAddSearchAudio(@Field("AudioName") String audioName,
                                                  @Field("PlaylistId") String playlistId);

    /* TODO BillingAddressFragment */
    @POST("billingaddress")
    @FormUrlEncoded
    Call<BillingAddressViewModel> getBillingAddressView(@Field("UserID") String userID);

    /* TODO BillingAddressFragment */
    @POST("payonbillingorder")
    @FormUrlEncoded
    Call<PayNowDetailsModel> getPayNowDetails(@Field("UserID") String userID,
                                              @Field("CardId") String cardId,
                                              @Field("PlanId") String planId,
                                              @Field("PlanType") String planType,
                                              @Field("invoicePayId") String invoicePayId,
                                              @Field("PlanStatus") String planStatus);

    /* TODO BillingAddressFragment */
    @POST("billingdetailsave")
    @FormUrlEncoded
    Call<BillingAddressSaveModel> getBillingAddressSave(@Field("UserID") String userID,
                                                        @Field("Name") String name,
                                                        @Field("Email") String email,
                                                        @Field("Country") String country,
                                                        @Field("AddressLine1") String addressLine1,
                                                        @Field("AddressLine2") String addressLine2,
                                                        @Field("Suburb") String suburb,
                                                        @Field("State") String state,
                                                        @Field("Postcode") String postcode);

    /* TODO CurrentPlanFragment */
    @POST("billingorder")
    @FormUrlEncoded
    Call<CurrentPlanVieViewModel> getCurrentPlanView(@Field("UserID") String userID);

    /* TODO CancelMembershipActivity */
    @POST("cancelplan")
    @FormUrlEncoded
    Call<CancelPlanModel> getCancelPlan(@Field("UserID") String userID,
                                        @Field("CancelId") String cancelId,
                                        @Field("CancelReason") String cancelReason);

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
    Call<SessionListModel> getAppointmentSession(@Field("UserID") String userID,
                                                 @Field("AppointmentName") String appointmentName
    );

    /* TODO AppointmentFragment */
    @POST("appointmentdetail")
    @FormUrlEncoded
    Call<AppointmentDetailModel> getAppointmentDetails(@Field("UserID") String userID,
                                                       @Field("AppointmentTypeId") String appointmentTypeId);


    /* TODO ContactBookActivity */
    @POST("addnewreferuser")
    @FormUrlEncoded
    Call<AllContactListModel> SetContactList(@Field("UserID") String userID,
                                             @Field("ToUser") String toUser,
                                             @Field("ReferCode") String referCode
    );

    /* TODO OrderSummaryActivity */
    @POST("checkrefercode")
    @FormUrlEncoded
    Call<CheckReferCodeModel> CheckReferCode(@Field("ReferCode") String referCode);

    /* TODO PlayWellnessActivity */
    @POST("recentlyplayed")
    @FormUrlEncoded
    Call<SucessModel> getRecentlyplayed(@Field("AudioId") String audioId,
                                        @Field("UserID") String userID);

    /* TODO AddQueueActivity */
    @POST("audiodetail")
    @FormUrlEncoded
    Call<DirectionModel> getAudioDetailLists(@Field("UserID") String userID,
                                             @Field("AudioId") String audioId);


    /* TODO MembershipChangeActivity */
    @POST("planlistonbilling")
    @FormUrlEncoded
    Call<PlanListBillingModel> getPlanListBilling(@Field("UserID") String userID);


    /* TODO AddQueueActivity */
    @POST("audiolike")
    @FormUrlEncoded
    Call<AudioLikeModel> getAudioLike(@Field("AudioId") String audioId,
                                      @Field("UserID") String userID);

    /* TODO MyPlaylistActivity */
    @POST("playlistlike")
    @FormUrlEncoded
    Call<PlaylistLikeModel> getPlaylistLike(@Field("PlaylistId") String audioId,
                                            @Field("UserID") String userID);

    /* TODO PlaylistFragment & AddPlaylistActivity*/
    @POST("createplaylist")
    @FormUrlEncoded
    Call<CreatePlaylistModel> getCreatePlaylist(@Field("UserID") String userID,
                                                @Field("PlaylistName") String playlistName);

    /* TODO MyPlaylistActivity */
    @POST("renameplaylist")
    @FormUrlEncoded
    Call<RenamePlaylistModel> getRenamePlaylist(@Field("UserID") String userID,
                                                @Field("PlaylistId") String playlistId,
                                                @Field("PlaylistNewName") String playlistNewName);

    /* TODO MyPlaylistActivity */
    @POST("removeaudiofromplaylist")
    @FormUrlEncoded
    Call<SucessModel> getRemoveAudioFromPlaylist(@Field("UserID") String userID,
                                                 @Field("AudioId") String audioId,
                                                 @Field("PlaylistId") String playlistId);

    /* TODO MyPlaylistsFragment */
    @POST("deleteplaylist")
    @FormUrlEncoded
    Call<SucessModel> getDeletePlaylist(@Field("UserID") String userID,
                                        @Field("PlaylistId") String playlistId);


    /* TODO set Shorted Audio from created Playlist */
    @POST("sortingplaylistaudio")
    @FormUrlEncoded
    Call<CardModel> setShortedAudio(@Field("UserID") String userID,
                                    @Field("PlaylistId") String playListId,
                                    @Field("PlaylistAudioId") String audioId);

    /* TODO InvoiceActivity */
    @POST("invoicelist")
    @FormUrlEncoded
    Call<InvoiceListModel> getInvoicelistPlaylist(@Field("UserID") String userID,
                                                  @Field("Flag") String flag);

    /* TODO InvoiceReceiptFragment */
    @POST("invoicedetaildownload")
    @FormUrlEncoded
    Call<InvoiceDetailModel> getInvoiceDetailPlaylist(@Field("UserID") String userID,
                                                      @Field("InvoiceId") String invoiceId,
                                                      @Field("Flag") String flag);

    /* TODO ReminderActivity */
    @POST("reminderstatus")
    @FormUrlEncoded
    Call<ReminderStatusPlaylistModel> getReminderStatusPlaylist(@Field("UserID") String userID,
                                                                @Field("PlaylistId") String playlistId,
                                                                @Field("ReminderStatus") String reminderStatus);
 
    /* TODO MiniPlayerFragment & AudioPlayerActivity */
    @POST("audiointerruption")
    @FormUrlEncoded
    Call<AudioInterruptionModel> getAudioInterruption(@Field("userId") String userId,
                                                      @Field("audioId") String audioId,
                                                      @Field("audioName") String audioName,
                                                      @Field("audioDescription") String audioDescription,
                                                      @Field("directions") String directions,
                                                      @Field("masterCategory") String masterCategory,
                                                      @Field("subCategory") String subCategory,
                                                      @Field("audioDuration") String audioDuration,
                                                      @Field("bitRate") String bitRate,
                                                      @Field("audioType") String audioType,
                                                      @Field("playerType") String playerType,
                                                      @Field("sound") String sound,
                                                      @Field("audioService") String audioService,
                                                      @Field("source") String source,
                                                      @Field("position") String position,
                                                      @Field("seekPosition") String seekPosition,
                                                      @Field("interruptionMethod") String interruptionMethod,
                                                      @Field("batteryLevel") int batteryLevel,
                                                      @Field("batteryState") String batteryState,
                                                      @Field("internetDownSpeed") Float internetDownSpeed,
                                                      @Field("internetUpSpeed") Float internetUpSpeed);
}