package com.example.jsonproject.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
    // Declare and Init our base url
    private static final String BASE_URL = "http://shibe.online/api/";

    // Declare Retrofit object
    private static Retrofit retrofit;

    // Create a private constructor
    private RetrofitClientInstance() {}

    // Create public static method to get instance of the Retrofit object

    public static Retrofit getRetrofit() {
        // This statement creates a new instance of Retrofit if the current instance is null

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
