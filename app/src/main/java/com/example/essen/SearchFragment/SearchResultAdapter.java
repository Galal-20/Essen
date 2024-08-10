package com.example.essen.SearchFragment;

import static com.example.essen.HomeFragment.HomeFragment.Cat;
import static com.example.essen.HomeFragment.HomeFragment.INSTRUCTIONS;
import static com.example.essen.HomeFragment.HomeFragment.LOCATION;
import static com.example.essen.HomeFragment.HomeFragment.NAME_MEAL;
import static com.example.essen.HomeFragment.HomeFragment.THUMB_MEAL;
import static com.example.essen.HomeFragment.HomeFragment.YOUTUBE;

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
import com.example.essen.MealActivity.MealActivity;
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
                    intent.putExtra(Cat, meal.getStrCategory());
                    intent.putExtra(NAME_MEAL, meal.getStrMeal());
                    intent.putExtra(THUMB_MEAL, meal.getStrMealThumb());
                    intent.putExtra(LOCATION, meal.getStrArea());
                    intent.putExtra(INSTRUCTIONS, meal.getStrInstructions());
                    intent.putExtra(YOUTUBE, meal.getStrYoutube());
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
