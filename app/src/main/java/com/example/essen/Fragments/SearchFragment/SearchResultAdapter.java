package com.example.essen.Fragments.SearchFragment;

import static com.example.essen.Fragments.HomeFragment.HomeFragment.INGREDIENTS;

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

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.MealViewHolder> {
    private final Context context;
    private final List<MainMeal> mealList = new ArrayList<>();

    public SearchResultAdapter(Context context) {
        this.context = context;
    }

    public void setMealList(List<MainMeal> meals) {
        if (meals == null) {
            meals = new ArrayList<>();
        }
        this.mealList.clear();
        this.mealList.addAll(meals);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MainMeal current = mealList.get(position);
        holder.bind(current);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final ImageView imageView;

        public MealViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textSearchResultName);
            imageView = itemView.findViewById(R.id.imageSearchResult);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MainMeal meal = mealList.get(position);
                    Intent intent = new Intent(context, MealActivity.class);
                    intent.putExtra(HomeFragment.Cat, meal.getStrCategory());
                    intent.putExtra(HomeFragment.NAME_MEAL, meal.getStrMeal());
                    intent.putExtra(HomeFragment.THUMB_MEAL, meal.getStrMealThumb());
                    intent.putExtra(HomeFragment.LOCATION, meal.getStrArea());
                    intent.putExtra(HomeFragment.INSTRUCTIONS, meal.getStrInstructions());
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
                }
            });
        }

        public void bind(MainMeal meal) {
            nameTextView.setText(meal.getStrMeal());
            Glide.with(context).load(meal.getStrMealThumb()).into(imageView);
        }
    }
}
