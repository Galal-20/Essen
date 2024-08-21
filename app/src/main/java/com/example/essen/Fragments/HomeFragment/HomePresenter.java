package com.example.essen.Fragments.HomeFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.essen.pojo.Category;
import com.example.essen.pojo.CategoryList;
import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.example.essen.retrofit.RetrofitInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View view;
    Context context;

    public HomePresenter(HomeContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void getRandomMeal() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String cachedMealJson = sharedPreferences.getString("randomMeal", null);

        if (cachedMealJson != null) {
            Log.d("RandomMeal", "Loaded from cache: " + cachedMealJson);
            MainMeal cachedMeal = new Gson().fromJson(cachedMealJson, MainMeal.class);
            view.showRandomMeal(cachedMeal);
        }

        RetrofitInstance.getApi().getRandom().enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.body() != null && !response.body().getMeals().isEmpty()) {
                    MainMeal randomMeal = response.body().getMeals().get(0);
                    view.showRandomMeal(randomMeal);
                    Log.i("RandomMeal", "Received from network: " + new Gson().toJson(randomMeal));

                    // Save to cache
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("randomMeal", new Gson().toJson(randomMeal));
                    editor.apply();
                    Log.d("RandomMeals", "Saved to cache: " + new Gson().toJson(randomMeal));
                } else {
                    //view.showError("Data not response");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                //view.showError("Check network");
            }
        });
    }


    @Override
    public void getPopularMeals() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String cachedMealsJson = sharedPreferences.getString("popularMeals", null);

        if (cachedMealsJson != null) {
            List<MainMeal> cachedMeals = new Gson().fromJson(cachedMealsJson, new TypeToken<List<MainMeal>>() {
            }.getType());
            view.showPopularMeals(cachedMeals);
        }

        List<MainMeal> mealList = new ArrayList<>();

        int mealCount = 30;
        for (int i = 0; i < mealCount; i++) {
            RetrofitInstance.getApi().getRandom().enqueue(new Callback<MealList>() {
                @Override
                public void onResponse(Call<MealList> call, Response<MealList> response) {
                    if (response.body() != null && !response.body().getMeals().isEmpty()) {
                        mealList.add(response.body().getMeals().get(0));
                        if (mealList.size() == mealCount) {
                            view.showPopularMeals(mealList);
                            // Save to cache
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("popularMeals", new Gson().toJson(mealList));
                            editor.apply();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MealList> call, Throwable t) {
                    //view.showError("Check network");
                }
            });
        }
    }

    @Override
    public void getCategories() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String cachedCategoriesJson = sharedPreferences.getString("categories", null);

        if (cachedCategoriesJson != null) {
            List<Category> cachedCategories = new Gson().fromJson(cachedCategoriesJson, new TypeToken<List<Category>>() {
            }.getType());
            view.showCategories(cachedCategories);
        }
        RetrofitInstance.getApi().getCategories().enqueue(new Callback<CategoryList>() {
            @Override
            public void onResponse(Call<CategoryList> call, Response<CategoryList> response) {
                if (response.body() != null && !response.body().getCategories().isEmpty()) {
                    view.showCategories(response.body().getCategories());
                    // Save to cache
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("categories", new Gson().toJson(response.body().getCategories()));
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call<CategoryList> call, Throwable t) {
                //view.showError("Data not response");

            }
        });
    }
}


