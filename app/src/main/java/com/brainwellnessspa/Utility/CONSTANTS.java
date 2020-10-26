package com.brainwellnessspa.Utility;

public class CONSTANTS {
    public static final String UTF_8 = "UTF-8";

    public interface ACTION {
        public static String MAIN_ACTION = "com.brainwellnessspa.action.main";
        public static String INIT_ACTION = "com.brainwellnessspa.action.init";
        public static String PREV_ACTION = "com.brainwellnessspa.action.prev";
        public static String PLAY_ACTION = "com.brainwellnessspa.action.play";
        public static String NEXT_ACTION = "com.brainwellnessspa.action.next";
        public static String STARTFOREGROUND_ACTION = "com.brainwellnessspa.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.brainwellnessspa.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

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

    public static final String FLAG_ZERO = "0";
    public static final String FLAG_ONE = "1";
    public static final String FLAG_TWO = "2";
    public static final String FLAG_THREE = "3";
    public static final String FLAG_FOUR = "4";
    public static final String FLAG_FIVE = "5";
    public static final String FLAG_SIX = "6";
    public static final String FLAG_SEVEN = "7";
    public static final String FLAG_EIGHT = "8";
    public static final String FLAG_NINE = "9";
    public static final String FLAG_TEN = "10";
    public static final String FLAG_ELEVEN = "11";
    public static final String FLAG_TWELVE = "12";
    public static final String FLAG_THIRTEEN = "13";
    public static final String FLAG_FORTEEN = "14";
    public static final String FLAG_FIFTEEN = "15";

    public static final String YEAR_TO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String TWENTY_FOUR_HOUR_FORMAT_WITH_SECOND = "HH:mm:ss";
    public static final String TWELVE_HOUR_FORMAT_WITH_AM_PM = "hh:mm a";
    public static final String MONTH_DATE_YEAR_FORMAT = "MMM dd, yyyy";
    public static final String SERVER_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TWENTY_FOUR_HH_MM_FORMAT = "HH:mm";

    public static String Tnc = "Tnc";
    public static String Web = "Web";
    public static String Check = "Check";
    public static String Name = "Name";
    public static String Like = "Like";
    public static String Download = "Download";
    public static String position = "position";
    public static String AudioList = "AudioList";
    public static String Code = "Code";
    public static String Codes = "Codes";
    public static String MobileNo = "MobileNo";
    public static String ID = "ID";
    public static String PlaylistID = "PlaylistID";
    public static String PlaylistName = "PlaylistName";
    public static String PlaylistImage = "PlaylistImage";
    public static String title = "title";
    public static String author = "author";
    public static String linkOne = "linkOne";
    public static String linkTwo = "linkTwo";
    public static String image = "image";
    public static String description = "description";
    public static String AudioFile = "AudioFile";
    public static String ImageFile = "ImageFile";
    public static String AudioDirection = "AudioDirection";
    public static String Audiomastercat = "Audiomastercat";
    public static String AudioSubCategory = "AudioSubCategory";

    //multipart tag for file upload
    public static final String MULTIPART_FORMAT = "multipart/form-data";

    public static final String PREF_KEY_LOGIN = "Login";
    public static final String PREF_KEY_Splash = "Splash";
    public static final String PREF_KEY_SplashKey = "SplashKey";
    public static final String PREF_KEY_AUDIO = "AUDIO";
    public static final String PREF_KEY_UserID = "UserID";
    public static final String PREF_KEY_LOGOUT = "Logout";
    public static final String PREF_KEY_LOGOUT_UserID = "UserID";
    public static final String PREF_KEY_LOGOUT_MobileNO = "MobileNO";
    public static final String PREF_KEY_ExpDate = "ExpDate";
    public static final String PREF_KEY_IsRepeat = "IsRepeat";
    public static final String PREF_KEY_Status = "Status";
    public static final String PREF_KEY_IsShuffle = "IsShuffle";
    public static final String PREF_KEY_MobileNo = "MobileNo";
    public static final String PREF_KEY_IsLock = "IsLock";
    public static final String PREF_KEY_modelList = "modelList";
    public static final String PREF_KEY_audioList = "audioList";
    public static final String PREF_KEY_queueList = "queueList";
    public static final String PREF_KEY_queuePlay = "queuePlay";
    public static final String PREF_KEY_position = "position";
    public static final String PREF_KEY_audioPlay = "audioPlay";
    public static final String PREF_KEY_AudioFlag = "AudioFlag";
    public static final String PREF_KEY_Cat_Name = "Cat_Name";
    public static final String PREF_KEY_myPlaylist = "myPlaylist";
    public static final String PREF_KEY_PlaylistId = "PlaylistId";
    public static final String PREF_KEY_DownloadPlaylist = "PlaylistId";
    public static final String PREF_KEY_DownloadName = "Name";
    public static final String PREF_KEY_DownloadUrl = "Url";
    public static final String PREF_KEY_DownloadPlaylistId = "downloadPlaylistId";
    public static final String PREF_KEY_Logout_DownloadPlaylist = "PlaylistId";
    public static final String PREF_KEY_Logout_DownloadName = "Name";
    public static final String PREF_KEY_Logout_DownloadUrl = "Url";
    public static final String PREF_KEY_Logout_DownloadPlaylistId = "downloadPlaylistId";
    //    public static final String PREF_KEY_removedDownloadPlaylist = "removedPlaylistId";
//    public static final String PREF_KEY_removedDownloadName = "removedName";
//    public static final String PREF_KEY_removedDownloadPlaylistId = "removedDownloadPlaylistId";
    public static final String PREF_KEY_CardID = "CardID";
    public static final String PREF_KEY_Name = "Name";
    public static final String PREF_KEY_Image = "Image";
    public static final String PREF_KEY_Plan = "Plan";

    public static final String Token = "Token";

    // Files
    public static final String DOWNLOAD_AUDIO_URL = "http://www.noiseaddicts.com/samples_1w72b820/272.mp3";
    public static final String FILE_NAME = "audio.mp3";
    public static final String TEMP_FILE_NAME = "temp";
    public static final String FILE_EXT = ".mp3";
    public static final String DIR_NAME = "Audio";
    public static final int OUTPUT_KEY_LENGTH = 256;

    // Algorithm
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String KEY_SPEC_ALGORITHM = "AES";
    public static final String PROVIDER = "BC";

    public static final String SECRET_KEY = "SECRET_KEY";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
}