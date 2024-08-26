package com.example.essen.Fragments.MealPlanFragment;

import com.example.essen.room.MealPlanEntity;

import java.util.List;

public interface MealPlanUpdateListener {
    void onMealPlansUpdated(List<MealPlanEntity> mealPlans);

    void onError(Exception e);
}
