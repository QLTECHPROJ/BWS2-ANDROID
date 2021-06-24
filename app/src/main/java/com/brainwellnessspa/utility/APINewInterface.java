package com.brainwellnessspa.utility;

import com.brainwellnessspa.dashboardModule.models.AudioInterruptionModel;
import com.brainwellnessspa.dashboardModule.models.ViewAllAudioListModel;
import com.brainwellnessspa.dashboardModule.models.ViewAllPlayListModel;
import com.brainwellnessspa.dashboardModule.models.AddToPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.AverageSleepTimeModel;
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel;
import com.brainwellnessspa.dashboardModule.models.MainPlaylistLibraryModel;
import com.brainwellnessspa.dashboardModule.models.NotificationlistModel;
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel;
import com.brainwellnessspa.dashboardModule.models.RecommendedCategoryModel;
import com.brainwellnessspa.dashboardModule.models.RenameNewPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.SaveRecommendedCatModel;
import com.brainwellnessspa.dashboardModule.models.SearchPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.SearchBothModel;
import com.brainwellnessspa.dashboardModule.models.SuggestedModel;
import com.brainwellnessspa.dashboardModule.models.SucessModel;
import com.brainwellnessspa.dashboardModule.models.AudioDetailModel;
import com.brainwellnessspa.dashboardModule.models.CreateNewPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.CreatePlaylistingModel;
import com.brainwellnessspa.dashboardModule.models.PlaylistDetailsModel;
import com.brainwellnessspa.dashboardModule.models.HomeDataModel;
import com.brainwellnessspa.assessmentProgressModule.models.AssessmentQusModel;
import com.brainwellnessspa.faqModule.models.FaqListModel;
import com.brainwellnessspa.reminderModule.models.DeleteRemiderModel;
import com.brainwellnessspa.reminderModule.models.ReminderListModel;
import com.brainwellnessspa.reminderModule.models.ReminderStatusModel;
import com.brainwellnessspa.reminderModule.models.SelectPlaylistModel;
import com.brainwellnessspa.reminderModule.models.SetReminderOldModel;
import com.brainwellnessspa.resourceModule.models.ResourceFilterModel;
import com.brainwellnessspa.resourceModule.models.ResourceListModel;
import com.brainwellnessspa.userModule.models.AuthOtpModel;
import com.brainwellnessspa.userModule.models.UserAccessModel;
import com.brainwellnessspa.userModule.models.VersionModel;
import com.brainwellnessspa.userModule.models.RemoveProfileModel;
import com.brainwellnessspa.userModule.models.AddUserModel;
import com.brainwellnessspa.userModule.models.AddedUserListModel;
import com.brainwellnessspa.userModule.models.AssessmentSaveDataModel;
import com.brainwellnessspa.userModule.models.ChangePasswordModel;
import com.brainwellnessspa.userModule.models.ChangePinModel;
import com.brainwellnessspa.userModule.models.CoUserDetailsModel;
import com.brainwellnessspa.userModule.models.CountryListModel;
import com.brainwellnessspa.userModule.models.EditProfileModel;
import com.brainwellnessspa.userModule.models.ForgotPasswordModel;
import com.brainwellnessspa.userModule.models.NewSignUpModel;
import com.brainwellnessspa.userModule.models.ProfileSaveDataModel;
import com.brainwellnessspa.userModule.models.SignInModel;
import com.brainwellnessspa.userModule.models.VerifyPinModel;

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

    @POST("loginsignup")
    @FormUrlEncoded
    Call<UserAccessModel> getUserAccess(@Field("MobileNo") String MobileNo,
                                        @Field("CountryCode") String CountryCode,
                                        @Field("DeviceType") String DeviceType,
                                        @Field("SignupFlag") String SignupFlag,
                                        @Field("key") String key);

 @POST("authotp")
    @FormUrlEncoded
    Call<AuthOtpModel> getAuthOtpAccess(@Field("OTP") String OTP,
                                        @Field("DeviceType") String DeviceType,
                                        @Field("DeviceID") String DeviceID,
                                        @Field("CountryCode") String CountryCode,
                                        @Field("MobileNo") String MobileNo,
                                        @Field("SignupFlag") String SignupFlag,
                                        @Field("Name") String Name,
                                        @Field("Email") String Email,
                                        @Field("Token") String Token);

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
    Call<AddUserModel> getAddUser(@Field("MainAccountID") String MainAccountID,
                                  @Field("UserName") String userName,
                                  @Field("Email") String email,
                                  @Field("MobileNo") String mobileNo);

    ;

    @GET("assesmentquestionlist")
    Call<AssessmentQusModel> getAssessmentQus();

    @POST("verifypin")
    @FormUrlEncoded
    Call<VerifyPinModel> getVerifyPin(@Field("UserId") String UserId,
                                      @Field("Pin") String pin);

    @POST("userlist")
    @FormUrlEncoded
    Call<AddedUserListModel> getUserList(@Field("MainAccountID") String MainAccountID);

    @POST("getcouserdetails")
    @FormUrlEncoded
    Call<CoUserDetailsModel> getCoUserDetails(@Field("UserId") String UserId);

    @POST("forgotpin")
    @FormUrlEncoded
    Call<SucessModel> getForgotPin(@Field("UserId") String UserId,
                                   @Field("Email") String email);

    @POST("profilesaveans")
    @FormUrlEncoded
    Call<ProfileSaveDataModel> getProfileSaveData(@Field("UserId") String UserId,
                                                  @Field("profileType") String profileType,
                                                  @Field("gender") String gender,
                                                  @Field("genderX") String genderX,
                                                  @Field("age") String age,
                                                  @Field("prevDrugUse") String prevDrugUse,
                                                  @Field("Medication") String medication);

    @POST("assesmentsaveans")
    @FormUrlEncoded
    Call<AssessmentSaveDataModel> getAssessmentSaveData(@Field("UserId") String UserId,
                                                        @Field("ans") String ans);

    @POST("audiodetail")
    @FormUrlEncoded
    Call<AudioDetailModel> getAudioDetail(@Field("UserId") String UserId,
                                          @Field("AudioId") String AudioId);

    @POST("createplaylist")
    @FormUrlEncoded
    Call<CreateNewPlaylistModel> getCreatePlaylist(@Field("UserId") String UserId,
                                                   @Field("PlaylistName") String AudioId);

    @POST("playlistdetails")
    @FormUrlEncoded
    Call<PlaylistDetailsModel> getPlaylistDetail(@Field("UserId") String UserId,
                                                 @Field("PlaylistId") String PlaylistId);

    @POST("getcreatedplaylist")
    @FormUrlEncoded
    Call<CreatePlaylistingModel> getPlaylisting(@Field("UserId") String UserId);

    @POST("renameplaylist")
    @FormUrlEncoded
    Call<RenameNewPlaylistModel> getRenameNewPlaylist(@Field("UserId") String UserId,
                                                      @Field("PlaylistId") String PlaylistId,
                                                      @Field("PlaylistNewName") String PlaylistNewName);

    @POST("deleteplaylist")
    @FormUrlEncoded
    Call<SucessModel> getDeletePlaylist(@Field("UserId") String userID,
                                        @Field("PlaylistId") String playlistId);


    @POST("removeaudiofromplaylist")
    @FormUrlEncoded
    Call<SucessModel> RemoveAudio(@Field("UserId") String UserId,
                                  @Field("AudioId") String AudioId,
                                  @Field("PlaylistId") String PlaylistId);

    @POST("sortingplaylistaudio")
    @FormUrlEncoded
    Call<SucessModel> SortAudio(@Field("UserId") String UserId,
                                @Field("PlaylistId") String PlaylistId,
                                @Field("PlaylistAudioId") String PlaylistAudioId);

    @POST("suggestedaudio")
    @FormUrlEncoded
    Call<SuggestedModel> getSuggestedLists(@Field("UserId") String UserId);

    @POST("suggestedplaylist")
    @FormUrlEncoded
    Call<SearchPlaylistModel> getSuggestedPlayLists(@Field("UserId") String UserId);

    @POST("addaptoplaylist")
    @FormUrlEncoded
    Call<AddToPlaylistModel> getAddSearchAudioFromPlaylist(@Field("UserId") String UserId,
                                                           @Field("AudioId") String audioId,
                                                           @Field("PlaylistId") String playlistId,
                                                           @Field("FromPlaylistId") String fromPlaylistId);

    @POST("searchonsuggestedlist")
    @FormUrlEncoded
    Call<SearchBothModel> getSearchBoth(@Field("UserId") String UserId,
                                        @Field("SuggestedName") String suggestedName);

    @POST("getallplaylist")
    @FormUrlEncoded
    Call<SelectPlaylistModel> getAllPlayListing(@Field("UserId") String UserId);

    @POST("playlistonviewall")
    @FormUrlEncoded
    Call<ViewAllPlayListModel> getViewAllPlayLists(@Field("UserId") String UserId,
                                                   @Field("GetLibraryId") String getLibraryId);

    @POST("playlistlibrary")
    @FormUrlEncoded
    Call<MainPlaylistLibraryModel> getMainPlayLists(@Field("UserId") String UserId);

    @POST("managehomescreen")
    @FormUrlEncoded
    Call<HomeDataModel> getHomeData(@Field("UserId") String UserId);

    @POST("managehomeviewallaudio")
    @FormUrlEncoded
    Call<ViewAllAudioListModel> getViewAllAudioLists(@Field("UserId") String UserId,
                                                     @Field("GetHomeAudioId") String GetHomeAudioId,
                                                     @Field("CategoryName") String CategoryName);

    @GET("avgsleeptime")
    Call<AverageSleepTimeModel> getAverageSleepTimeLists();

    @POST("getrecommendedcategory")
    @FormUrlEncoded
    Call<RecommendedCategoryModel> getRecommendedCategory(@Field("UserId") String UserId);

    @POST("saverecommendedcategory")
    @FormUrlEncoded
    Call<SaveRecommendedCatModel> getSaveRecommendedCategory(@Field("UserId") String UserId,
                                                             @Field("CatName") String CatName,
                                                             @Field("AvgSleepTime") String AvgSleepTime);

    @POST("recentlyplayed")
    @FormUrlEncoded
    Call<SucessModel> getRecentlyPlayed(@Field("UserId") String UserId,
                                        @Field("AudioId") String AudioId);

    @POST("homescreen")
    @FormUrlEncoded
    Call<HomeScreenModel> getHomeScreenData(@Field("UserId") String UserId);

    @POST("logout")
    @FormUrlEncoded
    Call<SucessModel> getLogout(@Field("UserId") String UserId,
                                @Field("Token") String Token,
                                @Field("DeviceType") String DeviceType);

    @POST("changepin")
    @FormUrlEncoded
    Call<ChangePinModel> getChangePin(@Field("UserId") String UserId,
                                      @Field("OldPin") String OldPin,
                                      @Field("NewPin") String NewPin);

    @POST("changepassword")
    @FormUrlEncoded
    Call<ChangePasswordModel> getChangePassword(@Field("MainAccountID") String MainAccountID ,
                                                @Field("UserId") String UserId,
                                                @Field("OldPassword") String OldPassword,
                                                @Field("NewPassword") String NewPassword);

    @POST("resourcelist")
    @FormUrlEncoded
    Call<ResourceListModel> getResourceList(@Field("UserId") String UserId,
                                            @Field("ResourceTypeId") String ResourceTypeId,
                                            @Field("Category") String Category);

    @POST("resourcecatlist")
    @FormUrlEncoded
    Call<ResourceFilterModel> getResourceCatList(@Field("UserId") String UserId);

    @GET("faqlist")
    Call<FaqListModel> getFaqLists();

    @POST("setreminder")
    @FormUrlEncoded
    Call<SetReminderOldModel> getSetReminder(@Field("UserId") String UserId,
                                             @Field("PlaylistId") String PlaylistId,
                                             @Field("ReminderDay") String ReminderDay,
                                             @Field("ReminderTime") String ReminderTime,
                                             @Field("IsSingle") String IsSingle);


    @POST("reminderlist")
    @FormUrlEncoded
    Call<ReminderListModel> getReminderList(@Field("UserId") String UserId);


    @POST("deletereminder")
    @FormUrlEncoded
    Call<DeleteRemiderModel> getDeleteRemider(@Field("UserId") String UserId,
                                              @Field("ReminderId") String ReminderId);

    @POST("reminderstatus")
    @FormUrlEncoded
    Call<ReminderStatusModel> getReminderStatus(@Field("UserId") String UserId,
                                                @Field("PlaylistId") String PlaylistId,
                                                @Field("ReminderStatus") String ReminderStatus);

    @POST("removeprofileimg")
    @FormUrlEncoded
    Call<RemoveProfileModel> getRemoveProfile(@Field("UserId") String UserId);

    @POST("editprofile")
    @FormUrlEncoded
    Call<EditProfileModel> getEditProfile(@Field("MainAccountID") String MainAccountID,
                                          @Field("UserId") String UserId,
                                          @Field("Name") String Name,
                                          @Field("Dob") String Dob,
                                          @Field("MobileNo") String MobileNo,
                                          @Field("EmailId") String EmailId);

    @POST("planlist")
    @FormUrlEncoded
    Call<PlanlistInappModel> getPlanlistInapp(@Field("UserId") String UserId);

    @POST("getnotificationlist")
    @FormUrlEncoded
    Call<NotificationlistModel> getNotificationlist(@Field("UserId") String UserId);

    @POST("audiointerruption")
    @FormUrlEncoded
    Call<AudioInterruptionModel> getAudioInterruption(@Field("UserId") String UserId,
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
                                                      @Field("batteryLevel") String batteryLevel,
                                                      @Field("batteryState") String batteryState,
                                                      @Field("internetDownSpeed") String internetDownSpeed,
                                                      @Field("internetUpSpeed") String internetUpSpeed,
                                                      @Field("appType") String  appType);
}
