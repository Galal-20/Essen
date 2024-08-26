package com.example.essen.repository;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.essen.Fragments.MealPlanFragment.MealPlanCallback;
import com.example.essen.Fragments.MealPlanFragment.MealPlanUpdateListener;
import com.example.essen.model.AuthService;
import com.example.essen.pojo.CategoryList;
import com.example.essen.pojo.CatygoryByMeal;
import com.example.essen.pojo.Countries;
import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.example.essen.retrofit.RetrofitInstance;
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealDao;
import com.example.essen.room.MealEntity;
import com.example.essen.room.MealPlanEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealRepositoryImpl implements MealRepository {

    private static final String PREFS_NAME = "MyPrefsFile";
    FirebaseAuth firebaseAuth;
    private AppDatabase appDatabase;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private SharedPreferences sharedPreferences;
    private AuthService authService;
    private MealDao mealDao;

    public MealRepositoryImpl(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        mealDao = database.mealDao();
    }

    public MealRepositoryImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public MealRepositoryImpl(AppDatabase appDatabase, FirebaseFirestore firestore, FirebaseUser currentUser) {
        this.appDatabase = appDatabase;
        this.firestore = firestore;
        this.currentUser = currentUser;
    }

    public MealRepositoryImpl(SharedPreferences sharedPreferences, FirebaseAuth firebaseAuth, AppDatabase appDatabase) {
        this.sharedPreferences = sharedPreferences;
        this.currentUser = firebaseAuth.getCurrentUser();
        this.appDatabase = appDatabase;
        this.firebaseAuth = firebaseAuth;

    }

    public MealRepositoryImpl(SharedPreferences sharedPreferences, FirebaseAuth firebaseAuth,
                              AppDatabase appDatabase, AuthService authService) {
        this.sharedPreferences = sharedPreferences;
        this.currentUser = firebaseAuth.getCurrentUser();
        this.appDatabase = appDatabase;
        this.firebaseAuth = firebaseAuth;
        this.authService = authService; // Initialize AuthService
    }

    public MealRepositoryImpl(Activity activity) {
        this.authService = new AuthService(activity);
    }

    //=================================================================================================
    // Home Meal
    @Override
    public void getRandomMeal(Callback<MealList> callback) {
        RetrofitInstance.getApi().getRandom().enqueue(callback);
    }

    @Override
    public void getCategories(Callback<CategoryList> callback) {
        RetrofitInstance.getApi().getCategories().enqueue(callback);
    }

    @Override
    public void getPopularMeals(Callback<List<MainMeal>> callback) {
        int mealCount = 30;
        List<MainMeal> aggregatedMeals = new ArrayList<>();

        for (int i = 0; i < mealCount; i++) {
            RetrofitInstance.getApi().getRandom().enqueue(new Callback<MealList>() {
                @Override
                public void onResponse(Call<MealList> call, Response<MealList> response) {
                    if (response.body() != null && !response.body().getMeals().isEmpty()) {
                        aggregatedMeals.add(response.body().getMeals().get(0));

                        if (aggregatedMeals.size() == mealCount) {
                            savePopularMealsToCache(aggregatedMeals);
                            callback.onResponse(null, Response.success(aggregatedMeals));
                        }
                    }
                }

                @Override
                public void onFailure(Call<MealList> call, Throwable t) {
                    callback.onFailure(null, t);
                }
            });
        }
    }

    //=================================================================================================
    // Countries
    @Override
    public void getCountries(Callback<List<MainMeal>> callback) {
        List<MainMeal> cachedCountries = loadCountriesFromCache();
        if (cachedCountries != null) {
            callback.onResponse(null, Response.success(cachedCountries));
        } else {
            RetrofitInstance.getApi().getCountries().enqueue(new Callback<Countries>() {
                @Override
                public void onResponse(@NonNull Call<Countries> call, Response<Countries> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        saveCountriesToCache(response.body());
                        callback.onResponse(null, Response.success(response.body().getMeals()));
                    } else {
                        callback.onFailure(null, new Throwable("Failed to load countries"));
                    }
                }

                @Override
                public void onFailure(Call<Countries> call, Throwable t) {
                    callback.onFailure(null, t);
                }
            });
        }
    }

    //=================================================================================================
    // Search Meals

    @Override
    public Call<MealList> searchMeals(String query) {
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return RetrofitInstance.getApi().getSearchMeal(query);
    }

    @Override
    public Call<MealList> searchMealsByCountry(String country) {
        try {
            country = URLEncoder.encode(country, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return RetrofitInstance.getApi().getMealsByCountry(country);
    }

    @Override
    public Call<MealList> searchMealsByIngredient(String ingredient) {
        try {
            ingredient = URLEncoder.encode(ingredient, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return RetrofitInstance.getApi().getMealsByIngredient(ingredient);
    }

    @Override
    public Call<MealList> searchMealsByCategory(String category) {
        try {
            category = URLEncoder.encode(category, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return RetrofitInstance.getApi().searchMealsByCategory(category);
    }


    private List<MainMeal> loadCountriesFromCache() {
        String countriesJson = sharedPreferences.getString("countries", null);
        if (countriesJson != null) {
            Countries countries = new Gson().fromJson(countriesJson, Countries.class);
            return countries.getMeals();
        }
        return null;
    }

    private void saveCountriesToCache(Countries countries) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("countries", new Gson().toJson(countries));
        editor.apply();
    }


    private void savePopularMealsToCache(List<MainMeal> mealList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("popularMeals", new Gson().toJson(mealList));
        editor.apply();
    }


    //=================================================================================================
    // Favorite Meals

    public LiveData<List<MealEntity>> getFavoriteMeals() {
        return mealDao.getAllMeals();
    }


    //========================================
    // Meal Plan
    @Override
    public void getMealsForDay(String day, MealPlanCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MealPlanEntity> mealsForDay = appDatabase.mealPlanDao().getMealsForDay(day);
            callback.onMealPlansLoaded(mealsForDay);
        });
    }

    @Override
    public void getMealsForMonth(int month, MealPlanCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MealPlanEntity> mealsForMonth = appDatabase.mealPlanDao().getMealsForMonth(month);
            callback.onMealPlansLoaded(mealsForMonth);
        });
    }

    @Override
    public void getMealsForYear(int year, MealPlanCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MealPlanEntity> mealsForYear = appDatabase.mealPlanDao().getMealsForYear(year);
            callback.onMealPlansLoaded(mealsForYear);
        });
    }

    @Override
    public void getMealsForDayNumber(int dayNumber, MealPlanCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MealPlanEntity> mealsForDayNumber = appDatabase.mealPlanDao().getMealsForDayNumber(dayNumber);
            callback.onMealPlansLoaded(mealsForDayNumber);
        });
    }

    @Override
    public void addMealPlanUpdateListener(MealPlanUpdateListener listener) {
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid())
                    .collection("mealPlans")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            listener.onError(e);
                            return;
                        }
                        if (snapshots != null) {
                            List<MealPlanEntity> mealPlans = new ArrayList<>();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                MealPlanEntity mealPlan = document.toObject(MealPlanEntity.class);
                                if (mealPlan != null) {
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        int count = appDatabase.mealPlanDao().isMealInMealPlan(mealPlan.getStrMeal(), mealPlan.getDayName());
                                        if (count == 0) {
                                            appDatabase.mealPlanDao().insert(mealPlan);
                                        }
                                    });
                                    mealPlans.add(mealPlan);
                                }
                            }
                            listener.onMealPlansUpdated(mealPlans);
                        } else {
                            listener.onMealPlansUpdated(new ArrayList<>());
                        }
                    });
        }
    }


    //==============================================================================================
    //Welcome Screen.
    @Override
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);

    }

    @Override
    public void setGuestMode(boolean isGuest) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isGuest", isGuest);
        editor.apply();
    }

    //================================================================================================
    // profile

    @Override
    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    @Override
    public void signOut(AuthService.AuthCallback callback) {
        if (firebaseAuth != null) {
            firebaseAuth.signOut();
            if (firebaseAuth.getCurrentUser() == null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                clearLocalFavorites();

                callback.onSuccess(null);
            } else {
                callback.onFailure("Failed to sign out.");
            }
        } else {
            callback.onFailure("FirebaseAuth not initialized.");
        }
    }


    @Override
    public void updateLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        android.content.res.Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        context.createConfigurationContext(config);
    }

    @Override
    public void clearLocalFavorites() {
        Executors.newSingleThreadExecutor().execute(() -> {
            appDatabase.mealDao().deleteAllFavorites();
            appDatabase.mealPlanDao().deleteAllMealPlan();
        });
    }

    @Override
    public void saveSelectedLanguage(String languageCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedLanguage", languageCode);
        editor.apply();
    }

    @Override
    public String getSelectedLanguage() {
        return sharedPreferences.getString("selectedLanguage", "en");
    }

    @Override
    public void getMealsByCategory(String categoryName, Callback<CatygoryByMeal> callback) {
        RetrofitInstance.getApi().getMealByCategory(categoryName).enqueue(callback);
    }

    @Override
    public void resetPassword(String email, AuthService.AuthCallback callback) {
        authService.forgetPassword(email, callback);
    }


    @Override
    public void login(String email, String password, AuthService.AuthCallback callback) {
        authService.login(email, password, callback);
    }

    @Override
    public void signInWithGoogle(Activity activity, AuthService.AuthCallback callback) {
        authService.signInWithGoogle(activity);
    }

    @Override
    public void handleGoogleSignInResult(Intent data, AuthService.AuthCallback callback) {
        authService.handleGoogleSignInResult(data, callback);
    }

    @Override
    public void signUp(String fullName, String email, String password, AuthService.AuthCallback callback) {
        authService.signUp(fullName, email, password, callback);
    }


}
