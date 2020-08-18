package com.qltech.bws.Utility;

import com.qltech.bws.DashboardModule.Models.DirectionModel;
import com.qltech.bws.LoginModule.Models.CountryListModel;
import com.qltech.bws.LoginModule.Models.LoginModel;
import com.qltech.bws.LoginModule.Models.OtpModel;
import com.qltech.bws.SplashModule.Models.VersionModel;

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
                               @Field("MobileNo") String mobileNo);

/* TODO CountryActivity */
    @GET("countrylist")
    Call<CountryListModel> getCountryLists();

/* TODO AddQueueActivity */
    @POST("audiodetail")
    @FormUrlEncoded
    Call<DirectionModel> getAudioDetailLists(@Field("AudioId") String audioId);
}