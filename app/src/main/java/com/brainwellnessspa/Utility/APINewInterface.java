package com.brainwellnessspa.Utility;

import com.brainwellnessspa.DashboardOldModule.Models.AudioInterruptionModel;
import com.brainwellnessspa.DashboardOldModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardOldModule.Models.ViewAllPlayListModel;
import com.brainwellnessspa.dashboardModule.models.AddToPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.AverageSleepTimeModel;
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel;
import com.brainwellnessspa.dashboardModule.models.MainPlaylistLibraryModel;
import com.brainwellnessspa.dashboardModule.models.NotificationlistModel;
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel;
import com.brainwellnessspa.dashboardModule.models.RecommendedCategoryModel;
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
import com.brainwellnessspa.dassAssSlider.models.AssessmentQusModel;
import com.brainwellnessspa.faqModule.models.FaqListModel;
import com.brainwellnessspa.reminderModule.models.DeleteRemiderModel;
import com.brainwellnessspa.reminderModule.models.ReminderListModel;
import com.brainwellnessspa.reminderModule.models.ReminderStatusModel;
import com.brainwellnessspa.reminderModule.models.SelectPlaylistModel;
import com.brainwellnessspa.reminderModule.models.SetReminderOldModel;
import com.brainwellnessspa.resourceModule.Models.ResourceFilterModel;
import com.brainwellnessspa.resourceModule.Models.ResourceListModel;
import com.brainwellnessspa.userModuleTwo.models.VersionModel;
import com.brainwellnessspa.userModuleTwo.models.RemoveProfileModel;
import com.brainwellnessspa.userModuleTwo.models.AddUserModel;
import com.brainwellnessspa.userModuleTwo.models.AddedUserListModel;
import com.brainwellnessspa.userModuleTwo.models.AssessmentSaveDataModel;
import com.brainwellnessspa.userModuleTwo.models.ChangePasswordModel;
import com.brainwellnessspa.userModuleTwo.models.ChangePinModel;
import com.brainwellnessspa.userModuleTwo.models.CoUserDetailsModel;
import com.brainwellnessspa.userModuleTwo.models.CountryListModel;
import com.brainwellnessspa.userModuleTwo.models.EditProfileModel;
import com.brainwellnessspa.userModuleTwo.models.ForgotPasswordModel;
import com.brainwellnessspa.userModuleTwo.models.ForgotPinModel;
import com.brainwellnessspa.userModuleTwo.models.NewSignUpModel;
import com.brainwellnessspa.userModuleTwo.models.ProfileSaveDataModel;
import com.brainwellnessspa.userModuleTwo.models.SignInModel;
import com.brainwellnessspa.userModuleTwo.models.VerifyPinModel;

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
    Call<VerifyPinModel> getVerifyPin(@Field("CoUserId") String CoUserId,
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
    Call<SucessModel> getForgotPin(@Field("UserID") String userID,
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
                                                  @Field("prevDrugUse") String prevDrugUse,
                                                  @Field("Medication") String medication);

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
                                @Field("PlaylistId") String PlaylistId,
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
    Call<SaveRecommendedCatModel> getSaveRecommendedCategory(@Field("CoUserId") String CoUserId,
                                                             @Field("CatName") String CatName,
                                                             @Field("AvgSleepTime") String AvgSleepTime);

    @POST("recentlyplayed")
    @FormUrlEncoded
    Call<SucessModel> getRecentlyPlayed(@Field("CoUserId") String CoUserId,
                                        @Field("AudioId") String AudioId);

    @POST("homescreen")
    @FormUrlEncoded
    Call<HomeScreenModel> getHomeScreenData(@Field("CoUserId") String CoUserId);

    @POST("logout")
    @FormUrlEncoded
    Call<SucessModel> getLogout(@Field("UserID") String UserID,
                                @Field("Token") String Token,
                                @Field("DeviceType") String DeviceType);

    @POST("changepin")
    @FormUrlEncoded
    Call<ChangePinModel> getChangePin(@Field("UserID") String UserID,
                                      @Field("CoUserId") String CoUserId,
                                      @Field("OldPin") String OldPin,
                                      @Field("NewPin") String NewPin);

    @POST("changepassword")
    @FormUrlEncoded
    Call<ChangePasswordModel> getChangePassword(@Field("UserID") String UserID,
                                                @Field("CoUserId") String CoUserId,
                                                @Field("OldPassword") String OldPassword,
                                                @Field("NewPassword") String NewPassword);

    @POST("resourcelist")
    @FormUrlEncoded
    Call<ResourceListModel> getResourceList(@Field("CoUserId") String CoUserId,
                                            @Field("ResourceTypeId") String ResourceTypeId,
                                            @Field("Category") String Category);

    @POST("resourcecatlist")
    @FormUrlEncoded
    Call<ResourceFilterModel> getResourceCatList(@Field("CoUserId") String CoUserId);

    @GET("faqlist")
    Call<FaqListModel> getFaqLists();

    @POST("setreminder")
    @FormUrlEncoded
    Call<SetReminderOldModel> getSetReminder(@Field("CoUserId") String CoUserId,
                                             @Field("PlaylistId") String PlaylistId,
                                             @Field("ReminderDay") String ReminderDay,
                                             @Field("ReminderTime") String ReminderTime,
                                             @Field("IsSingle") String IsSingle);


    @POST("reminderlist")
    @FormUrlEncoded
    Call<ReminderListModel> getReminderList(@Field("CoUserId") String CoUserId);


    @POST("deletereminder")
    @FormUrlEncoded
    Call<DeleteRemiderModel> getDeleteRemider(@Field("CoUserId") String CoUserId,
                                              @Field("ReminderId") String ReminderId);

    @POST("reminderstatus")
    @FormUrlEncoded
    Call<ReminderStatusModel> getReminderStatus(@Field("CoUserId") String CoUserId,
                                                @Field("PlaylistId") String PlaylistId,
                                                @Field("ReminderStatus") String ReminderStatus);

    @POST("removeprofileimg")
    @FormUrlEncoded
    Call<RemoveProfileModel> getRemoveProfile(@Field("CoUserId") String CoUserId);

    @POST("editprofile")
    @FormUrlEncoded
    Call<EditProfileModel> getEditProfile(@Field("CoUserId") String CoUserId,
                                          @Field("Name") String Name,
                                          @Field("Dob") String Dob,
                                          @Field("MobileNo") String MobileNo,
                                          @Field("EmailId") String EmailId);

    @POST("planlist")
    @FormUrlEncoded
    Call<PlanlistInappModel> getPlanlistInapp(@Field("CoUserId") String CoUserId);

    @POST("getnotificationlist")
    @FormUrlEncoded
    Call<NotificationlistModel> getNotificationlist(@Field("UserID") String UserID,
                                                    @Field("CoUserId") String CoUserId);

    @POST("audiointerruption")
    @FormUrlEncoded
    Call<AudioInterruptionModel> getAudioInterruption(@Field("CoUserId") String CoUserId,
                                                      @Field("userId") String userId,
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
