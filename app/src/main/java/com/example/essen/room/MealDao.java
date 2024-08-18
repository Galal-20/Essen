package com.example.essen.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MealDao {

    @Insert
    void insert(MealEntity mealEntity);


    @Query("SELECT * FROM meal_table")
    LiveData<List<MealEntity>> getAllMeals();

    @Query("SELECT COUNT(*) FROM meal_table WHERE strMeal = :mealName")
    int isMealInFavorites(String mealName);

    @Query("DELETE FROM meal_table WHERE strMeal = :mealName")
    void deleteMeal(String mealName);


    @Query("SELECT * FROM meal_table")
    List<MealEntity> getAllMealPlans();

}


