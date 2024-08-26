package com.example.essen.Fragments.FavoritFragment;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essen.repository.MealRepositoryImpl;
import com.example.essen.room.MealEntity;

import java.util.List;

public class FavoritePresenter {
    private final MealRepositoryImpl mealRepository;

    public FavoritePresenter(Application application) {
        mealRepository = new MealRepositoryImpl(application);
    }

    public LiveData<List<MealEntity>> getFavoriteMeals() {
        return mealRepository.getFavoriteMeals();
    }

}
