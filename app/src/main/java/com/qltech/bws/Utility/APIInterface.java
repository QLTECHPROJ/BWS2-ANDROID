package com.qltech.bws.Utility;

import com.qltech.bws.AddPayment.Model.AddCardModel;
import com.qltech.bws.BillingOrderModule.Models.BillingAddressSaveModel;
import com.qltech.bws.BillingOrderModule.Models.BillingAddressViewModel;
import com.qltech.bws.BillingOrderModule.Models.CancelPlanModel;
import com.qltech.bws.BillingOrderModule.Models.CardListModel;
import com.qltech.bws.BillingOrderModule.Models.CardModel;
import com.qltech.bws.BillingOrderModule.Models.CurrentPlanVieViewModel;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.DashboardModule.Models.AudioLikeModel;
import com.qltech.bws.DashboardModule.Models.CreatePlaylistModel;
import com.qltech.bws.DashboardModule.Models.DirectionModel;
import com.qltech.bws.DashboardModule.Models.DownloadPlaylistModel;
import com.qltech.bws.DashboardModule.Models.LogoutModel;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Models.MainPlayListModel;
import com.qltech.bws.DashboardModule.Models.NextSessionViewModel;
import com.qltech.bws.DashboardModule.Models.PlaylistingModel;
import com.qltech.bws.DashboardModule.Models.PreviousAppointmentsModel;
import com.qltech.bws.DashboardModule.Models.RenamePlaylistModel;
import com.qltech.bws.DashboardModule.Models.SearchBothModel;
import com.qltech.bws.DashboardModule.Models.SearchPlaylistModel;
import com.qltech.bws.DashboardModule.Models.SessionListModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Models.SuggestedModel;
import com.qltech.bws.DashboardModule.Models.SuggestionAudiosModel;
import com.qltech.bws.DashboardModule.Models.ViewAllAudioListModel;
import com.qltech.bws.DashboardModule.Models.ViewAllPlayListModel;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.FaqModule.Models.FaqListModel;
import com.qltech.bws.InvoiceModule.Models.InvoiceDetailModel;
import com.qltech.bws.InvoiceModule.Models.InvoiceListModel;
import com.qltech.bws.LoginModule.Models.CountryListModel;
import com.qltech.bws.LoginModule.Models.LoginModel;
import com.qltech.bws.LoginModule.Models.OtpModel;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.ReminderModule.Models.ReminderStatusModel;
import com.qltech.bws.ReminderModule.Models.SetReminderModel;
import com.qltech.bws.ResourceModule.Models.ResourceFilterModel;
import com.qltech.bws.ResourceModule.Models.ResourceListModel;
import com.qltech.bws.SplashModule.Models.VersionModel;
import com.qltech.bws.UserModule.Models.AddProfileModel;
import com.qltech.bws.UserModule.Models.ProfileUpdateModel;
import com.qltech.bws.UserModule.Models.ProfileViewModel;
import com.qltech.bws.UserModule.Models.RemoveProfileModel;

import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {
    /*TODO App Version*/
    @POST("appversion")
    @FormUrlEncoded
    Call<VersionModel> getVersionDatas(@Field("Version") String version,
                                       @Field("AppType") String appType);

    /* TODO LoginActivtiy & OtpActivity & CheckoutGetCodeActivity*/
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
    Call<AddCardModel> getMembershipPayment(
            @Field("PlanId") String planId,
            @Field("PlanFlag") String planFlag,
            @Field("TokenId") String CardId,
            @Field("MobileNo") String UserId);

    /* TODO CheckoutGetCodeActivity & OtpActivity */
    @POST("signupcheckout")
    @FormUrlEncoded
    Call<LoginModel> getSignUpDatas(@Field("MobileNo") String mobileNo,
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
    Call<CardModel> getAuthOtps1(@Field("OTP") String otp,
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
    @Multipart
    @POST("addprofileimage")
    Call<AddProfileModel> getAddProfile(@Part("UserID") String userID,
                                        @Part("ProfileImage") TypedFile profileImage);

    /*TODO UserProfileActivity */
    @POST("removeprofileimage")
    @FormUrlEncoded
    Call<RemoveProfileModel> getRemoveProfile(@Field("UserID") String userID);

    /* TODO AudioFragment */
    @POST("homeaudioscreen")
    @FormUrlEncoded
    Call<MainAudioModel> getMainAudioLists(@Field("UserID") String userID);


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
                                                     @Field("GetHomeId") String HomeId);

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
    @GET("suggested")
    Call<SuggestedModel> getSuggestedLists();

    /* TODO SearchFragment */
    @GET("suggestedplaylist")
    Call<SearchPlaylistModel> getSuggestedPlayLists();

    /* TODO SearchFragment */
    @POST("searchonsuggestedlist")
    @FormUrlEncoded
    Call<SearchBothModel> getSearchBoth(@Field("SuggestedName") String suggestedName);

    /* TODO MyPlaylistsFragment */
    @POST("addaudiotoplaylist")
    @FormUrlEncoded
    Call<SucessModel> getAddSearchAudioFromPlaylist(@Field("UserID") String userID,
                                                    @Field("AudioId") String audioId,
                                                    @Field("PlaylistId") String playlistId);

    /* TODO BillingAddressFragment */
    @POST("billingaddress")
    @FormUrlEncoded
    Call<BillingAddressViewModel> getBillingAddressView(@Field("UserID") String userID);

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

    /* TODO PlayWellnessActivity */
    @POST("recentlyplayed")
    @FormUrlEncoded
    Call<SucessModel> getRecentlyplayed(@Field("AudioId") String audioId,
                                        @Field("UserID") String userID);

    /* TODO AddQueueActivity */
    @POST("audiodetail")
    @FormUrlEncoded
    Call<DirectionModel> getAudioDetailLists(@Field("AudioId") String audioId);

    /* TODO AddPaymentActivity */
    @POST("cardadd")
    @FormUrlEncoded
    Call<AddCardModel> getAddCard(@Field("UserID") String userID,
                                  @Field("TokenId") String tokenId);

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


    /* TODO ReminderActivity */
    @POST("reminderstatus")
    @FormUrlEncoded
    Call<ReminderStatusModel> getReminderStatus(@Field("UserID") String userID,
                                                @Field("ReminderStatus") String reminderStatus);
}