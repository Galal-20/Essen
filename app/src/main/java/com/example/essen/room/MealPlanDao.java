package com.example.essen.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MealPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MealPlanEntity mealPlan);

    @Query("SELECT * FROM meal_plans")
    List<MealPlanEntity> getAllMealPlans();

    @Delete
    void delete(MealPlanEntity mealPlan);

    @Query("SELECT COUNT(*) FROM meal_plans WHERE strMeal = :mealName")
    int isMealInMealPlan(String mealName);
}
