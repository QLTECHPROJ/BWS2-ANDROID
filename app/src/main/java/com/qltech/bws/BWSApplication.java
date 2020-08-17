package com.qltech.bws;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.qltech.bws.Utility.MeasureRatio;

public class BWSApplication extends Application {
    private static Context mContext;
    private static BWSApplication BWSApplication;

    public static Context getContext() {
        return mContext;
    }

    public static MeasureRatio measureRatio(Context context, float outerMargin, float aspectX, float aspectY,
                                            float proportion, float innerMargin) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        float width = (float) (displayMetrics.widthPixels / displayMetrics.density);
        float widthImg = (float) (((width - outerMargin) * proportion) - innerMargin);
        float height = (float) (widthImg * aspectY / aspectX);
        //Log.e("width.........", "" + context.getClass().getSimpleName()+","+width);
//        //Log.e("widthImg.........", "" + context.getClass().getSimpleName()+","+widthImg);
//        //Log.e("height...........", "" + context.getClass().getSimpleName()+","+height);
//        //Log.e("displayMetrics.density...........", "" + context.getClass().getSimpleName()+","+displayMetrics.density);
        return new MeasureRatio(widthImg, height, displayMetrics.density, proportion);
    }


    public static synchronized BWSApplication getInstance() {
        return BWSApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        BWSApplication = this;
    }
}
