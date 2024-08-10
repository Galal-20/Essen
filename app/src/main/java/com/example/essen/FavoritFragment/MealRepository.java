package com.example.essen.FavoritFragment;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealDao;
import com.example.essen.room.MealEntity;

import java.util.List;

public class MealRepository {
    private final MealDao mealDao;

    public MealRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        mealDao = database.mealDao();
    }

    public LiveData<List<MealEntity>> getFavoriteMeals() {
        return mealDao.getAllMeals();
    }
}


