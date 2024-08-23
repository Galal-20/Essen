package com.example.essen.Fragments.MealPlanFragment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.essen.R;
import com.example.essen.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<String> daysOfWeek;
    private int selectedPosition = -1;
    private OnDayClickListener onDayClickListener;
    private AppDatabase appDatabase;
    private ExecutorService executorService;


    public CalendarAdapter(List<String> daysOfWeek, OnDayClickListener onDayClickListener,
                           AppDatabase appDatabase, ExecutorService executorService) {
        this.daysOfWeek = daysOfWeek;
        this.onDayClickListener = onDayClickListener;
        this.appDatabase = appDatabase;
        this.executorService = executorService;
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        holder.bind(daysOfWeek.get(position), position);
    }

    @Override
    public int getItemCount() {
        return daysOfWeek.size();
    }

    public interface OnDayClickListener {
        void onDayClick(String day);
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        private TextView dayText;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.calendar_day_text);
        }

        public void bind(String day, int position) {
            dayText.setText(day);
            itemView.setOnClickListener(v -> {
                selectedPosition = position;
                notifyDataSetChanged();
                if (onDayClickListener != null) {
                    onDayClickListener.onDayClick(day);
                }
            });

            /*itemView.setOnLongClickListener(v -> {
                if (appDatabase != null) {
                    // Delete meals for the selected day
                    executorService.execute(() -> {
                        appDatabase.mealPlanDao().deleteMealFromDay(day);
                        itemView.post(() -> {
                            if (onDayClickListener != null) {
                                onDayClickListener.onDayClick(day);
                            }
                        });
                    });
                }
                return true;
            });*/

            if (position == selectedPosition) {
                itemView.setBackgroundColor(Color.GREEN);
            } else {
                itemView.setBackgroundColor(Color.WHITE);
            }
        }
    }
}



