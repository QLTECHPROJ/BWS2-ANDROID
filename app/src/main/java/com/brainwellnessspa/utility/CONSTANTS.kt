package com.brainwellnessspa.utility

object CONSTANTS {
    const val UTF_8 = "UTF-8"

    /* public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_album_art, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }*/
    const val SERVICE_ACCOUNT_EMAIL = "service-861076939494@gcp-sa-pubsub.iam.gserviceaccount.com"
    const val track = "track"
    const val screen = "screen"
    const val FLAG_ZERO = "0"
    const val FLAG_ONE = "1"
    const val FLAG_TWO = "2"
    const val FLAG_THREE = "3"
    const val FLAG_FOUR = "4"
    const val FLAG_FIVE = "5"
    const val FLAG_SIX = "6"
    const val FLAG_SEVEN = "7"
    const val FLAG_EIGHT = "8"
    const val FLAG_NINE = "9"
    const val FLAG_TEN = "10"
    const val FLAG_ELEVEN = "11"
    const val FLAG_TWELVE = "12"
    const val FLAG_THIRTEEN = "13"
    const val FLAG_FORTEEN = "14"
    const val FLAG_FIFTEEN = "15"
    const val YEAR_TO_DATE_FORMAT = "yyyy-MM-dd"
    const val TWENTY_FOUR_HOUR_FORMAT_WITH_SECOND = "HH:mm:ss"
    const val TWELVE_HOUR_FORMAT_WITH_AM_PM = "hh:mm a"
    const val MONTH_DATE_YEAR_FORMAT = "MMM dd, yyyy"
    const val DATE_MONTH_YEAR_FORMAT = "dd MMM, yyyy"
    const val DATE_MONTH_YEAR_FORMAT_TIME = "dd MMMM, yyyy hh:mm:ss aa"
    const val DATE_MONTH_YEAR_FORMAT_TIME_with_Timzone = "yyyy-MM-dd HH:mm:ssZZ"
    const val SERVER_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val TWENTY_FOUR_HH_MM_FORMAT = "HH:mm"
    var ScreenView = "ScreenView"
    var ASSPROCESS = "ASSPROCESS"
    var IndexScore = "IndexScore"
    var ScoreLevel = "ScoreLevel"
    var PopUp = "PopUp"
    var ScreenVisible = "ScreenVisible"
    var Tnc = "Tnc"

    @JvmField var Web = "Web"
    var Check = "Check"

    @JvmField var Name = "Name"

    @JvmField var Promocode = "Promocode"
    var Like = "Like"
    var Download = "Download"
    var position = "position"
    var AudioList = "AudioList"
    var Code = "Code"
    var Codes = "Codes"
    var MobileNo = "MobileNo"
    var ID = "ID"

    @JvmField var PlaylistID = "PlaylistID"
    var PlaylistName = "PlaylistName"
    var PlaylistImage = "PlaylistImage"
    var title = "title"
    var author = "author"
    var linkOne = "linkOne"
    var linkTwo = "linkTwo"
    var image = "image"
    var description = "description"
    var mastercat = "mastercat"
    var subcat = "subcat"
    var AudioFile = "AudioFile"
    var ImageFile = "ImageFile"
    var AudioDirection = "AudioDirection"
    var Audiomastercat = "Audiomastercat"
    var AudioSubCategory = "AudioSubCategory"

    //multipart tag for file upload
    const val MULTIPART_FORMAT = "multipart/form-data"
    const val PREFE_ACCESS_SIGNIN_COUSER = "CoUser"
    const val PREFE_ACCESS_mainAccountID = "mainAccountID"
    const val PREFE_ACCESS_NAME = "Name"
    const val PREFE_ACCESS_DOB = "DOB"
    const val PREFE_ACCESS_IMAGE = "Image"
    const val PREFE_ACCESS_AreaOfFocus = "AreaOfFocus"
    const val PREFE_ACCESS_USEREMAIL = "UserEmail"
    const val PREFE_ACCESS_DeviceType = "DeviceType"
    const val PREFE_ACCESS_DeviceID = "DeviceID"
    const val PREFE_ACCESS_SLEEPTIME = "Sleeptime"
    const val PREFE_ACCESS_INDEXSCORE = "indexScore"
    const val PREFE_ACCESS_ISPROFILECOMPLETED = "isProfileCompleted"
    const val PREFE_ACCESS_assesmentContent = "assesmentContent"
    const val PREFE_ACCESS_ISAssCOMPLETED = "isAssessmentCompleted"
    const val PREFE_ACCESS_directLogin = "directLogin"
    const val PREFE_ACCESS_isSetLoginPin = "isSetLoginPin"
    const val PREFE_ACCESS_isPinSet = "isPinSet"
    const val PREFE_ACCESS_isMainAccount = "isMainAccount"
    const val PREFE_ACCESS_isEmailVerified = "isEmailVerified"
    const val PREFE_ACCESS_coUserCount = "coUserCount"
    const val PREFE_ACCESS_PlanDeviceType = "PlanDeviceType"
    const val PREFE_ACCESS_PlanId = "PlanId"
    const val PREFE_ACCESS_PlanPurchaseDate = "PlanPurchaseDate"
    const val PREFE_ACCESS_PlanExpireDate = "PlanExpireDate"
    const val PREFE_ACCESS_TransactionId = "TransactionId"
    const val PREFE_ACCESS_TrialPeriodStart = "TrialPeriodStart"
    const val PREFE_ACCESS_TrialPeriodEnd = "TrialPeriodEnd"
    const val PREFE_ACCESS_PlanStatus = "PlanStatus"
    const val PREFE_ACCESS_PlanContent = "PlanContent"
    const val PREFE_ACCESS_SCORELEVEL = "scoreLevel"
    const val PREFE_ACCESS_EMAIL = "Email"
    const val PREFE_ACCESS_MOBILE = "mobile"
    const val PREFE_ACCESS_UserId = "UserId"
    const val PREF_KEY_LOGIN = "Login"
    const val PREF_KEY_Splash = "Splash"
    const val PREF_KEY_SplashKey = "SplashKey"
    const val PREF_KEY_PLAYER = "Player"
    const val PREF_KEY_USER_ACTIVITY = "UserActivity"
    const val PREF_KEY_USER_TRACK_ARRAY = "UserTrackArray"
    const val PREF_KEY_MainAudioList = "MainAudioList"
    const val PREF_KEY_PlayerAudioList = "PlayerAudioList"
    const val PREF_KEY_AudioPlayerFlag = "AudioPlayerFlag"
    const val PREF_KEY_PlayerPlaylistId = "PayerPlaylistId"
    const val PREF_KEY_PlayerPlaylistName = "PayerPlaylistName"
    const val PREF_KEY_PlayerPosition = "PlayerPosition"
    const val PREF_KEY_PlayFrom = "PlayFrom"
    const val PREF_KEY_UserID = "UserID"
    const val PREF_KEY_UserPromocode = "UserPromocode"
    const val PREF_KEY_ReferLink = "ReferLink"
    const val PREF_KEY_IsDisclimer = "IsDisclimer"
    const val PREF_KEY_Disclimer = "Disclimer"
    const val PREF_KEY_LOGOUT = "Logout"
    const val PREF_KEY_LOGOUT_UserID = "UserID"
    const val PREF_KEY_LOGOUT_CoUserID = "CoUserID"
    const val PREF_KEY_UnLockAudiList = "UnLockAudiList"
    const val PREF_KEY_Cat_Name = "Cat_Name"
    const val PREF_KEY_PlaylistId = "PlaylistId"
    const val PREF_KEY_DownloadPlaylist = "PlaylistId"
    const val PREF_KEY_DownloadName = "Name"
    const val PREF_KEY_DownloadUrl = "Url"
    const val PREF_KEY_DownloadPlaylistId = "downloadPlaylistId"

    //    public static final String PREF_KEY_removedDownloadPlaylist = "removedPlaylistId";
    //    public static final String PREF_KEY_removedDownloadName = "removedName";
    //    public static final String PREF_KEY_removedDownloadPlaylistId = "removedDownloadPlaylistId";

    const val PREF_KEY_ReminderFirstLogin = "ReminderFirstLogin"
    const val PREF_KEY_Plan = "Plan"
    const val PREF_KEY_SEGMENT_PLAYLIST = "SegmentPlaylist"
    const val PREF_KEY_PlaylistID = "PlaylistID"
    const val PREF_KEY_PlaylistName = "PlaylistName"
    const val PREF_KEY_PlaylistDescription = "PlaylistDescription"
    const val PREF_KEY_PlaylistType = "PlaylistType"
    const val PREF_KEY_Totalhour = "Totalhour"
    const val PREF_KEY_Totalminute = "Totalminute"
    const val PREF_KEY_TotalAudio = "TotalAudio"
    const val PREF_KEY_ScreenView = "ScreenView"
    const val Token = "Token"
    const val RecommendedCatMain = "RecommendedCatMain"
    const val selectedCategoriesTitle = "selectedCategoriesTitle"
    const val selectedCategoriesName = "selectedCategoriesName"
    const val AssMain = "AssMain"
    const val AssQus = "AssQus"
    const val AssAns = "AssAns"
    const val AssSort = "AssSort"

    const val name = "Name"
    const val email = "Email"
    const val countryCode = "CountryCode"
    const val countryShortName = "CountryShortName"
    const val countryName = "CountryName"
    const val signupFlag = "SignupFlag"
    const val mobileNumber = "MobileNo"

    // Files
    const val DOWNLOAD_AUDIO_URL = "http://www.noiseaddicts.com/samples_1w72b820/272.mp3"
    const val FILE_NAME = "audio.mp3"
    const val TEMP_FILE_NAME = "temp"
    const val FILE_EXT = ".mp3"
    const val DIR_NAME = "Audio"
    const val OUTPUT_KEY_LENGTH = 256

    // Algorithm
    const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
    const val KEY_SPEC_ALGORITHM = "AES"
    const val PROVIDER = "BC"
    const val SECRET_KEY = "SECRET_KEY"
    const val REGISTRATION_COMPLETE = "registrationComplete"

    // Segment Events
    const val Wellness_Score_Screen_Viewed = "Wellness Score Screen Viewed"
    const val Assessment_Screen_Viewed = "Assessment Screen Viewed"
    const val Assessment_Form_Submitted = "Assessment Form Submitted"
    const val Billing_Order_Screen_Viewed = "Billing & Order Screen Viewed"
    const val Cancel_Subscription_Viewed = "Cancel Subscription Viewed"
    const val Assessment_Start_Screen_Viewed = "Assessment Start Screen Viewed"
    const val Email_Sent_Screen_Viewed = "Email Sent Screen Viewed"
    const val User_Sign_up = "User Sign up"
    const val User_Login   = "User Login"
    const val Resend_OTP_Clicked ="Resend OTP Clicked"

}