package com.example.essen.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface MealPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MealPlanEntity mealPlan);


    @Delete
    Completable delete(MealPlanEntity mealPlan);

    @Query("SELECT COUNT(*) FROM meal_plans WHERE strMeal = :mealName AND dayName = :day")
    int isMealInMealPlan(String mealName, String day);



    @Query("SELECT * FROM meal_plans")
    List<MealPlanEntity> getAllMealPlans();

    @Query("SELECT * FROM meal_plans WHERE dayName = :day")
    List<MealPlanEntity> getMealsForDay(String day);

    @Query("DELETE FROM meal_plans WHERE dayName = :day AND strMeal = :mealName")
    void deleteMealFromDay(String day, String mealName);




}


  /*@Query("DELETE FROM meal_plans WHERE dayName = :day")
    Completable deleteMealFromDay(String day);*/

/*
@Query("SELECT * FROM meal_plans WHERE dayName BETWEEN :startDay AND :endDay")
List<MealPlanEntity> getMealsForWeek(String startDay, String endDay);*/
