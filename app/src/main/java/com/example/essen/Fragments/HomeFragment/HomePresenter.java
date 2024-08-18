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

        // Proceed with network request
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


    /*public void getPopularMeals() {
        RetrofitInstance.getApi().getRandom().enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.body() != null && !response.body().getMeals().isEmpty()){
                    view.showPopularMeals(response.body().getMeals());
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError("Check network");
            }
        });
    }*/
   /* public void getPopularMeals() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<Meal>> callables = new ArrayList<>();

        int mealCount = 30;
        for (int i = 0; i < mealCount; i++) {
            callables.add(() -> {
                try {
                    Response<MealList> response = RetrofitInstance.getApi().getRandom().execute();
                    if (response.body() != null && !response.body().getMeals().isEmpty()) {
                        return response.body().getMeals().get(0);
                    } else {
                        throw new RuntimeException("No meal found");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to fetch meal", e);
                }
            });
        }

        try {

            List<Future<Meal>> futures = executorService.invokeAll(callables);


            List<Meal> mealList = new ArrayList<>();
            for (Future<Meal> future : futures) {
                try {
                    mealList.add(future.get(1, TimeUnit.SECONDS));
                } catch (TimeoutException e) {
                    Log.e("HomePresenter", "Timeout while fetching meal", e);
                }
            }


            view.showPopularMeals(mealList);

        } catch (InterruptedException | ExecutionException e) {
            view.showError("Error fetching meals: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }*/
