package com.brainwellnessspa.Utility;

import android.app.Application;

public class AppUtils extends Application {
    public static final String MAIN_URL = "https://brainwellnessapp.com.au";
    public static final String STAGING_MAIN_URL = "http://brainwellnessapp.com.au/bwsapi/api/staging/v1/";
    public static final String Live_MAIN_URL = "http://brainwellnessapp.com.au/bwsapi/api/live/v1/";
    public static final String BASE_URL = STAGING_MAIN_URL;

    public static final String DEVELOPER_KEY = "AIzaSyD43ZM6bESb_pdSPzgcuCzKy8yD_45mlT8";
    public static final String YOUTUBE_VIDEO_CODE = "y1rfRW6WX08";

    public static String tncs_url = MAIN_URL + "/terms-conditions/";
    public static String privacy_policy_url = MAIN_URL + "/privacy-policy/";
    public static String how_refer_works_url = MAIN_URL + "/how-refer-works/";
}