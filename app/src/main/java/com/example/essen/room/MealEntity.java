package com.example.essen.room;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_table")
public class MealEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;


    private String strMeal;
    private String strMealThumb;
    private String strCategory;
    private String strArea;
    private String strInstructions;
    private String ingredients;
    private String strYoutube;


    public int getId() {
        return id;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public String getStrArea() {
        return strArea;
    }

    public void setStrArea(String strArea) {
        this.strArea = strArea;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public void setStrInstructions(String strInstructions) {
        this.strInstructions = strInstructions;
    }

    public String getStrYoutube() {
        return strYoutube;
    }

    public void setStrYoutube(String strYoutube) {
        this.strYoutube = strYoutube;
    }
}

