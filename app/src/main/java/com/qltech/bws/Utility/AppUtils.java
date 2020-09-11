package com.qltech.bws.Utility;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import static com.qltech.bws.BWSApplication.getContext;
import static java.sql.DriverManager.println;

public class AppUtils extends Application {
    public static final String Live_MAIN_URL = "http://brainwellnessspa.com.au/bwsapi/api/staging/v1/";
    public static final String BASE_URL = Live_MAIN_URL;

    public static final String DEVELOPER_KEY = "AIzaSyD43ZM6bESb_pdSPzgcuCzKy8yD_45mlT8";

    public static final String YOUTUBE_VIDEO_CODE = "y1rfRW6WX08";
}