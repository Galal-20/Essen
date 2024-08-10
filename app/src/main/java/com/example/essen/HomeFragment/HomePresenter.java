package com.example.essen.HomeFragment;

import android.util.Log;

import com.example.essen.pojo.CategoryList;
import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.example.essen.retrofit.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View view;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void getRandomMeal() {
        RetrofitInstance.getApi().getRandom().enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.body() != null && !response.body().getMeals().isEmpty()) {
                    view.showRandomMeal(response.body().getMeals().get(0));
                    Log.i("name", response.body().getMeals().get(0).getStrMeal());
                    Log.i("link", response.body().getMeals().get(0).getStrYoutube());
                } else {
                    view.showError("Data not response");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError("Check network");
            }
        });
    }

    @Override
    public void getPopularMeals() {
        List<MainMeal> mealList = new ArrayList<>();

        int mealCount = 30;
        for (int i = 0; i < mealCount; i++) {
            RetrofitInstance.getApi().getRandom().enqueue(new Callback<MealList>() {
                @Override
                public void onResponse(Call<MealList> call, Response<MealList> response) {
                    if (response.body() != null && !response.body().getMeals().isEmpty()) {
                        mealList.add(response.body().getMeals().get(0));
                        // When all meals are fetched, update the view
                        if (mealList.size() == mealCount) {
                            view.showPopularMeals(mealList);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MealList> call, Throwable t) {
                    view.showError("Check network");
                }
            });
        }
    }

    @Override
    public void getCategories() {
        RetrofitInstance.getApi().getCategories().enqueue(new Callback<CategoryList>() {
            @Override
            public void onResponse(Call<CategoryList> call, Response<CategoryList> response) {
                if (response.body() != null && !response.body().getCategories().isEmpty()) {
                    view.showCategories(response.body().getCategories());
                }
            }

            @Override
            public void onFailure(Call<CategoryList> call, Throwable t) {
                view.showError("Data not response");

            }
        });
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
        ExecutorService executorService = Executors.newFixedThreadPool(5); // Adjust pool size as needed
        List<Callable<Meal>> callables = new ArrayList<>();

        int mealCount = 30; // Number of meals to fetch
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
            // Wait for all tasks to complete
            List<Future<Meal>> futures = executorService.invokeAll(callables);

            // Get results from futures
            List<Meal> mealList = new ArrayList<>();
            for (Future<Meal> future : futures) {
                try {
                    mealList.add(future.get(1, TimeUnit.SECONDS)); // Adjust timeout as needed
                } catch (TimeoutException e) {
                    Log.e("HomePresenter", "Timeout while fetching meal", e);
                }
            }

            // Update the view with the fetched meals
            view.showPopularMeals(mealList);

        } catch (InterruptedException | ExecutionException e) {
            view.showError("Error fetching meals: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }*/


}
