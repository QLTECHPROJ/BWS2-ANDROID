package com.brainwellnessspa.Utility;

import com.brainwellnessspa.DassAssSliderTwo.Model.AssessmentQusModel;
import com.brainwellnessspa.LoginModule.Models.CountryListModel;
import com.brainwellnessspa.LoginModule.Models.LoginModel;
import com.brainwellnessspa.SplashModule.Models.VersionModel;
import com.brainwellnessspa.UserModuleTwo.Models.AddUserModel;
import com.brainwellnessspa.UserModuleTwo.Models.AddedUserListModel;
import com.brainwellnessspa.UserModuleTwo.Models.ForgotPasswordModel;
import com.brainwellnessspa.UserModuleTwo.Models.SignInModel;
import com.brainwellnessspa.UserModuleTwo.Models.VerifyPinModel;

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
    Call<SignInModel> getSignIn(@Field("Email") String email,
                                @Field("Password") String password,
                                @Field("DeviceType") String deviceType,
                                @Field("DeviceID") String deviceID,
                                @Field("Token") String token);

    @POST("signup")
    @FormUrlEncoded
    Call<LoginModel> getSignUp(@Field("Name") String name,
                               @Field("Email") String email,
                               @Field("CountryCode") String countryCode,
                               @Field("MobileNo") String mobileNo,
                               @Field("DeviceType") String deviceType,
                               @Field("Password") String password,
                               @Field("DeviceID") String deviceID,
                               @Field("Token") String token);

    @POST("forgotpass")
    @FormUrlEncoded
    Call<ForgotPasswordModel> getForgotPassword(@Field("Email") String email);

    @POST("addcouser")
    @FormUrlEncoded
    Call<AddUserModel> getAddUser(@Field("UserID") String userID,
                                  @Field("UserName") String userName,
                                  @Field("Email") String email,
                                  @Field("MobileNo") String mobileNo);;

    @GET("assesmentquestionlist")
    Call<AssessmentQusModel> getAssessmentQus();

    @POST("verifypin")
    @FormUrlEncoded
    Call<VerifyPinModel> getVerifyPin(@Field("UserID") String userID,
                                      @Field("Pin") String pin);

    @POST("userlist")
    @FormUrlEncoded
    Call<AddedUserListModel> getUserList(@Field("UserID") String userID);
}
