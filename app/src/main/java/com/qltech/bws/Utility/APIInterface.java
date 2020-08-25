package com.qltech.bws.Utility;

import com.qltech.bws.BillingOrderModule.Models.BillingAddressSaveModel;
import com.qltech.bws.BillingOrderModule.Models.BillingAddressViewModel;
import com.qltech.bws.BillingOrderModule.Models.CancelPlanModel;
import com.qltech.bws.BillingOrderModule.Models.CardListModel;
import com.qltech.bws.BillingOrderModule.Models.CardModel;
import com.qltech.bws.BillingOrderModule.Models.CurrentPlanVieViewModel;
import com.qltech.bws.DashboardModule.Models.AudioLikeModel;
import com.qltech.bws.DashboardModule.Models.CreatePlaylistModel;
import com.qltech.bws.DashboardModule.Models.DirectionModel;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Models.MainPlayListModel;
import com.qltech.bws.DashboardModule.Models.PreviousAppointmentsModel;
import com.qltech.bws.DashboardModule.Models.RenamePlaylistModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Models.SuggestionAudiosModel;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.FaqModule.Models.FaqListModel;
import com.qltech.bws.InvoiceModule.Models.InvoiceDetailModel;
import com.qltech.bws.InvoiceModule.Models.InvoiceListModel;
import com.qltech.bws.LoginModule.Models.CountryListModel;
import com.qltech.bws.LoginModule.Models.LoginModel;
import com.qltech.bws.LoginModule.Models.OtpModel;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
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
import retrofit2.http.POST;

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
    /* TODO CountryActivity */
    @GET("countrylist")
    Call<MembershipPlanListModel> getMembershipPlanList();

    /*TODO UserProfileActivity */
    @POST("addprofileimage")
    @FormUrlEncoded
    Call<AddProfileModel> getAddProfile(@Field("UserID") String userID,
                                        @Field("ProfileImage") TypedFile profileImage);

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

    /* TODO MyPlaylistsFragment */
    @POST("playlistdetails")
    @FormUrlEncoded
    Call<SubPlayListModel> getSubPlayLists(@Field("UserID") String userID,
                                           @Field("PlaylistId") String playlistId);

    /* TODO MyPlaylistsFragment */
    @POST("addaudiosearch")
    @FormUrlEncoded
    Call<SuggestionAudiosModel> getAddSearchAudio(@Field("AudioName") String audioName);

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
    @POST("appointmentcategorylist")
    @FormUrlEncoded
    Call<PreviousAppointmentsModel> getAppointmentVIew(@Field("UserID") String userID);

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
    Call<CardModel> getAddCard(@Field("UserID") String userID,
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

    /* TODO AddQueueActivity & PlayWellnessActivity */
    @POST("downloads")
    @FormUrlEncoded
    Call<SucessModel> getDownloadlistPlaylist(@Field("UserID") String userID,
                                              @Field("AudioId") String audioId,
                                              @Field("PlaylistId") String playlistId);

    /* TODO InvoiceActivity */
    @POST("invoicelist")
    @FormUrlEncoded
    Call<InvoiceListModel> getInvoicelistPlaylist(@Field("UserID") String userID);

    /* TODO InvoiceReceiptFragment */
    @POST("invoicedetaildownload")
    @FormUrlEncoded
    Call<InvoiceDetailModel> getInvoiceDetailPlaylist(@Field("UserID") String userID,
                                                      @Field("InvoiceId") String invoiceId);

    /* TODO DownloadsActivity */
    @POST("downloadlist")
    @FormUrlEncoded
    Call<DownloadlistModel> getDownloadlistPlaylist(@Field("UserID") String userID);

    /* TODO ResourceActivity */
    @POST("resourcelist")
    @FormUrlEncoded
    Call<ResourceListModel> getResourcLists(@Field("UserID") String userID,
                                            @Field("ResourceTypeId") String resourceTypeId,
                                            @Field("Category") String category);

}