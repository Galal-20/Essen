package com.example.essen.Fragments.MealPlanFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essen.R;
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealPlanEntity;

import java.util.List;

public class MealPlanFragment extends Fragment {

    private RecyclerView mealPlanRecyclerView;
    private MealPlanAsdapter mealPlanAdapter;
    private AppDatabase appDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        mealPlanRecyclerView = view.findViewById(R.id.food_recycler_view);
        mealPlanRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        appDatabase = AppDatabase.getDatabase(getContext());

        loadMealPlans();

        return view;
    }

    private void loadMealPlans() {
        new Thread(() -> {
            List<MealPlanEntity> mealPlans = appDatabase.mealPlanDao().getAllMealPlans();
            getActivity().runOnUiThread(() -> {
                if (mealPlanAdapter == null) {
                    mealPlanAdapter = new MealPlanAsdapter(mealPlans, appDatabase, getContext());
                    mealPlanRecyclerView.setAdapter(mealPlanAdapter);
                } else {
                    mealPlanAdapter.notifyDataSetChanged(); // Refresh adapter data
                }
            });
        }).start();
    }
}




/* new Thread(() -> {
            List<MealPlanEntity> mealPlans = appDatabase.mealPlanDao().getAllMealPlans();

            getActivity().runOnUiThread(() -> {
                mealPlanAdapter = new MealPlanAsdapter(mealPlans , appDatabase);
                mealPlanRecyclerView.setAdapter(mealPlanAdapter);
            });
        }).start();*/