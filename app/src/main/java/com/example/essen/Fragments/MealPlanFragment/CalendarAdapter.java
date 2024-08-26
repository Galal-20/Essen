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
    static int selectedPosition = -1;
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

    public void updateData(List<String> newDaysOfWeek) {
        this.daysOfWeek = newDaysOfWeek;
        notifyDataSetChanged();
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
        void onDayClick(String item);
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        private TextView dayTextView;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.calendar_day_text);
        }

        public void bind(String day, int position) {
            dayTextView.setText(day);

            if (selectedPosition == position) {
                itemView.setBackgroundColor(Color.GREEN);
            } else {
                itemView.setBackgroundColor(Color.WHITE);
            }

            itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = getAdapterPosition();

                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

                if (onDayClickListener != null) {
                    onDayClickListener.onDayClick(day);
                }
            });
        }
    }
}




