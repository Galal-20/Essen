package com.example.essen.Fragments.CountriesFragment;

import com.example.essen.pojo.MainMeal;
import com.example.essen.repository.MealRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountriesPresenter implements CountriesContract.Presenter {

    private CountriesContract.View view;
    private MealRepository mealRepository;

    public CountriesPresenter(CountriesContract.View view, MealRepository mealRepository) {
        this.view = view;
        this.mealRepository = mealRepository;
    }


    @Override
    public void getCountries() {
        mealRepository.getCountries(new Callback<List<MainMeal>>() {
            @Override
            public void onResponse(Call<List<MainMeal>> call, Response<List<MainMeal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showCountries(response.body());
                } else {
                    view.showError("Failed to load countries");
                }
            }

            @Override
            public void onFailure(Call<List<MainMeal>> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }
}
   /* public CountriesPresenter(CountriesContract.View view, MealAPI mealAPI) {
        this.view = view;
        this.mealAPI = mealAPI;
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
*/