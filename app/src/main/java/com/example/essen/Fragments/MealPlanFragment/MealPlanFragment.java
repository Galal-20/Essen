package com.example.essen.Fragments.MealPlanFragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private FirebaseUser currentUse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        mealPlanRecyclerView = view.findViewById(R.id.food_recycler_view);
        mealPlanRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        appDatabase = AppDatabase.getDatabase(getContext());
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUse = firebaseAuth.getCurrentUser();
        listenForMealPlanUpdates();
        return view;
    }

    private void listenForMealPlanUpdates() {
        if (currentUse != null) {
            firestore.collection("users").document(currentUse.getUid())
                    .collection("mealPlans")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            showMessage("Error fetching updates: " + e.getMessage());
                            return;
                        }

                        if (snapshots != null) {
                            List<MealPlanEntity> mealPlans = new ArrayList<>();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                MealPlanEntity mealPlan = document.toObject(MealPlanEntity.class);

                                // Update Room database
                                new Thread(() -> {
                                    int count = appDatabase.mealPlanDao().isMealInMealPlan(mealPlan.getStrMeal());
                                    if (count == 0) {
                                        appDatabase.mealPlanDao().insert(mealPlan);
                                    }
                                }).start();

                                mealPlans.add(mealPlan);
                            }

                            // Update UI
                            if (mealPlanAdapter == null) {
                                mealPlanAdapter = new MealPlanAdapter(mealPlans, appDatabase, getContext());
                                mealPlanRecyclerView.setAdapter(mealPlanAdapter);
                            } else {
                                mealPlanAdapter.updateMealPlans(mealPlans);
                            }
                        } else {
                            loadMealPlansFromRoom();
                        }
                    });
        }
    }




    private void loadMealPlansFromRoom() {
        if (!isConnectedToInternet()) {
            new Thread(() -> {
                List<MealPlanEntity> mealPlans = appDatabase.mealPlanDao().getAllMealPlans();

                getActivity().runOnUiThread(() -> {
                    updateAdapter(mealPlans);
                });
            }).start();
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateAdapter(List<MealPlanEntity> mealPlans) {

        if (mealPlanAdapter == null) {
            mealPlanAdapter = new MealPlanAdapter(mealPlans, appDatabase, getContext());
            mealPlanRecyclerView.setAdapter(mealPlanAdapter);
        } else {
            mealPlanAdapter.updateMealPlans(mealPlans);
        }
    }


    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showMessage(String message) {
        View rootView = getView();
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Log.e("MealPlanFragment", "Root view is null, cannot show Snackbar");
        }
    }
}



