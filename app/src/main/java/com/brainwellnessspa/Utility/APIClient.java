package com.brainwellnessspa.Utility;

import android.provider.Settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.brainwellnessspa.BWSApplication.securityKey;
import static com.brainwellnessspa.Utility.AppUtils.BASE_URL;
import static com.brainwellnessspa.BWSApplication.getContext;

public class APIClient {
    private static Retrofit retrofit = null;

    public static APIInterface getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(80, TimeUnit.HOURS)
                .writeTimeout(80, TimeUnit.HOURS)
                .readTimeout(80, TimeUnit.HOURS);

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Oauth", securityKey())
                    .header("Newtoken", "1")
                    .header("Yaccess", Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID))
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        APIInterface service = retrofit.create(APIInterface.class);
        return service;
    }
}