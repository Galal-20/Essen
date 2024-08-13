// MealAdapter.java
package com.example.essen.Activities.MealCountry;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essen.Activities.MealActivity.MealActivity;
import com.example.essen.Fragments.HomeFragment.HomeFragment;
import com.example.essen.R;
import com.example.essen.pojo.MainMeal;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private List<MainMeal> meals;
    private Context context;

    public MealAdapter(Context context, List<MainMeal> meals) {
        this.context = context;
        this.meals = meals;
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
        holder.mealName.setText(meal.getStrMeal());

        Glide.with(holder.imageView.getContext())
                .load(meal.getStrMealThumb())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealActivity.class);
            intent.putExtra(HomeFragment.Cat, meal.getStrCategory());
            intent.putExtra(HomeFragment.NAME_MEAL, meal.getStrMeal());
            intent.putExtra(HomeFragment.THUMB_MEAL, meal.getStrMealThumb());
            intent.putExtra(HomeFragment.LOCATION, meal.getStrArea());
            intent.putExtra(HomeFragment.INSTRUCTIONS, meal.getStrInstructions());
            intent.putExtra(HomeFragment.INGREDIENTS,
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
        });
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    public void updateMeals(List<MainMeal> meals) {
        this.meals = meals;
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
