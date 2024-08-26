package com.example.essen.Fragments.MealPlanFragment;

import com.example.essen.repository.MealRepository;
import com.example.essen.room.MealPlanEntity;

import java.util.List;

public class MealPlanPresenter implements MealPlanContract.Presenter {

    private final MealPlanContract.View view;
    private final MealRepository repository;

    public MealPlanPresenter(MealPlanContract.View view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void loadMealsForDay(String day) {
        repository.getMealsForDay(day, new MealPlanCallback() {
            @Override
            public void onMealPlansLoaded(List<MealPlanEntity> mealPlans) {
                view.showMeals(mealPlans);
            }

            @Override
            public void onError(Exception e) {
                view.showError(e.getMessage());
            }
        });
    }

    @Override
    public void loadMealsForMonth(int month) {
        repository.getMealsForMonth(month, new MealPlanCallback() {
            @Override
            public void onMealPlansLoaded(List<MealPlanEntity> mealPlans) {
                view.showMeals(mealPlans);
            }

            @Override
            public void onError(Exception e) {
                view.showError(e.getMessage());
            }
        });
    }

    @Override
    public void loadMealsForYear(int year) {
        repository.getMealsForYear(year, new MealPlanCallback() {
            @Override
            public void onMealPlansLoaded(List<MealPlanEntity> mealPlans) {
                view.showMeals(mealPlans);
            }

            @Override
            public void onError(Exception e) {
                view.showError(e.getMessage());
            }
        });
    }

    @Override
    public void loadMealsForDayNumber(int dayNumber) {
        repository.getMealsForDayNumber(dayNumber, new MealPlanCallback() {
            @Override
            public void onMealPlansLoaded(List<MealPlanEntity> mealPlans) {
                view.showMeals(mealPlans);
            }

            @Override
            public void onError(Exception e) {
                view.showError(e.getMessage());
            }
        });
    }

    @Override
    public void listenForMealPlanUpdates() {
        repository.addMealPlanUpdateListener(new MealPlanUpdateListener() {
            @Override
            public void onMealPlansUpdated(List<MealPlanEntity> mealPlans) {
                view.showMeals(mealPlans);
            }

            @Override
            public void onError(Exception e) {
                view.showError(e.getMessage());
            }
        });
    }


    @Override
    public void onDestroy() {

    }
}
