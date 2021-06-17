package com.brainwellnessspa.utility

import android.annotation.SuppressLint
import android.provider.Settings
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.utility.AppUtils.New_BASE_URL
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit.RequestInterceptor
import retrofit.RequestInterceptor.RequestFacade
import retrofit.RestAdapter
import retrofit.client.OkClient
import retrofit.converter.GsonConverter
import java.util.concurrent.TimeUnit

object APIClientProfile {
    @JvmStatic
    var apiService: APIInterfaceProfile? = null
        get() = if (field == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient()
            okHttpClient.setConnectTimeout(0, TimeUnit.HOURS)
            okHttpClient.setWriteTimeout(0, TimeUnit.HOURS)
            okHttpClient.setReadTimeout(0, TimeUnit.HOURS)
            val restAdapter: RestAdapter = RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(New_BASE_URL)
                .setRequestInterceptor(MyRetrofitInterceptor())
                .setClient(OkClient(okHttpClient))
                .setConverter(GsonConverter(Gson()))
                .build()
            field = restAdapter.create(
                APIInterfaceProfile::class.java
            )
            field
        } else {
            field
        }
        private set

    private class MyRetrofitInterceptor : RequestInterceptor {
        @SuppressLint("HardwareIds")
        override fun intercept(request: RequestFacade) {
//            request.addHeader("platform", "Android");
//            request.addHeader("osversion", Build.VERSION.RELEASE);
//            request.addHeader("device", Build.MODEL);
//            request.addHeader("appVersion", YupITApplication.getVersionCode());
//            request.addHeader("tokenkey", "");
            request.addHeader("Oauth", BWSApplication.securityKey())
            request.addHeader("Newtoken", "1")
            request.addHeader(
                "Yaccess",
                Settings.Secure.getString(
                    BWSApplication.getContext().contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            )
        }
    }
}