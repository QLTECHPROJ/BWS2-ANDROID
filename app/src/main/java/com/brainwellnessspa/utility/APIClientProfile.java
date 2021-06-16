package com.brainwellnessspa.utility;

import android.provider.Settings;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import static com.brainwellnessspa.BWSApplication.getContext;
import static com.brainwellnessspa.BWSApplication.securityKey;
import static com.brainwellnessspa.utility.AppUtils.New_BASE_URL;

public class APIClientProfile {
    private static APIInterfaceProfile apiService;

    public static APIInterfaceProfile getApiService() {

        if (apiService == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(0, TimeUnit.HOURS);
            okHttpClient.setWriteTimeout(0, TimeUnit.HOURS);
            okHttpClient.setReadTimeout(0, TimeUnit.HOURS);
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setEndpoint(New_BASE_URL)
                    .setRequestInterceptor(new MyRetrofitInterceptor())
                    .setClient(new OkClient(okHttpClient))
                    .setConverter(new GsonConverter(new Gson()))
                    .build();
            apiService = restAdapter.create(APIInterfaceProfile.class);
            return apiService;
        } else {
            return apiService;
        }
    }


    private static class MyRetrofitInterceptor implements RequestInterceptor {
        @Override
        public void intercept(RequestFacade request) {
//            request.addHeader("platform", "Android");
//            request.addHeader("osversion", Build.VERSION.RELEASE);
//            request.addHeader("device", Build.MODEL);
//            request.addHeader("appVersion", YupITApplication.getVersionCode());
//            request.addHeader("tokenkey", "");

            request.addHeader("Oauth", securityKey());
            request.addHeader("Newtoken", "1");
            request.addHeader("Yaccess", Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));

        }
    }
}

