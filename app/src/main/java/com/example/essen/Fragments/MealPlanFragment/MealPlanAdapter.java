package com.example.essen.Fragments.MealPlanFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essen.Activities.MealActivity.MealActivity;
import com.example.essen.Fragments.HomeFragment.HomeFragment;
import com.example.essen.R;
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealPlanEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.MealPlanViewHolder> {
    private static final int REQUEST_READ_CALENDAR_PERMISSION = 100;
    private final Context context;
    private List<MealPlanEntity> mealPlans;
    private AppDatabase appDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    public MealPlanAdapter(List<MealPlanEntity> mealPlans, AppDatabase appDatabase, Context context) {
        this.mealPlans = mealPlans;
        this.appDatabase = appDatabase;
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.currentUser = firebaseAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public MealPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_plan_item, parent, false);
        return new MealPlanViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull MealPlanViewHolder holder, int position) {
        MealPlanEntity mealPlan = mealPlans.get(position);

        Glide.with(holder.itemView.getContext()).load(mealPlan.getStrMealThumb()).into(holder.mealImageView);
        holder.mealNameTextView.setText(mealPlan.getStrMeal());
        holder.dateTextView.setText(mealPlan.getDayName() + ", " + mealPlan.getDayNumber() + " " + mealPlan.getMonthName());
        holder.mealTypeTextView.setText(mealPlan.getMealType());

        holder.deleteButton.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);
            Button yesButton = dialogView.findViewById(R.id.cancel_button);
            Button cancelButton = dialogView.findViewById(R.id.cancel_Ok);
            AlertDialog dialog = builder.create();
            yesButton.setOnClickListener(view -> {
                Completable.fromAction(() -> appDatabase.mealPlanDao().delete(mealPlan))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            mealPlans.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Meal plan deleted from Room", Toast.LENGTH_SHORT).show();
                            notifyItemRangeChanged(position, mealPlans.size());
                            if (currentUser != null) {
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                firestore.collection("users").document(currentUser.getUid())
                                        .collection("mealPlans").document(mealPlan.getStrMeal())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Meal plan deleted from Firestore", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> {
                                            Log.e("DeleteError", "Error deleting meal plan from Firestore: " + e.getMessage());
                                            Toast.makeText(context, "Error deleting meal plan from Firestore", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                            Log.e("DeleteError", "Error deleting meal plan: " + throwable.getMessage());
                            Toast.makeText(context, "Error deleting meal plan: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                dialog.dismiss();
            });

            cancelButton.setOnClickListener(view -> {
                dialog.dismiss();
                Toast.makeText(context, "Meal plan Not deleted from Firestore or Room", Toast.LENGTH_SHORT).show();
            });

            dialog.show();


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

        holder.addToCalendarButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.WRITE_CALENDAR},
                        REQUEST_READ_CALENDAR_PERMISSION);
            } else {
                addMealPlanToCalendar(mealPlan);
            }
        });
    }

    public void updateMealPlans(List<MealPlanEntity> newMealPlans) {
        this.mealPlans.clear();
        this.mealPlans.addAll(newMealPlans);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mealPlans.size();
    }

    private void addMealPlanToCalendar(MealPlanEntity mealPlan) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, mealPlan.getStrMeal());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, mealPlan.getStrInstructions());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, mealPlan.getStrArea());
        intent.putExtra(CalendarContract.Events.ALL_DAY, false);
        intent.putExtra(CalendarContract.Events.HAS_ALARM, true);

        intent.putExtra(CalendarContract.Reminders.MINUTES, 10);
        intent.putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "No Calendar app found!", Toast.LENGTH_SHORT).show();
        }
    }


    static class MealPlanViewHolder extends RecyclerView.ViewHolder {
        TextView mealNameTextView;
        TextView dateTextView;
        TextView mealTypeTextView;
        ImageView mealImageView;
        ImageView deleteButton;
        Button addToCalendarButton;

        public MealPlanViewHolder(View itemView) {
            super(itemView);
            mealNameTextView = itemView.findViewById(R.id.text_meal_plan);
            dateTextView = itemView.findViewById(R.id.text_date_picker);
            mealTypeTextView = itemView.findViewById(R.id.dinner_lanuch_breakfast);
            mealImageView = itemView.findViewById(R.id.image_meal_plan);
            deleteButton = itemView.findViewById(R.id.deleteButton_plan);
            addToCalendarButton = itemView.findViewById(R.id.addToCalendarButton);
        }
    }
}

