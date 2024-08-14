package com.example.essen.Fragments.MealPlanFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essen.Activities.MealActivity.MealActivity;
import com.example.essen.Fragments.HomeFragment.HomeFragment;
import com.example.essen.R;
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealPlanEntity;

import java.util.List;

public class MealPlanAsdapter extends RecyclerView.Adapter<MealPlanAsdapter.MealPlanViewHolder> {

    private final Context context;
    private List<MealPlanEntity> mealPlans;
    private AppDatabase appDatabase;


    public MealPlanAsdapter(List<MealPlanEntity> mealPlans, AppDatabase appDatabase, Context context) {
        this.mealPlans = mealPlans;
        this.appDatabase = appDatabase;
        this.context = context;
    }

    @Override
    public MealPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_plan_item, parent, false);
        return new MealPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MealPlanViewHolder holder, int position) {
        MealPlanEntity mealPlan = mealPlans.get(position);

        Glide.with(holder.itemView.getContext()).load(mealPlan.getStrMealThumb()).into(holder.mealImageView);
        holder.mealNameTextView.setText(mealPlan.getStrMeal());
        holder.dateTextView.setText(mealPlan.getDayName() + ", " + mealPlan.getDayNumber() + " " + mealPlan.getMonthName());
        holder.mealTypeTextView.setText(mealPlan.getMealType());

        holder.deleteButton.setOnClickListener(v -> {
            new Thread(() -> {
                appDatabase.mealPlanDao().delete(mealPlan);
                ((Activity) holder.itemView.getContext()).runOnUiThread(() -> {
                    mealPlans.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mealPlans.size());
                });
            }).start();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealActivity.class);
            intent.putExtra(HomeFragment.Cat, mealPlan.getStrCategory());
            intent.putExtra(HomeFragment.NAME_MEAL, mealPlan.getStrMeal());
            intent.putExtra(HomeFragment.THUMB_MEAL, mealPlan.getStrMealThumb());
            intent.putExtra(HomeFragment.LOCATION, mealPlan.getStrArea());
            intent.putExtra(HomeFragment.INSTRUCTIONS, mealPlan.getStrInstructions());
            intent.putExtra(HomeFragment.YOUTUBE, mealPlan.getStrYoutube());
            intent.putExtra(HomeFragment.INGREDIENTS, mealPlan.getIngredients());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mealPlans.size();
    }

    static class MealPlanViewHolder extends RecyclerView.ViewHolder {

        TextView mealNameTextView;
        TextView dateTextView;
        TextView mealTypeTextView;
        ImageView mealImageView;
        Button deleteButton;

        public MealPlanViewHolder(View itemView) {
            super(itemView);
            mealNameTextView = itemView.findViewById(R.id.text_meal_plan);
            dateTextView = itemView.findViewById(R.id.text_date_picker);
            mealTypeTextView = itemView.findViewById(R.id.dinner_lanuch_breakfast);
            mealImageView = itemView.findViewById(R.id.image_meal_plan);
            deleteButton = itemView.findViewById(R.id.deleteButton_plan);
        }
    }
}



/* holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MealActivity.class);
            intent.putExtra("MEAL_ID", mealPlan.getId());
            v.getContext().startActivity(intent);
        });*/