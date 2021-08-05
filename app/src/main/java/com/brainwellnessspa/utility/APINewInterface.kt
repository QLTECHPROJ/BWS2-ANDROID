package com.brainwellnessspa.utility

import com.brainwellnessspa.assessmentProgressModule.models.AssesmentGetDetailsModel
import com.brainwellnessspa.assessmentProgressModule.models.AssessmentQusModel
import com.brainwellnessspa.billingOrderModule.models.CancelPlanModel
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.faqModule.models.FaqListModel
import com.brainwellnessspa.membershipModule.models.UpdatePlanPurchase
import com.brainwellnessspa.reminderModule.models.DeleteRemiderModel
import com.brainwellnessspa.reminderModule.models.ReminderListModel
import com.brainwellnessspa.reminderModule.models.ReminderStatusModel
import com.brainwellnessspa.reminderModule.models.SetReminderOldModel
import com.brainwellnessspa.resourceModule.models.ResourceFilterModel
import com.brainwellnessspa.resourceModule.models.ResourceListModel
import com.brainwellnessspa.userModule.models.*
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface APINewInterface {
    @get:GET("countrylist")
    val countryLists: Call<CountryListModel>

    @POST("appversion")
    @FormUrlEncoded
    fun getAppVersions(
        @Field("Version")
        version: String?,
        @Field("AppType")
        appType: String?): Call<VersionModel>

    @POST("loginsignup")
    @FormUrlEncoded
    fun getUserAccess(
        @Field("MobileNo")
        MobileNo: String?,
        @Field("CountryCode")
        CountryCode: String?,
        @Field("DeviceType")
        DeviceType: String?,
        @Field("SignupFlag")
        SignupFlag: String?,
        @Field("key")
        key: String?): Call<UserAccessModel>

    @POST("authotp")
    @FormUrlEncoded
    fun getAuthOtpAccess(
        @Field("OTP")
        OTP: String?,
        @Field("DeviceType")
        DeviceType: String?,
        @Field("DeviceID")
        DeviceID: String?,
        @Field("CountryCode")
        CountryCode: String?,
        @Field("MobileNo")
        MobileNo: String?,
        @Field("SignupFlag")
        SignupFlag: String?,
        @Field("Name")
        Name: String?,
        @Field("Email")
        Email: String?,
        @Field("Token")
        Token: String?): Call<AuthOtpModel>

    @POST("forgotpass")
    @FormUrlEncoded
    fun getForgotPassword(
        @Field("Email")
        email: String?): Call<ForgotPasswordModel>

    @POST("setloginpin")
    @FormUrlEncoded
    fun getSetLoginPin(
        @Field("UserId")
        userId: String?,
        @Field("Pin")
        pin: String?): Call<SetLoginPinModel>

    @POST("inviteuser")
    @FormUrlEncoded
    fun getSetInviteUser(
        @Field("UserId")
        userId: String?,
        @Field("Name")
        name: String?,
        @Field("MobileNo")
        mobileNo: String?): Call<SetInviteUserModel>

    @POST("proceed")
    @FormUrlEncoded
    fun getReminderProceed(
        @Field("UserId")
        userId: String?): Call<ReminderProceedModel>

    @POST("cancelinviteuser")
    @FormUrlEncoded
    fun getCancelInviteUser(
        @Field("UserId")
        userId: String?,
        @Field("MobileNo")
        mobileNo: String?): Call<CancelInviteUserModel>

    @POST("deleteuser")
    @FormUrlEncoded
    fun getDeleteAccount(
        @Field("UserId")
        userID: String?,
        @Field("CancelId")
        cancelId: String?,
        @Field("CancelReason")
        cancelReason: String?): Call<DeleteInviteUserModel?>?

    @POST("removeuser")
    @FormUrlEncoded
    fun getRemoveInviteUser(
        @Field("UserId")
        userId: String?,
        @Field("MainAccountID")
        MainAccountID: String?): Call<RemoveInviteUserModel>

    @POST("addcouser")
    @FormUrlEncoded
    fun getAddUser(
        @Field("MainAccountID")
        MainAccountID: String?,
        @Field("Name")
        name: String?,
        @Field("Email")
        email: String?): Call<AddUserModel>

    @POST("manageuserlist")
    @FormUrlEncoded
    fun getManageUserList(
        @Field("MainAccountID")
        MainAccountID: String?): Call<ManageUserListModel>

    @POST("assesmentgetdetails")
    @FormUrlEncoded
    fun getAssesmentGetDetails(
        @Field("UserId")
        MainAccountID: String?): Call<AssesmentGetDetailsModel>

    @get:GET("assesmentquestionlist")
    val assessmentQus: Call<AssessmentQusModel>

    @POST("verifypin")
    @FormUrlEncoded
    fun getVerifyPin(
        @Field("UserId")
        UserId: String?,
        @Field("Pin")
        pin: String?): Call<AuthOtpModel>

    @POST("userlist")
    @FormUrlEncoded
    fun getUserList(
        @Field("MainAccountID")
        MainAccountID: String?): Call<AddedUserListModel>

    @POST("getcouserdetails")
    @FormUrlEncoded
    fun getCoUserDetails(
        @Field("UserId")
        UserId: String?): Call<AuthOtpModel>

    @POST("forgotpin")
    @FormUrlEncoded
    fun getForgotPin(
        @Field("UserId")
        UserId: String?,
        @Field("Email")
        email: String?): Call<SucessModel>

    @POST("profilesaveans")
    @FormUrlEncoded
    fun getProfileSaveData(
        @Field("UserId")
        UserId: String?,
        @Field("gender")
        gender: String?,
        @Field("genderX")
        genderX: String?,
        @Field("dob")
        age: String?,
        @Field("prevDrugUse")
        prevDrugUse: String?,
        @Field("Medication")
        medication: String?): Call<ProfileSaveDataModel>

    @POST("assesmentsaveans")
    @FormUrlEncoded
    fun getAssessmentSaveData(
        @Field("UserId")
        UserId: String?,
        @Field("ans")
        ans: String?): Call<AssessmentSaveDataModel>

    @POST("audiodetail")
    @FormUrlEncoded
    fun getAudioDetail(
        @Field("UserId")
        UserId: String?,
        @Field("AudioId")
        AudioId: String?): Call<AudioDetailModel>

    @POST("createplaylist")
    @FormUrlEncoded
    fun getCreatePlaylist(
        @Field("UserId")
        UserId: String?,
        @Field("PlaylistName")
        AudioId: String?): Call<CreateNewPlaylistModel>

    @POST("playlistdetails")
    @FormUrlEncoded
    fun getPlaylistDetail(
        @Field("UserId")
        UserId: String?,
        @Field("PlaylistId")
        PlaylistId: String?): Call<PlaylistDetailsModel>

    @POST("getcreatedplaylist")
    @FormUrlEncoded
    fun getPlaylisting(
        @Field("UserId")
        UserId: String?): Call<CreatePlaylistingModel>

    @POST("renameplaylist")
    @FormUrlEncoded
    fun getRenameNewPlaylist(
        @Field("UserId")
        UserId: String?,
        @Field("PlaylistId")
        PlaylistId: String?,
        @Field("PlaylistNewName")
        PlaylistNewName: String?): Call<RenameNewPlaylistModel>

    @POST("deleteplaylist")
    @FormUrlEncoded
    fun getDeletePlaylist(
        @Field("UserId")
        userID: String?,
        @Field("PlaylistId")
        playlistId: String?): Call<SucessModel>

    @POST("removeaudiofromplaylist")
    @FormUrlEncoded
    fun removeAudio(
        @Field("UserId")
        UserId: String?,
        @Field("AudioId")
        AudioId: String?,
        @Field("PlaylistId")
        PlaylistId: String?): Call<SucessModel>

    @POST("sortingplaylistaudio")
    @FormUrlEncoded
    fun sortAudio(
        @Field("UserId")
        UserId: String?,
        @Field("PlaylistId")
        PlaylistId: String?,
        @Field("PlaylistAudioId")
        PlaylistAudioId: String?): Call<SucessModel>

    @POST("suggestedaudio")
    @FormUrlEncoded
    fun getSuggestedLists(
        @Field("UserId")
        UserId: String?): Call<SuggestedModel>

    @POST("suggestedplaylist")
    @FormUrlEncoded
    fun getSuggestedPlayLists(
        @Field("UserId")
        UserId: String?): Call<SearchPlaylistModel>

    @POST("addaptoplaylist")
    @FormUrlEncoded
    fun getAddSearchAudioFromPlaylist(
        @Field("UserId")
        UserId: String?,
        @Field("AudioId")
        audioId: String?,
        @Field("PlaylistId")
        playlistId: String?,
        @Field("FromPlaylistId")
        fromPlaylistId: String?): Call<AddToPlaylistModel>

    @POST("searchonsuggestedlist")
    @FormUrlEncoded
    fun getSearchBoth(
        @Field("UserId")
        UserId: String?,
        @Field("SuggestedName")
        suggestedName: String?): Call<SearchBothModel>

    @POST("playlistonviewall")
    @FormUrlEncoded
    fun getViewAllPlayLists(
        @Field("UserId")
        UserId: String?,
        @Field("GetLibraryId")
        getLibraryId: String?): Call<ViewAllPlayListModel>

    @POST("playlistlibrary")
    @FormUrlEncoded
    fun getMainPlayLists(
        @Field("UserId")
        UserId: String?): Call<MainPlaylistLibraryModel>

    @POST("managehomescreen")
    @FormUrlEncoded
    fun getHomeData(
        @Field("UserId")
        UserId: String?): Call<HomeDataModel>

    @POST("managehomeviewallaudio")
    @FormUrlEncoded
    fun getViewAllAudioLists(
        @Field("UserId")
        UserId: String?,
        @Field("GetHomeAudioId")
        GetHomeAudioId: String?,
        @Field("CategoryName")
        CategoryName: String?): Call<ViewAllAudioListModel>

    @get:GET("avgsleeptime")
    val averageSleepTimeLists: Call<AverageSleepTimeModel>

    @POST("getrecommendedcategory")
    @FormUrlEncoded
    fun getRecommendedCategory(
        @Field("UserId")
        UserId: String?): Call<RecommendedCategoryModel>

    @POST("saverecommendedcategory")
    @FormUrlEncoded
    fun getSaveRecommendedCategory(
        @Field("UserId")
        UserId: String?,
        @Field("CatName")
        CatName: String?,
        @Field("AvgSleepTime")
        AvgSleepTime: String?): Call<SaveRecommendedCatModel>

    @POST("recentlyplayed")
    @FormUrlEncoded
    fun getRecentlyPlayed(
        @Field("UserId")
        UserId: String?,
        @Field("AudioId")
        AudioId: String?): Call<SucessModel>

    @POST("homescreen")
    @FormUrlEncoded
    fun getHomeScreenData(
        @Field("UserId")
        UserId: String?): Call<HomeScreenModel>

    @POST("logout")
    @FormUrlEncoded
    fun getLogout(
        @Field("UserId")
        UserId: String?,
        @Field("Token")
        Token: String?,
        @Field("DeviceType")
        DeviceType: String?): Call<SucessModel>

    @POST("changepin")
    @FormUrlEncoded
    fun getChangePin(
        @Field("UserId")
        UserId: String?,
        @Field("OldPin")
        OldPin: String?,
        @Field("NewPin")
        NewPin: String?): Call<ChangePinModel>

    @POST("changepassword")
    @FormUrlEncoded
    fun getChangePassword(
        @Field("MainAccountID")
        MainAccountID: String?,
        @Field("UserId")
        UserId: String?,
        @Field("OldPassword")
        OldPassword: String?,
        @Field("NewPassword")
        NewPassword: String?): Call<ChangePinModel>

    @POST("resourcelist")
    @FormUrlEncoded
    fun getResourceList(
        @Field("UserId")
        UserId: String?,
        @Field("ResourceTypeId")
        ResourceTypeId: String?,
        @Field("Category")
        Category: String?): Call<ResourceListModel>

    @POST("resourcecatlist")
    @FormUrlEncoded
    fun getResourceCatList(
        @Field("UserId")
        UserId: String?): Call<ResourceFilterModel>

    @get:GET("faqlist")
    val faqLists: Call<FaqListModel?>?

    @POST("setreminder")
    @FormUrlEncoded
    fun getSetReminder(
        @Field("UserId")
        UserId: String?,
        @Field("PlaylistId")
        PlaylistId: String?,
        @Field("ReminderDay")
        ReminderDay: String?,
        @Field("ReminderTime")
        ReminderTime: String?,
        @Field("IsSingle")
        IsSingle: String?): Call<SetReminderOldModel>

    @POST("reminderlist")
    @FormUrlEncoded
    fun getReminderList(
        @Field("UserId")
        UserId: String?): Call<ReminderListModel>

    @POST("deletereminder")
    @FormUrlEncoded
    fun getDeleteRemider(
        @Field("UserId")
        UserId: String?,
        @Field("ReminderId")
        ReminderId: String?): Call<DeleteRemiderModel>

    @POST("reminderstatus")
    @FormUrlEncoded
    fun getReminderStatus(
        @Field("UserId")
        UserId: String?,
        @Field("PlaylistId")
        PlaylistId: String?,
        @Field("ReminderStatus")
        ReminderStatus: String?): Call<ReminderStatusModel>

    @POST("removeprofileimg")
    @FormUrlEncoded
    fun getRemoveProfile(
        @Field("UserId")
        UserId: String?): Call<RemoveProfileModel>

    @POST("editprofile")
    @FormUrlEncoded
    fun getEditProfile(
        @Field("MainAccountID")
        MainAccountID: String?,
        @Field("UserId")
        UserId: String?,
        @Field("Name")
        Name: String?,
        @Field("Dob")
        Dob: String?,
        @Field("MobileNo")
        MobileNo: String?,
        @Field("EmailId")
        EmailId: String?): Call<EditProfileModel>

    @POST("planlist")
    @FormUrlEncoded
    fun getPlanlistInapp(
        @Field("UserId")
        UserId: String?): Call<PlanlistInappModel>

    @POST("planpurchase")
    @FormUrlEncoded
    fun getUpdatePlanPurchase(
        @Field("UserId")
        UserId: String?,
        @Field("MainAccountID")
        MainAccountID: String?,
        @Field("TransactionID")
        TransactionID: String?,
        @Field("PlanId")
        PlanId: String?,
        @Field("AppType")
        AppType: String?): Call<UpdatePlanPurchase>

    @POST("getnotificationlist")
    @FormUrlEncoded
    fun getNotificationlist(
        @Field("UserId")
        UserId: String?): Call<NotificationlistModel>

    @POST("audiointerruption")
    @FormUrlEncoded
    fun getAudioInterruption(
        @Field("UserId")
        UserId: String?,
        @Field("audioId")
        audioId: String?,
        @Field("audioName")
        audioName: String?,
        @Field("audioDescription")
        audioDescription: String?,
        @Field("directions")
        directions: String?,
        @Field("masterCategory")
        masterCategory: String?,
        @Field("subCategory")
        subCategory: String?,
        @Field("audioDuration")
        audioDuration: String?,
        @Field("bitRate")
        bitRate: String?,
        @Field("audioType")
        audioType: String?,
        @Field("playerType")
        playerType: String?,
        @Field("sound")
        sound: String?,
        @Field("audioService")
        audioService: String?,
        @Field("source")
        source: String?,
        @Field("position")
        position: String?,
        @Field("seekPosition")
        seekPosition: String?,
        @Field("interruptionMethod")
        interruptionMethod: String?,
        @Field("batteryLevel")
        batteryLevel: String?,
        @Field("batteryState")
        batteryState: String?,
        @Field("internetDownSpeed")
        internetDownSpeed: String?,
        @Field("internetUpSpeed")
        internetUpSpeed: String?,
        @Field("appType")
        appType: String?): Call<AudioInterruptionModel>

    @POST("cancelplan")
    @FormUrlEncoded
    fun getCancelPlan(
        @Field("UserID")
        userID: String?,
        @Field("CancelId")
        cancelId: String?,
        @Field("CancelReason")
        cancelReason: String?): Call<CancelPlanModel?>?

    @POST("useraudiotracking")
    @FormUrlEncoded
    fun getUserAudioTracking(
        @Field("TrackingData")
        trackingData: String?): Call<UserAudioTrackingModel?>?
}