package com.brainwellnessspa.Utility;

import com.brainwellnessspa.LoginModule.Models.CountryListModel;
import com.brainwellnessspa.LoginModule.Models.LoginModel;
import com.brainwellnessspa.SplashModule.Models.VersionModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APINewInterface {
    @GET("countrylist")
    Call<CountryListModel> getCountryLists();

    @POST("appversion")
    @FormUrlEncoded
    Call<VersionModel> getAppVersions(@Field("Version") String version,
                                 @Field("AppType") String appType);

    @POST("login")
    @FormUrlEncoded
    Call<LoginModel> getLogins(@Field("Email") String email,
                               @Field("Password") String password);

    @POST("signup")
    @FormUrlEncoded
    Call<LoginModel> getSignUp(@Field("Name") String name,
                               @Field("Email") String email,
                               @Field("CountryCode") String countryCode,
                               @Field("MobileNo") String mobileNo,
                               @Field("DeviceType") String deviceType,
                               @Field("Password") String password);
}
