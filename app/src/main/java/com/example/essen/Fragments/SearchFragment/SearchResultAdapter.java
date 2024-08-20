package com.example.essen.Fragments.SearchFragment;

import static com.example.essen.Fragments.HomeFragment.HomeFragment.Cat;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.INGREDIENTS;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.INSTRUCTIONS;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.LOCATION;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.NAME_MEAL;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.THUMB_MEAL;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.YOUTUBE;

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
import com.example.essen.pojo.AllDetailsMeal;
import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealId;
import com.example.essen.retrofit.MealAPI;
import com.example.essen.retrofit.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.MealViewHolder> {
    private final Context context;
    private final List<MainMeal> mealList = new ArrayList<>();
    private final MealAPI mealAPI;

    public SearchResultAdapter(Context context) {
        this.context = context;
        this.mealAPI = RetrofitInstance.getApi();
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
                    mealAPI.getMealDetails(meal.getidMeal()).enqueue(new Callback<MealId>() {
                        @Override
                        public void onResponse(Call<MealId> call, Response<MealId> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                AllDetailsMeal detailedMeal = response.body().getMeals().get(0);

                                Intent intent = new Intent(context, MealActivity.class);
                                intent.putExtra(Cat, detailedMeal.getStrCategory());
                                intent.putExtra(NAME_MEAL, detailedMeal.getStrMeal());
                                intent.putExtra(THUMB_MEAL, detailedMeal.getStrMealThumb());
                                intent.putExtra(LOCATION, detailedMeal.getStrArea());
                                intent.putExtra(INSTRUCTIONS, detailedMeal.getStrInstructions());
                                intent.putExtra(INGREDIENTS,
                                        detailedMeal.getStrIngredient1() + "\n" + detailedMeal.getStrIngredient2() +
                                                detailedMeal.getStrIngredient3() + "\n" + detailedMeal.getStrIngredient4() +
                                                detailedMeal.getStrIngredient5() + "\n" + detailedMeal.getStrIngredient6() +
                                                detailedMeal.getStrIngredient7() + "\n" + detailedMeal.getStrIngredient8() +
                                                detailedMeal.getStrIngredient9() + "\n" + detailedMeal.getStrIngredient10() +
                                                detailedMeal.getStrIngredient11() + "\n" + detailedMeal.getStrIngredient12() +
                                                detailedMeal.getStrIngredient13() + "\n" + detailedMeal.getStrIngredient14() +
                                                detailedMeal.getStrIngredient15() + "\n"
                                );
                                intent.putExtra(YOUTUBE, detailedMeal.getStrYoutube());
                                context.startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<MealId> call, Throwable t) {

                        }
                    });
                }
            });
        }

        public void bind(MainMeal meal) {
            nameTextView.setText(meal.getStrMeal());
            Glide.with(context).load(meal.getStrMealThumb()).into(imageView);
        }
    }
}


