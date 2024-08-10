package com.example.essen.FavoritFragment;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essen.room.MealEntity;

import java.util.List;

public class FavoritePresenter {
    private final MealRepository mealRepository;

    public FavoritePresenter(Application application) {
        mealRepository = new MealRepository(application);
        //loadFavoriteMeals();
    }

    public LiveData<List<MealEntity>> getFavoriteMeals() {
        return mealRepository.getFavoriteMeals();
    }

    /*private void loadFavoriteMeals() {
        // LiveData will automatically update the observer
    }*/
}

