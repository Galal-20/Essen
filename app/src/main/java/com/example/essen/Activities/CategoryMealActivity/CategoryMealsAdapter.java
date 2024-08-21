package com.example.essen.Activities.CategoryMealActivity;

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
import com.example.essen.R;
import com.example.essen.pojo.MealX;

import java.util.ArrayList;
import java.util.List;

public class CategoryMealsAdapter extends RecyclerView.Adapter<CategoryMealsAdapter.CategoryMealsViewHolder> {
    private ArrayList<MealX> mealList = new ArrayList<>();
    private Context context;

    public CategoryMealsAdapter(Context context) {
        this.context = context;
    }

    public void setMealsList(List<MealX> mealList) {
        this.mealList = (ArrayList<MealX>) mealList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryMealsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item, parent,
                false);
        return new CategoryMealsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryMealsViewHolder holder, int position) {
        MealX meal = mealList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.bg1)
                .error(R.drawable.bg1)
                .into(holder.imageMeal);
        holder.tvMeal.setText(meal.getStrMeal());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealActivity.class);
            intent.putExtra("MEAL_ID", meal.getidMeal());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class CategoryMealsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMeal;
        TextView tvMeal;

        public CategoryMealsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMeal = itemView.findViewById(R.id.image_meal);
            tvMeal = itemView.findViewById(R.id.tv_meal);


        }
    }
}

