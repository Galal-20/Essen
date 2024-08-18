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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MealPlanFragment extends Fragment {

    private RecyclerView mealPlanRecyclerView;
    private MealPlanAdapter mealPlanAdapter;
    private AppDatabase appDatabase;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        mealPlanRecyclerView = view.findViewById(R.id.food_recycler_view);
        mealPlanRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        appDatabase = AppDatabase.getDatabase(getContext());
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        loadMealPlans();

        return view;
    }

    private void loadMealPlans() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid())
                    .collection("mealPlans")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<MealPlanEntity> mealPlans = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                MealPlanEntity mealPlan = document.toObject(MealPlanEntity.class);
                                mealPlans.add(mealPlan);
                                // Optionally update local Room database
                                new Thread(() -> appDatabase.mealPlanDao().insert(mealPlan)).start();
                            }
                            if (mealPlanAdapter == null) {
                                mealPlanAdapter = new MealPlanAdapter(mealPlans, appDatabase, getContext());
                                mealPlanRecyclerView.setAdapter(mealPlanAdapter);
                            } else {
                                mealPlanAdapter.notifyDataSetChanged(); // Refresh adapter data
                            }
                        } else {
                            showMessage("Error fetching meal plans: " + task.getException().getMessage());
                        }
                    });
        } else {
            showMessage("User not logged in.");
        }
    }

    private void showMessage(String message) {
        Snackbar.make(mealPlanRecyclerView, message, Snackbar.LENGTH_SHORT).show();
    }
}



/* new Thread(() -> {
            List<MealPlanEntity> mealPlans = appDatabase.mealPlanDao().getAllMealPlans();

            getActivity().runOnUiThread(() -> {
                mealPlanAdapter = new MealPlanAsdapter(mealPlans , appDatabase);
                mealPlanRecyclerView.setAdapter(mealPlanAdapter);
            });
        }).start();*/