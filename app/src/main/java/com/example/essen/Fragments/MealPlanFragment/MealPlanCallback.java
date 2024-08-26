package com.example.essen.Fragments.MealPlanFragment;

import com.example.essen.room.MealPlanEntity;

import java.util.List;

public interface MealPlanCallback {
    void onMealPlansLoaded(List<MealPlanEntity> mealPlans);

    void onError(Exception e);

}
