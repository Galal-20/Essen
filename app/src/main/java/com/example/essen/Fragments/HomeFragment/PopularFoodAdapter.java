package com.example.essen.Fragments.HomeFragment;


import static com.example.essen.Fragments.HomeFragment.HomeFragment.INGREDIENTS;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essen.Activities.MealActivity.MealActivity;
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
                // .placeholder(R.drawable.breakfast)
                // .error(R.drawable.breakfast)
                .into(holder.imagePopular);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealActivity.class);
            intent.putExtra(HomeFragment.NAME_MEAL, meal.getStrMeal());
            intent.putExtra(HomeFragment.THUMB_MEAL, meal.getStrMealThumb());
            intent.putExtra(HomeFragment.Cat, meal.getStrCategory());
            intent.putExtra(HomeFragment.LOCATION, meal.getStrArea());
            intent.putExtra(HomeFragment.INSTRUCTIONS, meal.getStrInstructions());
            intent.putExtra(HomeFragment.YOUTUBE, meal.getStrYoutube());
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
