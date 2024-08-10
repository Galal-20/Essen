package com.example.essen.HomeFragment;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essen.MealActivity.MealActivity;
import com.example.essen.R;
import com.example.essen.pojo.MainMeal;

import java.util.List;

public class PopularFoodAdapter extends RecyclerView.Adapter<PopularFoodAdapter.PopularFoodViewHolder> {

    private final Context context;
    private List<MainMeal> popularMeals;

    public PopularFoodAdapter(Context context, List<MainMeal> popularMeals) {
        this.context = context;
        this.popularMeals = popularMeals;
    }

    public void setMealsList(List<MainMeal> popularMeals) {
        this.popularMeals = popularMeals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popular_item, parent, false);
        return new PopularFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularFoodViewHolder holder, int position) {
        MainMeal meal = popularMeals.get(position);
        Glide.with(context)
                .load(meal.getStrMealThumb())
                .into(holder.imagePopular);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealActivity.class);
            intent.putExtra(HomeFragment.NAME_MEAL, meal.getStrMeal());
            intent.putExtra(HomeFragment.THUMB_MEAL, meal.getStrMealThumb());
            intent.putExtra(HomeFragment.Cat, meal.getStrCategory());
            intent.putExtra(HomeFragment.LOCATION, meal.getStrArea());
            intent.putExtra(HomeFragment.INSTRUCTIONS, meal.getStrInstructions());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return popularMeals.size();
    }


    public static class PopularFoodViewHolder extends RecyclerView.ViewHolder {

        ImageView imagePopular;

        public PopularFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePopular = itemView.findViewById(R.id.imagePopular);
        }
    }
}
