package com.example.essen.Activities.MealCountry;

import android.content.Context;
import android.content.Intent;
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
    private final Context context;
    private final CountryContract.Presenter presenter;

    public MealAdapter(Context context, List<MainMeal> meals, CountryContract.Presenter presenter) {
        this.context = context;
        this.meals = meals != null ? meals : new ArrayList<>();
        this.presenter = presenter;
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
                    intent.putExtra("MEAL_ID", selectedMeal.getidMeal());
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






