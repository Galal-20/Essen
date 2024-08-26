package com.example.essen.Fragments.HomeFragment;

import com.example.essen.pojo.CategoryList;
import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.example.essen.repository.MealRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View view;
    private MealRepository mealRepository;

    public HomePresenter(HomeContract.View view, MealRepository mealRepository) {
        this.view = view;
        this.mealRepository = mealRepository;
    }

    @Override
    public void getRandomMeal() {
        mealRepository.getRandomMeal(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getMeals().isEmpty()) {
                    MainMeal randomMeal = response.body().getMeals().get(0);
                    view.showRandomMeal(randomMeal);
                } else {
                    view.showError("Failed to fetch random meal");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }

    @Override
    public void getPopularMeals() {
        mealRepository.getPopularMeals(new Callback<List<MainMeal>>() {
            @Override
            public void onResponse(Call<List<MainMeal>> call, Response<List<MainMeal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showPopularMeals(response.body());
                } else {
                    view.showError("Failed to fetch popular meals");
                }
            }

            @Override
            public void onFailure(Call<List<MainMeal>> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });

    }

    @Override
    public void getCategories() {
        mealRepository.getCategories(new Callback<CategoryList>() {
            @Override
            public void onResponse(Call<CategoryList> call, Response<CategoryList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showCategories(response.body().getCategories());
                } else {
                    view.showError("Failed to fetch categories");
                }
            }

            @Override
            public void onFailure(Call<CategoryList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }
}

/*
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
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
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

            }
        });
    }
}


*/
