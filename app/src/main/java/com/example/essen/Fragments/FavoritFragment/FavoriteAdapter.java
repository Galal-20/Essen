package com.example.essen.Fragments.FavoritFragment;

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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private static final int REQUEST_READ_CALENDAR_PERMISSION = 100;

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
        private final ImageView deleteButton;
        private final Button addToCalendarButton;

        public MealViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textFavoriteName);
            imageView = itemView.findViewById(R.id.imageFavorite);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            addToCalendarButton = itemView.findViewById(R.id.addToCalendarButton);

            addToCalendarButton.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{android.Manifest.permission.READ_CALENDAR},
                            REQUEST_READ_CALENDAR_PERMISSION);
                } else {
                    findCalendarEvents(nameTextView.getText().toString());
                }
            });
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

            addToCalendarButton.setOnClickListener(v -> {
                addMealToCalendar(meal);
            });
        }

        private void addMealToCalendar(MealEntity meal) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.TITLE, meal.getStrMeal());
            intent.putExtra(CalendarContract.Events.DESCRIPTION, meal.getStrInstructions());
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, meal.getStrArea());
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
    }
}
