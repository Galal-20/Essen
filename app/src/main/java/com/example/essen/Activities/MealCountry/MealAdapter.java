package com.example.essen.Activities.MealCountry;

import static com.example.essen.Fragments.HomeFragment.HomeFragment.Cat;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.INGREDIENTS;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.INSTRUCTIONS;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.LOCATION;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.NAME_MEAL;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.THUMB_MEAL;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.YOUTUBE;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essen.Activities.MealActivity.MealActivity;
import com.example.essen.R;
import com.example.essen.pojo.MainMeal;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private List<MainMeal> meals;
    private Context context;

    public MealAdapter(Context context, List<MainMeal> meals) {
        this.context = context;
        this.meals = meals != null ? meals : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainMeal meal = meals.get(position);

        if (meal != null) {
            Log.d("MealAdapter", "Meal: " + (meal.getStrMeal() != null ? meal.getStrMeal() : "No Name"));
            Log.d("MealAdapter", "Category: " + (meal.getStrCategory() != null ? meal.getStrCategory() : "No Category"));
            Log.d("MealAdapter", "Thumb: " + (meal.getStrMealThumb() != null ? meal.getStrMealThumb() : "No Thumbnail"));
            Log.d("MealAdapter", "Area: " + (meal.getStrArea() != null ? meal.getStrArea() : "No Area"));
            Log.d("MealAdapter", "Instructions: " + (meal.getStrInstructions() != null ? meal.getStrInstructions() : "No Instructions"));
            Log.d("MealAdapter", "Ingredients: " + (meal.getStrIngredient1() != null ? meal.getStrIngredient1() : "No Ingredients"));

            holder.mealName.setText(meal.getStrMeal() != null ? meal.getStrMeal() : "No Name");

            Glide.with(holder.imageView.getContext())
                    .load(meal.getStrMealThumb() != null ? meal.getStrMealThumb() : R.drawable.coffe)
                    .placeholder(R.drawable.coffe)
                    .error(R.drawable.coffe)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(v -> {
                int holderAdapterPosition = holder.getAdapterPosition();
                if (holderAdapterPosition != RecyclerView.NO_POSITION) {
                    MainMeal selectedMeal = meals.get(holderAdapterPosition);
                    Intent intent = new Intent(context, MealActivity.class);
                    intent.putExtra(Cat, selectedMeal.getStrCategory() != null ? selectedMeal.getStrCategory() : "No Category");
                    intent.putExtra(NAME_MEAL, selectedMeal.getStrMeal() != null ? selectedMeal.getStrMeal() : "No Name");
                    intent.putExtra(THUMB_MEAL, selectedMeal.getStrMealThumb() != null ? selectedMeal.getStrMealThumb() : "No Thumbnail");
                    intent.putExtra(LOCATION, selectedMeal.getStrArea() != null ? selectedMeal.getStrArea() : "No Area");
                    intent.putExtra(INSTRUCTIONS, selectedMeal.getStrInstructions() != null ? selectedMeal.getStrInstructions() : "No Instructions");

                    StringBuilder ingredients = new StringBuilder();
                    if (selectedMeal.getStrIngredient1() != null)
                        ingredients.append(selectedMeal.getStrIngredient1()).append("\n");
                    if (selectedMeal.getStrIngredient2() != null)
                        ingredients.append(selectedMeal.getStrIngredient2()).append("\n");

                    intent.putExtra(INGREDIENTS, ingredients.length() > 0 ? ingredients.toString() : "No Ingredients");

                    intent.putExtra(YOUTUBE, selectedMeal.getStrYoutube() != null ? selectedMeal.getStrYoutube() : "No Youtube");
                    context.startActivity(intent);
                }
            });
        } else {
            Toast.makeText(context, "Meal data not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    public void updateMeals(List<MainMeal> meals) {
        this.meals = meals != null ? meals : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mealName;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.meal_name);
            imageView = itemView.findViewById(R.id.image_meal_c);
        }
    }
}



 /* Intent intent = new Intent(context, MealActivity.class);
                intent.putExtra(HomeFragment.Cat, meal.getStrCategory() != null ? meal.getStrCategory() : "No Category");
                intent.putExtra(HomeFragment.NAME_MEAL, meal.getStrMeal() != null ? meal.getStrMeal() : "No " + "Name");
                intent.putExtra(HomeFragment.THUMB_MEAL, meal.getStrMealThumb() != null ? meal.getStrMealThumb() : "No Thumb");
                intent.putExtra(HomeFragment.LOCATION, meal.getStrArea() != null ? meal.getStrArea() : "No " + "Location");
                intent.putExtra(HomeFragment.INSTRUCTIONS, meal.getStrInstructions() != null ? meal.getStrInstructions() : "No Instructions");
                intent.putExtra(HomeFragment.INGREDIENTS, meal.getStrIngredient1() != null ? meal.getStrIngredient1() : "No Ingredients" + "\n" +
                                meal.getStrIngredient2() +
                                meal.getStrIngredient3() + "\n" + meal.getStrIngredient4() +
                                meal.getStrIngredient5() + "\n" + meal.getStrIngredient6() +
                                meal.getStrIngredient7() + "\n" + meal.getStrIngredient8() +
                                meal.getStrIngredient9() + "\n" + meal.getStrIngredient10() +
                                meal.getStrIngredient11() + "\n" + meal.getStrIngredient12() +
                                meal.getStrIngredient13() + "\n" + meal.getStrIngredient14() +
                                meal.getStrIngredient15() + "\n" + meal.getStrIngredient16() +
                                meal.getStrIngredient17() + "\n" + meal.getStrIngredient18() +
                                meal.getStrIngredient19() + "\n" + meal.getStrIngredient20()
                );
                intent.putExtra(HomeFragment.YOUTUBE, meal.getStrYoutube() != null ? meal.getStrYoutube() : "No Youtube");
                context.startActivity(intent);*/

/* MainMeal meal = meals.get(position);
        holder.mealName.setText(meal.getStrMeal());

        Glide.with(holder.imageView.getContext())
                .load(meal.getStrMealThumb())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (mealselected != null){
                Intent intent = new Intent(context, MealActivity.class);
                intent.putExtra(Cat, meal.getStrCategory());
                intent.putExtra(NAME_MEAL, meal.getStrMeal());
                intent.putExtra(THUMB_MEAL, meal.getStrMealThumb());
                intent.putExtra(LOCATION, meal.getStrArea());
                intent.putExtra(INSTRUCTIONS, meal.getStrInstructions());
                intent.putExtra(INGREDIENTS,
                        meal.getStrIngredient1() + "\n" + meal.getStrIngredient2() +
                                meal.getStrIngredient3() + "\n" + meal.getStrIngredient4() +
                                meal.getStrIngredient5() + "\n" + meal.getStrIngredient6() +
                                meal.getStrIngredient7() + "\n" + meal.getStrIngredient8() +
                                meal.getStrIngredient9() + "\n" + meal.getStrIngredient10() +
                                meal.getStrIngredient11() + "\n" + meal.getStrIngredient12() +
                                meal.getStrIngredient13() + "\n" + meal.getStrIngredient14() +
                                meal.getStrIngredient15() + "\n" + meal.getStrIngredient16() +
                                meal.getStrIngredient17() + "\n" + meal.getStrIngredient18() +
                                meal.getStrIngredient19() + "\n" + meal.getStrIngredient20()
                );
                intent.putExtra(HomeFragment.YOUTUBE, meal.getStrYoutube());
                context.startActivity(intent);
            }else {
                Toast.makeText(context, "Meal data not available", Toast.LENGTH_SHORT).show();
            }

        });*/



