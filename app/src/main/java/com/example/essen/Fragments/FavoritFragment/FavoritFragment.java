package com.example.essen.Fragments.FavoritFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essen.R;
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealEntity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritFragment extends Fragment implements FavoriteAdapter.OnDeleteClickListener {
    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FavoritePresenter presenter;
    private AppDatabase appDatabase;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorit, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new FavoriteAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        appDatabase = AppDatabase.getDatabase(requireContext());
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        presenter = new FavoritePresenter(getActivity().getApplication());

        presenter.getFavoriteMeals().observe(getViewLifecycleOwner(), meals -> {
            adapter.submitList(meals);
        });

        fetchUserFavorites();


        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchUserFavorites();
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    private void fetchUserFavorites() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid())
                    .collection("favorites")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<MealEntity> favoriteMeals = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MealEntity meal = document.toObject(MealEntity.class);
                                favoriteMeals.add(meal);
                            }
                            adapter.submitList(favoriteMeals);
                        } else {
                            Toast.makeText(requireContext(), "Error fetching user favorites", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void showCustomDialog(String mealName) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        Button yesButton = dialogView.findViewById(R.id.cancel_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_Ok);

        AlertDialog dialog = builder.create();

       /* yesButton.setOnClickListener(view -> {
            new Thread(() -> {
                // Delete from local database
                appDatabase.mealDao().deleteMeal(mealName);

                // Delete from Firestore
                if (firebaseAuth.getCurrentUser() != null) {
                    firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                            .collection("favorites").document(mealName)
                            .delete()
                            .addOnSuccessListener(aVoid -> requireActivity().runOnUiThread(() -> {
                                presenter.getFavoriteMeals().observe(getViewLifecycleOwner(), meals -> {
                                    adapter.submitList(meals);
                                });
                                Snackbar.make(recyclerView, "Meal removed from favorites and Firestore!", Snackbar.LENGTH_SHORT).show();
                            }))
                            .addOnFailureListener(e -> requireActivity().runOnUiThread(() -> {
                                Snackbar.make(recyclerView, "Error removing from Firestore: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }));
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Snackbar.make(recyclerView, "User not logged in, unable to remove from Firestore.", Snackbar.LENGTH_SHORT).show();
                    });
                }
            }).start();
            dialog.dismiss();
        });*/

        yesButton.setOnClickListener(view -> {
            deleteFavorite(mealName);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view -> {
            dialog.dismiss();
            Snackbar.make(recyclerView, "Meal still in favorites!", Snackbar.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void deleteFavorite(String mealName) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid())
                    .collection("favorites").document(mealName)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        fetchUserFavorites(); // Refresh favorites after deletion
                        Snackbar.make(recyclerView, "Meal removed from favorites!", Snackbar.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Snackbar.make(recyclerView, "Error removing from Firestore: " + e.getMessage(), Snackbar.LENGTH_SHORT).show());
        } else {
            Snackbar.make(recyclerView, "User not logged in, unable to remove from Firestore.", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(String mealName) {
        requireActivity().runOnUiThread(() -> showCustomDialog(mealName));
    }
}
