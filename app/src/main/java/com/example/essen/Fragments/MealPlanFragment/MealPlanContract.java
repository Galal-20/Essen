package com.example.essen.Fragments.MealPlanFragment;

import com.example.essen.room.MealPlanEntity;

import java.util.List;

public interface MealPlanContract {

    interface View {
        void showMeals(List<MealPlanEntity> meals);

        void showError(String message);
    }

    interface Presenter {
        void loadMealsForDay(String day);

        void loadMealsForMonth(int month);

        void loadMealsForYear(int year);

        void loadMealsForDayNumber(int dayNumber);

        void listenForMealPlanUpdates();

        void onDestroy();
    }
}
