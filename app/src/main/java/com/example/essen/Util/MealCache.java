package com.example.essen.Util;


import com.example.essen.pojo.CategoryList;
import com.example.essen.pojo.MealList;

import java.util.List;

public interface MealCache {
    MealList getRandomMeal();

    void saveRandomMeal(MealList meal);

    List<MealList> getPopularMeals();

    void savePopularMeals(List<MealList> meals);

    CategoryList getCategories();

    void saveCategories(CategoryList categories);
}

