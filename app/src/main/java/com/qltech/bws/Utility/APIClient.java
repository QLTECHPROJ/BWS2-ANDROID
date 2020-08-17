package com.qltech.bws.Utility;

import android.provider.Settings;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.qltech.bws.Utility.AppUtils.BASE_URL;
import static com.qltech.bws.Utility.AppUtils.securityKey;
import static com.qltech.bws.BWSApplication.getContext;

public class APIClient {
    private static Retrofit retrofit = null;

    public static APIInterface getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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

        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        APIInterface service = retrofit.create(APIInterface.class);
        return service;
    }
}
