package com.example.essen.repository;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.essen.Fragments.MealPlanFragment.MealPlanCallback;
import com.example.essen.Fragments.MealPlanFragment.MealPlanUpdateListener;
import com.example.essen.model.AuthService;
import com.example.essen.pojo.CategoryList;
import com.example.essen.pojo.CatygoryByMeal;
import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public interface MealRepository {
    void getRandomMeal(Callback<MealList> callback);

    void getCategories(Callback<CategoryList> callback);

    void getPopularMeals(Callback<List<MainMeal>> callback);

    void getCountries(Callback<List<MainMeal>> callback);


    Call<MealList> searchMeals(String query);

    Call<MealList> searchMealsByCountry(String country);

    Call<MealList> searchMealsByIngredient(String ingredient);

    Call<MealList> searchMealsByCategory(String category);


    void getMealsForDay(String day, MealPlanCallback callback);

    void getMealsForMonth(int month, MealPlanCallback callback);

    void getMealsForYear(int year, MealPlanCallback callback);

    void getMealsForDayNumber(int dayNumber, MealPlanCallback callback);

    void addMealPlanUpdateListener(MealPlanUpdateListener listener);


    boolean isLoggedIn();

    void setGuestMode(boolean isGuest);

    FirebaseUser getCurrentUser();

    void signOut(AuthService.AuthCallback callback);

    void updateLocale(Context context, String languageCode);

    void clearLocalFavorites();

    void saveSelectedLanguage(String languageCode);

    String getSelectedLanguage();

    void getMealsByCategory(String categoryName, Callback<CatygoryByMeal> callback);


    void resetPassword(String email, AuthService.AuthCallback callback);


    void login(String email, String password, AuthService.AuthCallback callback);

    void signInWithGoogle(Activity activity, AuthService.AuthCallback callback);

    void handleGoogleSignInResult(Intent data, AuthService.AuthCallback callback);

    void signUp(String fullName, String email, String password, AuthService.AuthCallback callback);


}









