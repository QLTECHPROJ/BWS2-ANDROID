package com.qltech.bws.Utility;

import com.qltech.bws.BillingOrderModule.Models.CancelPlanModel;
import com.qltech.bws.DashboardModule.Models.AudioLikeModel;
import com.qltech.bws.DashboardModule.Models.DirectionModel;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Models.MainPlayModel;
import com.qltech.bws.DashboardModule.Models.RenamePlaylistModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.InvoiceModule.Models.InvoiceDetailModel;
import com.qltech.bws.InvoiceModule.Models.InvoiceListModel;
import com.qltech.bws.LoginModule.Models.CountryListModel;
import com.qltech.bws.LoginModule.Models.LoginModel;
import com.qltech.bws.LoginModule.Models.OtpModel;
import com.qltech.bws.ResourceModule.Models.ResourceListModel;
import com.qltech.bws.SplashModule.Models.VersionModel;
import com.qltech.bws.UserModule.Models.AddProfileModel;
import com.qltech.bws.UserModule.Models.ProfileUpdateModel;
import com.qltech.bws.UserModule.Models.ProfileViewModel;
import com.qltech.bws.UserModule.Models.RemoveProfileModel;

import java.io.File;
import java.util.List;

import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {
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
    Call<MainPlayModel> getMainPlayLists(@Field("UserID") String userID);

    /* TODO AddQueueActivity */
    @POST("audiodetail")
    @FormUrlEncoded
    Call<DirectionModel> getAudioDetailLists(@Field("AudioId") String audioId);

    /* TODO AddQueueActivity */
    @POST("audiolike")
    @FormUrlEncoded
    Call<AudioLikeModel> getAudioLike(@Field("AudioId") String audioId,
                                      @Field("UserID") String userID);

    /* TODO AddQueueActivity */
    @POST("downloads")
    @FormUrlEncoded
    Call<SucessModel> getAudioDownloads(@Field("AudioId") String audioId,
                                        @Field("UserID") String userID);

    /* TODO PlaylistFragment & AddPlaylistActivity*/
    @POST("createplaylist")
    @FormUrlEncoded
    Call<SucessModel> getCreatePlaylist(@Field("UserID") String userID,
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

    /* TODO AppointmentInvoiceFragment & MembershipInvoiceFragment */
    @POST("invoicedownloaddetail")
    @FormUrlEncoded
    Call<InvoiceDetailModel> getInvoiceDetailPlaylist(@Field("UserID") String userID,
                                                      @Field("InvoiceId") String invoiceId);

    /* TODO CancelMembershipActivity */
    @POST("cancelplan")
    @FormUrlEncoded
    Call<CancelPlanModel> getCancelPlan(@Field("UserID") String userID,
                                        @Field("CancelId") String cancelId,
                                        @Field("CancelReason") String cancelReason);
}