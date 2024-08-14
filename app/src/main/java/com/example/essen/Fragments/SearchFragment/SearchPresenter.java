package com.example.essen.Fragments.SearchFragment;

import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.example.essen.retrofit.MealAPI;
import com.example.essen.retrofit.RetrofitInstance;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPresenter implements SearchContract.Presenter {
    private final SearchContract.View view;
    private final MealAPI mealAPI;

    public SearchPresenter(SearchContract.View view) {
        this.view = view;
        this.mealAPI = RetrofitInstance.getApi();
    }

   /* @Override
    public void searchMeals(String query) {
        mealAPI.getSearchMeal(query).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MainMeal> meals = response.body().getMeals();
                    if (meals != null && !meals.isEmpty()) {
                        view.showMeals(meals);
                    } else {
                        view.showError("No results found.");
                    }
                } else {
                    view.showError("Error fetching data.");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }*/

    @Override
    public void searchMeals(String query) {
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mealAPI.getSearchMeal(query).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MainMeal> meals = response.body().getMeals();
                    if (meals != null && !meals.isEmpty()) {
                        view.showMeals(meals);
                    } else {
                        view.showError("No results found.");
                    }
                } else {
                    view.showError("Error fetching data.");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }


    @Override
    public void searchMealsByCountry(String country) {
        try {
            country = URLEncoder.encode(country, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mealAPI.getMealsByCountry(country).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MainMeal> meals = response.body().getMeals();
                    view.showMeals(meals);
                } else {
                    view.showError("No meals found for this country.");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }

    @Override
    public void searchMealsByIngredient(String ingredient) {
        try {
            ingredient = URLEncoder.encode(ingredient, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mealAPI.getMealsByIngredient(ingredient).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showMeals(response.body().getMeals());
                } else {
                    view.showError("No meals found for this ingredient.");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }

    @Override
    public void searchMealsByCategory(String category) {
        try {
            category = URLEncoder.encode(category, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mealAPI.searchMealsByCategory(category).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showMeals(response.body().getMeals());
                } else {
                    view.showError("No meals found for this category.");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }


}



/* if (response.isSuccessful() && response.body() != null) {
                    // Extract the list of meals from the MealList object
                    List<MainMeal> meals = response.body().getMeals();
                    view.showMeals(meals);
                } else {
                    view.showError("No results found.");
                }*/
 /*@Override
    public void searchMeals(String query) {
        mealAPI.getSearchMeal(query).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showMeals(response.body().getMeals());
                } else {
                    view.showError("No results found.");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }*/