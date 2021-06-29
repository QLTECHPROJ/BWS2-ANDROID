package com.brainwellnessspa.utility

import android.annotation.SuppressLint
import android.provider.Settings
import com.androidnetworking.AndroidNetworking
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.utility.AppUtils.New_BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object APINewClient {
    private var retrofit: Retrofit? = null

    /*Gson gson = new GsonBuilder()
                .setLenient()
                .create();*/
    @JvmStatic
    val client: APINewInterface
        @SuppressLint("HardwareIds") get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
            httpClient.readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS)
            httpClient.addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val request = original.newBuilder().header("Oauth", BWSApplication.securityKey()).header("Yaccess", Settings.Secure.getString(BWSApplication.getContext().contentResolver, Settings.Secure.ANDROID_ID)).method(original.method, original.body).build()
                chain.proceed(request)
            })

            /*Gson gson = new GsonBuilder()
                .setLenient()
                .create();*/
            val client: OkHttpClient = httpClient.addInterceptor(interceptor).build()
            retrofit = Retrofit.Builder().baseUrl(New_BASE_URL).addConverterFactory(GsonConverterFactory.create()).addConverterFactory(ScalarsConverterFactory.create()).client(client).build()
            AndroidNetworking.initialize(BWSApplication.getContext(), client)
            return retrofit!!.create(APINewInterface::class.java)
        }
}