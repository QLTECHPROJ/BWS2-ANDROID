package com.brainwellnessspa.Utility;

import com.brainwellnessspa.DashboardModule.Models.MainPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllPlayListModel;
import com.brainwellnessspa.DashboardTwoModule.Model.AddToPlaylistModel;
import com.brainwellnessspa.DashboardTwoModule.Model.AverageSleepTimeModel;
import com.brainwellnessspa.DashboardTwoModule.Model.MainPlaylistLibraryModel;
import com.brainwellnessspa.DashboardTwoModule.Model.PlanlistModel;
import com.brainwellnessspa.DashboardTwoModule.Model.RecommendedCategoryModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SearchPlaylistModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SearchBothModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SuggestedModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SucessModel;
import com.brainwellnessspa.DashboardTwoModule.Model.AudioDetailModel;
import com.brainwellnessspa.DashboardTwoModule.Model.CreateNewPlaylistModel;
import com.brainwellnessspa.DashboardTwoModule.Model.CreatePlaylistingModel;
import com.brainwellnessspa.DashboardTwoModule.Model.PlaylistDetailsModel;
import com.brainwellnessspa.DashboardTwoModule.Model.HomeDataModel;
import com.brainwellnessspa.DassAssSliderTwo.Model.AssessmentQusModel;
import com.brainwellnessspa.LoginModule.Models.CountryListModel;
import com.brainwellnessspa.ReminderModule.Models.SelectPlaylistModel;
import com.brainwellnessspa.SplashModule.Models.VersionModel;
import com.brainwellnessspa.UserModuleTwo.Models.AddUserModel;
import com.brainwellnessspa.UserModuleTwo.Models.AddedUserListModel;
import com.brainwellnessspa.UserModuleTwo.Models.AssessmentSaveDataModel;
import com.brainwellnessspa.UserModuleTwo.Models.CoUserDetailsModel;
import com.brainwellnessspa.UserModuleTwo.Models.ForgotPasswordModel;
import com.brainwellnessspa.UserModuleTwo.Models.ForgotPinModel;
import com.brainwellnessspa.UserModuleTwo.Models.NewSignUpModel;
import com.brainwellnessspa.UserModuleTwo.Models.ProfileSaveDataModel;
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
    Call<NewSignUpModel> getSignUp(@Field("Name") String name,
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
                                  @Field("MobileNo") String mobileNo);

    ;

    @GET("assesmentquestionlist")
    Call<AssessmentQusModel> getAssessmentQus();

    @POST("verifypin")
    @FormUrlEncoded
    Call<VerifyPinModel> getVerifyPin(@Field("UserID") String userID,
                                      @Field("Pin") String pin);

    @POST("userlist")
    @FormUrlEncoded
    Call<AddedUserListModel> getUserList(@Field("UserID") String userID);

    @POST("getcouserdetails")
    @FormUrlEncoded
    Call<CoUserDetailsModel> getCoUserDetails(@Field("UserID") String userID,
                                              @Field("CoUserId") String CoUserId);

    @POST("forgotpin")
    @FormUrlEncoded
    Call<ForgotPinModel> getForgotPin(@Field("UserID") String userID,
                                      @Field("CoUserId") String CoUserId,
                                      @Field("Email") String email);

    @POST("profilesaveans")
    @FormUrlEncoded
    Call<ProfileSaveDataModel> getProfileSaveData(@Field("UserID") String userID,
                                                  @Field("CoUserId") String CoUserId,
                                                  @Field("profileType") String profileType,
                                                  @Field("gender") String gender,
                                                  @Field("genderX") String genderX,
                                                  @Field("age") String age,
                                                  @Field("prevDrugUse") String prevDrugUse);

    @POST("assesmentsaveans")
    @FormUrlEncoded
    Call<AssessmentSaveDataModel> getAssessmentSaveData(@Field("UserID") String userID,
                                                        @Field("CoUserId") String CoUserId,
                                                        @Field("ans") String ans);

    @POST("audiodetail")
    @FormUrlEncoded
    Call<AudioDetailModel> getAudioDetail(@Field("CoUserId") String CoUserId,
                                          @Field("AudioId") String AudioId);

    @POST("createplaylist")
    @FormUrlEncoded
    Call<CreateNewPlaylistModel> getCreatePlaylist(@Field("CoUserId") String CoUserId,
                                                   @Field("PlaylistName") String AudioId);

    @POST("playlistdetails")
    @FormUrlEncoded
    Call<PlaylistDetailsModel> getPlaylistDetail(@Field("CoUserId") String CoUserId,
                                                 @Field("PlaylistId") String PlaylistId);

    @POST("getcreatedplaylist")
    @FormUrlEncoded
    Call<CreatePlaylistingModel> getPlaylisting(@Field("CoUserId") String CoUserId);

    @POST("renameplaylist")
    @FormUrlEncoded
    Call<SucessModel> getRenameNewPlaylist(@Field("CoUserId") String CoUserId,
                                           @Field("PlaylistId") String PlaylistId,
                                           @Field("PlaylistNewName") String PlaylistNewName);

    @POST("deleteplaylist")
    @FormUrlEncoded
    Call<SucessModel> getDeletePlaylist(@Field("CoUserId") String userID,
                                        @Field("PlaylistId") String playlistId);


    @POST("removeaudiofromplaylist")
    @FormUrlEncoded
    Call<SucessModel> RemoveAudio(@Field("CoUserId") String CoUserId,
                                  @Field("AudioId") String AudioId,
                                  @Field("PlaylistId") String PlaylistId);

    @POST("sortingplaylistaudio")
    @FormUrlEncoded
    Call<SucessModel> SortAudio(@Field("CoUserId") String CoUserId,
                                @Field("PlaylistId") String AudioId,
                                @Field("PlaylistAudioId") String PlaylistAudioId);

    @POST("suggestedaudio")
    @FormUrlEncoded
    Call<SuggestedModel> getSuggestedLists(@Field("CoUserId") String CoUserId);

    @POST("suggestedplaylist")
    @FormUrlEncoded
    Call<SearchPlaylistModel> getSuggestedPlayLists(@Field("CoUserId") String CoUserId);

    @POST("addaptoplaylist")
    @FormUrlEncoded
    Call<AddToPlaylistModel> getAddSearchAudioFromPlaylist(@Field("CoUserId") String CoUserId,
                                                           @Field("AudioId") String audioId,
                                                           @Field("PlaylistId") String playlistId,
                                                           @Field("FromPlaylistId") String fromPlaylistId);

    @POST("searchonsuggestedlist")
    @FormUrlEncoded
    Call<SearchBothModel> getSearchBoth(@Field("CoUserId") String CoUserId,
                                        @Field("SuggestedName") String suggestedName);

    @POST("getallplaylist")
    @FormUrlEncoded
    Call<SelectPlaylistModel> getAllPlayListing(@Field("CoUserId") String CoUserId);

    @POST("playlistonviewall")
    @FormUrlEncoded
    Call<ViewAllPlayListModel> getViewAllPlayLists(@Field("CoUserId") String CoUserId,
                                                   @Field("GetLibraryId") String getLibraryId);

    @POST("playlistlibrary")
    @FormUrlEncoded
    Call<MainPlaylistLibraryModel> getMainPlayLists(@Field("CoUserId") String CoUserId);

    @POST("managehomescreen")
    @FormUrlEncoded
    Call<HomeDataModel> getHomeData(@Field("CoUserId") String CoUserId);

    @POST("managehomeviewallaudio")
    @FormUrlEncoded
    Call<ViewAllAudioListModel> getViewAllAudioLists(@Field("CoUserId") String CoUserId,
                                                     @Field("GetHomeAudioId") String GetHomeAudioId,
                                                     @Field("CategoryName") String CategoryName);

    @GET("avgsleeptime")
    Call<AverageSleepTimeModel> getAverageSleepTimeLists();

    @POST("getrecommendedcategory")
    @FormUrlEncoded
    Call<RecommendedCategoryModel> getRecommendedCategory(@Field("CoUserId") String CoUserId);

    @POST("saverecommendedcategory")
    @FormUrlEncoded
    Call<SucessModel> getSaveRecommendedCategory(@Field("CoUserId") String CoUserId,
                                                 @Field("CatName") String CatName,
                                                 @Field("AvgSleepTime") String AvgSleepTime);

    @POST("recentlyplayed")
    @FormUrlEncoded
    Call<SucessModel> getRecentlyPlayed(@Field("CoUserId") String CoUserId,
                                        @Field("AudioId") String AudioId);
}
