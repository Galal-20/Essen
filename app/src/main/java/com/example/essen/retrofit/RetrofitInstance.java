package com.example.essen.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    public static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    public static Retrofit retrofit;

    public static MealAPI getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(MealAPI.class);
    }
}

