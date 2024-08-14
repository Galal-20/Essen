package com.example.essen.Fragments.CountriesFragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essen.Activities.MealCountry.MealCountryActivity;
import com.example.essen.R;
import com.example.essen.pojo.MainMeal;

import java.util.List;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ViewHolder> {

    private List<MainMeal> countries;
    private Context context;

    public CountriesAdapter(Context context, List<MainMeal> countries) {
        this.context = context;
        this.countries = countries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainMeal country = countries.get(position);
        holder.textView.setText(country.getStrArea());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealCountryActivity.class);
            intent.putExtra("country_name", country.getStrArea());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_title);
        }
    }
}
