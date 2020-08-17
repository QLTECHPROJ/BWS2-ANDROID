package com.qltech.bws.Utility;

import com.qltech.bws.LoginModule.Models.CountryListModel;
import com.qltech.bws.LoginModule.Models.LoginModel;
import com.qltech.bws.SplashModule.Activities.Models.VersionModel;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {

    @POST("/appversion")
    @FormUrlEncoded
    Call<VersionModel> getVersionDatas(@Path("Version") String version,
                         @Path("AppType") String appType);

    @POST("/sendotp")
    @FormUrlEncoded
    Call<LoginModel> getLoginDatas(@Path("MobileNo") String mobileNo,
                       @Path("CountryCode") int countryCode,
                       @Path("DeviceType") String deviceType,
                       @Path("Resend") String resend,
                       @Path("key") String key);


    @GET("/countrylist")
    Call<CountryListModel> getCountryLists();
}
