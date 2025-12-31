package com.simats.foodstall;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor; // <-- IMPORT THIS
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "http://14.139.187.229:8081/stallspot/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // --- START: ADD LOGGING INTERCEPTOR ---
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            // Set level to Level.BODY to see request headers and body
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            // --- END: ADD LOGGING INTERCEPTOR ---

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    // --- START: ADD INTERCEPTOR TO CLIENT ---
                    .addInterceptor(loggingInterceptor) // Add the logger
                    // --- END: ADD INTERCEPTOR TO CLIENT ---
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}