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

    @Query("SELECT COUNT(*) FROM meal_plans WHERE strMeal = :mealName")
    int isMealInMealPlan(String mealName);

    @Query("SELECT * FROM meal_plans")
    List<MealPlanEntity> getAllMealPlans();

}


/*
@Query("SELECT * FROM meal_plans")
List<MealPlanEntity> getAllMealPlans();*/
/*@Query("SELECT * FROM meal_plans WHERE firestoreId = :firestoreId LIMIT 1")
    MealPlanEntity getMealPlanByFirestoreId(String firestoreId);*/ /* @Query("DELETE FROM meal_plans")
    void clearAllMealPlans();*/