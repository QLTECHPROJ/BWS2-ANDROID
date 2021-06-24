package com.brainwellnessspa.utility;

import android.provider.Settings;

import com.androidnetworking.AndroidNetworking;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.brainwellnessspa.BWSApplication.getContext;
import static com.brainwellnessspa.BWSApplication.securityKey;
import static com.brainwellnessspa.utility.AppUtils.BASE_URL;

public class APIClient {
    private static Retrofit retrofit = null;

    public static APIInterface getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS);

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder().header("Oauth", securityKey()).header("Yaccess", Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID)).method(original.method(), original.body()).build();
            return chain.proceed(request);
        });

        /*Gson gson = new GsonBuilder()
                .setLenient()
                .create();*/

        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).addConverterFactory(ScalarsConverterFactory.create()).client(client).build();

        AndroidNetworking.initialize(getContext(), client);
        APIInterface service = retrofit.create(APIInterface.class);
        return service;
    }
}