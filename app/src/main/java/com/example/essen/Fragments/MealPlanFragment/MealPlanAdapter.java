package com.example.essen.Fragments.MealPlanFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
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

    @Override
    public void onBindViewHolder(@NonNull MealPlanViewHolder holder, int position) {
        MealPlanEntity mealPlan = mealPlans.get(position);

        Glide.with(holder.itemView.getContext()).load(mealPlan.getStrMealThumb()).into(holder.mealImageView);
        holder.mealNameTextView.setText(mealPlan.getStrMeal());
        holder.dateTextView.setText(mealPlan.getDayName() + ", " + mealPlan.getDayNumber() + " " + mealPlan.getMonthName());
        holder.mealTypeTextView.setText(mealPlan.getMealType());

        holder.deleteButton.setOnClickListener(v -> showDeleteDialog(mealPlan, position));
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

    private void showDeleteDialog(MealPlanEntity mealPlan, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Meal Plan")
                .setMessage("Are you sure you want to delete this meal plan?")
                .setPositiveButton("Yes", (dialog, which) -> deleteMealPlan(mealPlan, position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteMealPlan(MealPlanEntity mealPlan, int position) {
        // Remove from local database
        new Thread(() -> {
            appDatabase.mealPlanDao().delete(mealPlan);
            ((Activity) context).runOnUiThread(() -> {
                mealPlans.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mealPlans.size());
            });
        }).start();

        // Remove from Firestore if user is logged in
        if (currentUser != null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("users").document(currentUser.getUid())
                    .collection("mealPlans").document(mealPlan.getFirestoreId()) // Ensure you have a Firestore ID for deletion
                    .delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Meal plan deleted from Firestore", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Error deleting meal plan", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
        }
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
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, mealPlan.getStrArea()); // Set area as location
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

    @SuppressLint("QueryPermissionsNeeded")
    private void findCalendarEvents(String mealName) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        String[] projection = new String[]{
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DESCRIPTION
        };

        String selection = "(" + CalendarContract.Events.TITLE + " LIKE ?)";
        String[] selectionArgs = new String[]{"%" + mealName + "%"};

        Cursor cursor = cr.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder events = new StringBuilder();
            do {
                long eventID = cursor.getLong(0);
                String title = cursor.getString(1);
                long startTime = cursor.getLong(2);
                long endTime = cursor.getLong(3);
                String location = cursor.getString(4);
                String description = cursor.getString(5);

                events.append("Event ID: ").append(eventID)
                        .append("\nTitle: ").append(title)
                        .append("\nStart Time: ").append(startTime)
                        .append("\nEnd Time: ").append(endTime)
                        .append("\nLocation: ").append(location)
                        .append("\nDescription: ").append(description)
                        .append("\n\n");

            } while (cursor.moveToNext());
            cursor.close();

            Toast.makeText(context, events.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "No events found for " + mealName, Toast.LENGTH_SHORT).show();
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


/* holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MealActivity.class);
            intent.putExtra("MEAL_ID", mealPlan.getId());
            v.getContext().startActivity(intent);
        });*/