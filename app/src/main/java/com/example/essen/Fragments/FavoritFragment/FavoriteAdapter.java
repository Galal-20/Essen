package com.example.essen.Fragments.FavoritFragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essen.Activities.MealActivity.MealActivity;
import com.example.essen.Fragments.HomeFragment.HomeFragment;
import com.example.essen.R;
import com.example.essen.room.MealEntity;


public class FavoriteAdapter extends ListAdapter<MealEntity, FavoriteAdapter.MealViewHolder> {
    private static final DiffUtil.ItemCallback<MealEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<MealEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull MealEntity oldItem, @NonNull MealEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull MealEntity oldItem, @NonNull MealEntity newItem) {
            return oldItem.equals(newItem);
        }
    };
    private final Context context;
    private final OnDeleteClickListener onDeleteClickListener;

    public FavoriteAdapter(Context context, OnDeleteClickListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.onDeleteClickListener = listener;

    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealEntity current = getItem(position);
        holder.bind(current);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String mealName);
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final ImageView imageView;
        private final Button deleteButton;

        public MealViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textFavoriteName);
            imageView = itemView.findViewById(R.id.imageFavorite);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }

        public void bind(MealEntity meal) {
            nameTextView.setText(meal.getStrMeal());
            Glide.with(context).load(meal.getStrMealThumb()).into(imageView);
            itemView.setOnClickListener(v -> {
                Toast.makeText(context, "Clicked on " + meal.getStrMeal(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MealActivity.class);
                intent.putExtra(HomeFragment.Cat, meal.getStrCategory());
                intent.putExtra(HomeFragment.NAME_MEAL, meal.getStrMeal());
                intent.putExtra(HomeFragment.THUMB_MEAL, meal.getStrMealThumb());
                intent.putExtra(HomeFragment.LOCATION, meal.getStrArea());
                intent.putExtra(HomeFragment.INSTRUCTIONS, meal.getStrInstructions());
                intent.putExtra(HomeFragment.YOUTUBE, meal.getStrYoutube());
                intent.putExtra(HomeFragment.INGREDIENTS, meal.getIngredients());
                context.startActivity(intent);
            });

            itemView.setOnLongClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(meal.getStrMeal());
                }
                return true;
            });


            deleteButton.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(meal.getStrMeal());
                }
            });
        }
    }
}
