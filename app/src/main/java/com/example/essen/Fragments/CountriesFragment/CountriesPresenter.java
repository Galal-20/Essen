package com.example.essen.Fragments.CountriesFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.Toast;

import com.example.essen.pojo.Countries;
import com.example.essen.pojo.MainMeal;
import com.example.essen.retrofit.MealAPI;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountriesPresenter implements CountriesContract.Presenter {

    private CountriesContract.View view;
    private MealAPI mealAPI;
    private Context context;

    public CountriesPresenter(CountriesContract.View view, MealAPI mealAPI, Context context) {
        this.view = view;
        this.mealAPI = mealAPI;
        this.context = context;
    }

    public void getCountries() {
        List<MainMeal> cachedCountries = loadCountriesFromCache();
        if (cachedCountries != null) {
            view.showCountries(cachedCountries);
        }

        mealAPI.getCountries().enqueue(new Callback<Countries>() {
            @Override
            public void onResponse(Call<Countries> call, Response<Countries> response) {
                if (response.isSuccessful()) {
                    Countries countries = response.body();
                    saveCountriesToCache(countries);
                    view.showCountries(countries.getMeals());
                }
            }

            @Override
            public void onFailure(Call<Countries> call, Throwable t) {
                if (context != null) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
                view.showError(t.getMessage());
            }
        });
    }

    private void saveCountriesToCache(Countries countries) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(countries);

        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(byteStream);
            gzip.write(json.getBytes());
            gzip.close();
            String compressedJson = Base64.encodeToString(byteStream.toByteArray(), Base64.DEFAULT);
            editor.putString("countries_cache", compressedJson);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<MainMeal> loadCountriesFromCache() {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String compressedJson = prefs.getString("countries_cache", null);

        if (compressedJson != null) {
            try {
                byte[] compressedBytes = Base64.decode(compressedJson, Base64.DEFAULT);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedBytes);
                GZIPInputStream gzip = new GZIPInputStream(byteStream);
                InputStreamReader reader = new InputStreamReader(gzip);
                Countries countries = gson.fromJson(reader, Countries.class);
                return countries.getMeals();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}


/*import com.example.essen.pojo.Countries;
import com.example.essen.retrofit.MealAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountriesPresenter implements CountriesContract.Presenter {

    private CountriesContract.View view;
    private MealAPI mealAPI;

    public CountriesPresenter(CountriesContract.View view, MealAPI mealAPI) {
        this.view = view;
        this.mealAPI = mealAPI;
    }

    public void getCountries() {
        mealAPI.getCountries().enqueue(new Callback<Countries>() {
            @Override
            public void onResponse(Call<Countries> call, Response<Countries> response) {
                if (response.isSuccessful()) {
                    view.showCountries(response.body().getMeals());
                }
            }
            @Override
            public void onFailure(Call<Countries> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }
}*/
/*import android.content.Context;
import android.content.SharedPreferences;

import com.example.essen.pojo.MainMeal;
import com.example.essen.retrofit.MealAPI;
import com.google.gson.Gson;
import com.example.essen.pojo.Countries;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountriesPresenter implements CountriesContract.Presenter {

    private CountriesContract.View view;
    private MealAPI mealAPI;
    private Context context;

    public CountriesPresenter(CountriesContract.View view, MealAPI mealAPI, Context context) {
        this.view = view;
        this.mealAPI = mealAPI;
        this.context = context;
    }

    public void getCountries() {
        mealAPI.getCountries().enqueue(new Callback<Countries>() {
            @Override
            public void onResponse(Call<Countries> call, Response<Countries> response) {
                if (response.isSuccessful()) {
                    // Cache data
                    saveCountriesToCache(response.body());
                    view.showCountries(response.body().getMeals());
                }
            }

            @Override
            public void onFailure(Call<Countries> call, Throwable t) {
                // Try to load from cache if network fails
                List<MainMeal> cachedCountries = loadCountriesFromCache();
                if (cachedCountries != null) {
                    view.showCountries(cachedCountries);
                } else {
                    view.showError(t.getMessage());
                }
            }
        });
    }

    private void saveCountriesToCache(Countries countries) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(countries);
        editor.putString("countries_cache", json);
        editor.apply();
    }

    private List<MainMeal> loadCountriesFromCache() {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("countries_cache", null);
        if (json != null) {
            Countries countries = gson.fromJson(json, Countries.class);
            return countries.getMeals();
        }
        return null;
    }
}*/
/*public void getMealsByCountry(String countryName) {
        mealAPI.getMealsByCountry(countryName).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showMeals(response.body().getMeals());
                } else {
                    view.showError("Failed to fetch meals");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }*/