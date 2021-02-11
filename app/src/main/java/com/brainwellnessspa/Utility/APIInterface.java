package com.brainwellnessspa.Utility;

import com.brainwellnessspa.AddPayment.Model.AddCardModel;
import com.brainwellnessspa.BillingOrderModule.Models.BillingAddressSaveModel;
import com.brainwellnessspa.BillingOrderModule.Models.BillingAddressViewModel;
import com.brainwellnessspa.BillingOrderModule.Models.CancelPlanModel;
import com.brainwellnessspa.BillingOrderModule.Models.CardListModel;
import com.brainwellnessspa.BillingOrderModule.Models.CardModel;
import com.brainwellnessspa.BillingOrderModule.Models.CurrentPlanVieViewModel;
import com.brainwellnessspa.BillingOrderModule.Models.PayNowDetailsModel;
import com.brainwellnessspa.BillingOrderModule.Models.PlanListBillingModel;
import com.brainwellnessspa.DashboardModule.Models.AddToPlaylist;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.AudioLikeModel;
import com.brainwellnessspa.DashboardModule.Models.CreatePlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.DirectionModel;
import com.brainwellnessspa.DashboardModule.Models.DownloadPlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.LogoutModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.MainPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.NextSessionViewModel;
import com.brainwellnessspa.DashboardModule.Models.PlaylistLikeModel;
import com.brainwellnessspa.DashboardModule.Models.PlaylistingModel;
import com.brainwellnessspa.DashboardModule.Models.PreviousAppointmentsModel;
import com.brainwellnessspa.DashboardModule.Models.ReminderStatusPlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.RenamePlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.SearchBothModel;
import com.brainwellnessspa.DashboardModule.Models.SearchPlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.SessionListModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SucessModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestedModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestionAudiosModel;
import com.brainwellnessspa.DashboardModule.Models.UnlockAudioList;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllPlayListModel;
import com.brainwellnessspa.DownloadModule.Models.DownloadlistModel;
import com.brainwellnessspa.FaqModule.Models.FaqListModel;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceDetailModel;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceListModel;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.LoginModule.Models.CountryListModel;
import com.brainwellnessspa.LoginModule.Models.LoginModel;
import com.brainwellnessspa.LoginModule.Models.OtpModel;
import com.brainwellnessspa.MembershipModule.Models.MembershipPlanListModel;
import com.brainwellnessspa.MembershipModule.Models.RegisterModel;
import com.brainwellnessspa.MembershipModule.Models.SignUpModel;
import com.brainwellnessspa.ReferralModule.Model.AllContactListModel;
import com.brainwellnessspa.ReferralModule.Model.CheckReferCodeModel;
import com.brainwellnessspa.ReminderModule.Models.DeleteRemiderModel;
import com.brainwellnessspa.ReminderModule.Models.RemiderDetailsModel;
import com.brainwellnessspa.ReminderModule.Models.ReminderStatusModel;
import com.brainwellnessspa.ReminderModule.Models.SelectPlaylistModel;
import com.brainwellnessspa.ReminderModule.Models.SetReminderModel;
import com.brainwellnessspa.ResourceModule.Models.ResourceFilterModel;
import com.brainwellnessspa.ResourceModule.Models.ResourceListModel;
import com.brainwellnessspa.SplashModule.Models.VersionModel;
import com.brainwellnessspa.UserModule.Models.AddProfileModel;
import com.brainwellnessspa.UserModule.Models.ProfileUpdateModel;
import com.brainwellnessspa.UserModule.Models.ProfileViewModel;
import com.brainwellnessspa.UserModule.Models.RemoveProfileModel;

import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {

  /*  @Multipart
    @POST("/addprofileimage")
    void getAddProfile(@Part("UserID") String UserID,
                       @Part("ProfileImage") TypedFile Avtar,
                       Callback<AddProfileModel> addProfileModelCallback);*/

    /*TODO UserProfileActivity */

    @Multipart
    @POST("addprofileimage")
    Call<AddProfileModel> getAddProfile(@Part("UserID") String userID,
                                        @Part("ProfileImage") TypedFile profileImage);

    /*TODO App Version*/
    @POST("appversion")
    @FormUrlEncoded
    Call<VersionModel> getVersionDatas(@Field("Version") String version,
                                       @Field("AppType") String appType);

    /* TODO LoginActivtiy & OtpActivity */
    @POST("sendotp")
    @FormUrlEncoded
    Call<LoginModel> getLoginDatas(@Field("MobileNo") String mobileNo,
                                   @Field("CountryCode") String countryCode,
                                   @Field("DeviceType") String deviceType,
                                   @Field("Resend") String resend,
                                   @Field("key") String key);

    /* TODO Registration */
    @POST("signupcheckout")
    @FormUrlEncoded
    Call<LoginModel> getRegData(@Field("MobileNo") String mobileNo,
                                @Field("CountryCode") String countryCode);

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

    /* TODO CheckoutGetCodeActivity */
    @POST("signupcheckout")
    @FormUrlEncoded
    Call<SignUpModel> getSignUpDatas(@Field("MobileNo") String mobileNo,
                                     @Field("CountryCode") String countryCode,
                                     @Field("DeviceType") String deviceType,
                                     @Field("Resend") String resend,
                                     @Field("key") String key);

    /* TODO OtpActivity */
    @POST("authotp")
    @FormUrlEncoded
    Call<OtpModel> getAuthOtps(@Field("OTP") String otp,
                               @Field("Token") String token,
                               @Field("DeviceType") String deviceType,
                               @Field("DeviceID") String deviceID,
                               @Field("MobileNo") String mobileNo,
                               @Field("SignupFlag") String signupFlag);

    /* TODO OtpActivity */
    @POST("authotp")
    @FormUrlEncoded
    Call<OtpModel> getAuthOtps1(@Field("OTP") String otp,
                                @Field("Token") String token,
                                @Field("DeviceType") String deviceType,
                                @Field("DeviceID") String deviceID,
                                @Field("MobileNo") String mobileNo,
                                @Field("SignupFlag") String signupFlag);

    /* TODO AccountFragment */
    @POST("logout")
    @FormUrlEncoded
    Call<LogoutModel> getLogout(@Field("UserID") String otp,
                                @Field("Token") String token,
                                @Field("Type") String type);

    /* TODO CountryActivity */
    @GET("countrylist")
    Call<CountryListModel> getCountryLists();

    /* TODO AudioFaqActivity */
    @GET("faqlist")
    Call<FaqListModel> getFaqLists();

    /* TODO AudioFaqActivity */
    @GET("faqlistmembership")
    Call<FaqListModel> getFaqListings();

    /* TODO UserProfileActivity */
    @POST("profiledetail")
    @FormUrlEncoded
    Call<ProfileViewModel> getProfileView(@Field("UserID") String userID);

    /*TODO UserProfileActivity */
    @POST("profileupdate")
    @FormUrlEncoded
    Call<ProfileUpdateModel> getProfileUpdate(@Field("UserID") String userID,
                                              @Field("Name") String name,
                                              @Field("Dob") String dob,
                                              @Field("MobileNo") String mobileNo,
                                              @Field("EmailId") String emailId,
                                              @Field("IsVerify") String isVerify);

    /* TODO Membership Plan List */
    @GET("planlist")
    Call<MembershipPlanListModel> getMembershipPlanList();

    /*TODO UserProfileActivity */
    @POST("removeprofileimage")
    @FormUrlEncoded
    Call<RemoveProfileModel> getRemoveProfile(@Field("UserID") String userID);

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

    /* TODO ViewAllPlaylistFragment */
    @POST("playlistongetlibrary")
    @FormUrlEncoded
    Call<ViewAllPlayListModel> getViewAllPlayLists(@Field("UserID") String userID,
                                                   @Field("GetLibraryId") String getLibraryId);

    /* TODO ViewAllAudioFragment */
    @POST("gethomeallaudio")
    @FormUrlEncoded
    Call<ViewAllAudioListModel> getViewAllAudioLists(@Field("UserID") String userID,
                                                     @Field("GetHomeId") String HomeId,
                                                     @Field("CategoryName") String CategoryName);

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

    /* TODO AddAudioActivity & SearchFragment */
    @POST("suggestedaudio")
    @FormUrlEncoded
    Call<SuggestedModel> getSuggestedLists(@Field("UserID") String userID);

    /* TODO SearchFragment */
    @POST("suggestedplaylist")
    @FormUrlEncoded
    Call<SearchPlaylistModel> getSuggestedPlayLists(@Field("UserID") String userID);

    /* TODO SearchFragment */
    @POST("searchonsuggestedlist")
    @FormUrlEncoded
    Call<SearchBothModel> getSearchBoth(@Field("UserID") String userID,
                                        @Field("SuggestedName") String suggestedName);

    /* TODO MyPlaylistsFragment */
    @POST("addaudiotoplaylist")
    @FormUrlEncoded
    Call<AddToPlaylist> getAddSearchAudioFromPlaylist(@Field("UserID") String userID,
                                                      @Field("AudioId") String audioId,
                                                      @Field("PlaylistId") String playlistId,
                                                      @Field("FromPlaylistId") String fromPlaylistId);

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

    /* TODO ReminderActivity */
    @POST("setreminder")
    @FormUrlEncoded
    Call<SetReminderModel> SetReminder(@Field("PlaylistId") String PlaylistId,
                                       @Field("UserID") String userID,
                                       @Field("IsSingle") String IsSingle,
                                       @Field("ReminderTime") String ReminderTime,
                                       @Field("ReminderDay") String ReminderDay
    );

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

    /* TODO AddPaymentActivity */
    @POST("cardadd")
    @FormUrlEncoded
    Call<AddCardModel> getAddCard(@Field("UserID") String userID,
                                  @Field("TokenId") String tokenId);

    /* TODO MembershipChangeActivity */
    @POST("planlistonbilling")
    @FormUrlEncoded
    Call<PlanListBillingModel> getPlanListBilling(@Field("UserID") String userID);

    /* TODO PaymentFragment & AllCardAdapter*/
    @POST("cardlist")
    @FormUrlEncoded
    Call<CardListModel> getCardLists(@Field("UserID") String userID);

    /* TODO AllCardAdapter */
    @POST("carddefault")
    @FormUrlEncoded
    Call<CardListModel> getChangeCard(@Field("UserID") String userID,
                                      @Field("CardId") String cardId);

    /* TODO AllCardAdapter */
    @POST("cardremove")
    @FormUrlEncoded
    Call<CardModel> getRemoveCard(@Field("UserID") String userID,
                                  @Field("CardId") String cardId);

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

    /* TODO AddPlaylistActivity */
    @POST("playlist")
    @FormUrlEncoded
    Call<PlaylistingModel> getPlaylisting(@Field("UserID") String userID);

    /* TODO set Shorted Audio from created Playlist */
    @POST("sortingplaylistaudio")
    @FormUrlEncoded
    Call<CardModel> setShortedAudio(@Field("UserID") String userID,
                                    @Field("PlaylistId") String playListId,
                                    @Field("PlaylistAudioId") String audioId);

    /* TODO AddQueueActivity & PlayWellnessActivity */
    @POST("downloads")
    @FormUrlEncoded
    Call<DownloadPlaylistModel> getDownloadlistPlaylist(@Field("UserID") String userID,
                                                        @Field("AudioId") String audioId,
                                                        @Field("PlaylistId") String playlistId);

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

    /* TODO DownloadsActivity */
    @POST("downloadlist")
    @FormUrlEncoded
    Call<DownloadlistModel> getDownloadlistPlaylist(@Field("UserID") String userID);

    /* TODO LikeAudiosFragment & LikePlaylistsFragment */
    @POST("likelist")
    @FormUrlEncoded
    Call<LikesHistoryModel> getLikeAudioPlaylistListing(@Field("UserID") String userID);

    /* TODO AppsFragment & AudioBooksFragment & DocumentariesFragment & PodcastsFragment & WebsiteFragment */
    @POST("resourcelist")
    @FormUrlEncoded
    Call<ResourceListModel> getResourcLists(@Field("UserID") String userID,
                                            @Field("ResourceTypeId") String resourceTypeId,
                                            @Field("Category") String category);

    /* TODO ResourceActivity */
    @POST("resourcecategorylist")
    @FormUrlEncoded
    Call<ResourceFilterModel> getResourcFilterLists(@Field("UserID") String userID);


    /* TODO ReminderDetailsActivity */
    @POST("getreminder")
    @FormUrlEncoded
    Call<RemiderDetailsModel> getGetReminderStatus(@Field("UserID") String userID);

    /* TODO ReminderDetailsActivity */
    @POST("deletereminder")
    @FormUrlEncoded
    Call<DeleteRemiderModel> getDeleteRemiderStatus(@Field("UserID") String userID,
                                                    @Field("ReminderId") String reminderId);

    /* TODO ReminderActivity */
    @POST("reminderstatus")
    @FormUrlEncoded
    Call<ReminderStatusModel> getReminderStatus(@Field("UserID") String userID,
                                                @Field("PlaylistId") String playlistId,
                                                @Field("ReminderStatus") String reminderStatus);

    /* TODO ReminderActivity */
    @POST("reminderstatus")
    @FormUrlEncoded
    Call<ReminderStatusPlaylistModel> getReminderStatusPlaylist(@Field("UserID") String userID,
                                                                @Field("PlaylistId") String playlistId,
                                                                @Field("ReminderStatus") String reminderStatus);

    /* TODO SelectPlaylistActivity */
    @POST("allplaylist")
    @FormUrlEncoded
    Call<SelectPlaylistModel> getAllPlayListing(@Field("UserID") String userID);
}