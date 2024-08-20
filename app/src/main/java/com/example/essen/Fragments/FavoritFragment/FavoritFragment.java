package com.example.essen.Fragments.FavoritFragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

       /* fetchUserFavorites();
        checkInternetAndSync();*/
        setupRealtimeUpdates();

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
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            Toast.makeText(requireContext(), "Error listening to changes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (snapshots != null && !snapshots.isEmpty()) {
                            List<MealEntity> favoriteMeals = new ArrayList<>();
                            for (QueryDocumentSnapshot document : snapshots) {
                                MealEntity meal = document.toObject(MealEntity.class);
                                favoriteMeals.add(meal);
                            }
                            adapter.submitList(favoriteMeals);
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        checkInternetAndSync();
    }

    private void checkInternetAndSync() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            syncFavoritesFromFirestore();
        }
    }

    private void syncFavoritesFromFirestore() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid())
                    .collection("favorites")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            new Thread(() -> {
                                List<MealEntity> newMeals = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    MealEntity meal = document.toObject(MealEntity.class);
                                    int count = appDatabase.mealDao().isMealInFavorites(meal.getStrMeal().trim());

                                    try {
                                        if (count == 0) {
                                            appDatabase.mealDao().insert(meal);
                                            newMeals.add(meal);
                                        }
                                    } catch (Exception e) {
                                        // Log the exception if needed
                                    }
                                }

                                // Only update UI if the fragment is still attached to the activity
                                if (isAdded() && getActivity() != null) {
                                    requireActivity().runOnUiThread(() -> {
                                        presenter.getFavoriteMeals().observe(getViewLifecycleOwner(), meals -> {
                                            Set<MealEntity> uniqueMeals = new HashSet<>(meals); // Remove duplicates
                                            adapter.submitList(new ArrayList<>(uniqueMeals));
                                        });
                                    });
                                }
                            }).start();
                        } else {
                            if (isAdded() && getActivity() != null) {
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(requireContext(), "Error syncing with Firestore", Toast.LENGTH_SHORT).show()
                                );
                            }
                        }
                    });
        } else {
            if (isAdded() && getActivity() != null) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                );
            }
        }
    }


    /* private void setupRealtimeUpdates() {
         FirebaseUser currentUser = firebaseAuth.getCurrentUser();
         if (currentUser != null) {
             firestore.collection("users").document(currentUser.getUid())
                     .collection("favorites")
                     .addSnapshotListener((snapshots, e) -> {
                         if (e != null) {
                             if (isAdded() && getActivity() != null) {
                                 requireActivity().runOnUiThread(() ->
                                         Toast.makeText(requireContext(), "Error listening to updates", Toast.LENGTH_SHORT).show()
                                 );
                             }
                             return;
                         }

                         new Thread(() -> {
                             if (snapshots != null && !snapshots.isEmpty()) {
                                 List<MealEntity> updatedMeals = new ArrayList<>();
                                 for (QueryDocumentSnapshot document : snapshots) {
                                     MealEntity meal = document.toObject(MealEntity.class);
                                     updatedMeals.add(meal);

                                     try {
                                         appDatabase.mealDao().insert(meal);
                                     } catch (Exception ex) {
                                         // Handle insertion exceptions if necessary
                                     }
                                 }

                                 // Only update UI if the fragment is still attached to the activity
                                 if (isAdded() && getActivity() != null) {
                                     requireActivity().runOnUiThread(() -> {
                                         presenter.getFavoriteMeals().observe(getViewLifecycleOwner(), meals -> {
                                             Set<MealEntity> uniqueMeals = new HashSet<>(meals); // Remove duplicates
                                             adapter.submitList(new ArrayList<>(uniqueMeals));
                                         });
                                     });
                                 }
                             }
                         }).start();
                     });
         } else {
             if (isAdded() && getActivity() != null) {
                 requireActivity().runOnUiThread(() ->
                         Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                 );
             }
         }
     }*/
    private void setupRealtimeUpdates() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid())
                    .collection("favorites")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            if (isAdded() && getActivity() != null) {
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(requireContext(), "Error listening to updates", Toast.LENGTH_SHORT).show()
                                );
                            }
                            return;
                        }

                        new Thread(() -> {
                            if (snapshots != null) {
                                List<MealEntity> updatedMeals = new ArrayList<>();
                                for (QueryDocumentSnapshot document : snapshots) {
                                    MealEntity meal = document.toObject(MealEntity.class);
                                    updatedMeals.add(meal);
                                }

                                // Only update UI if the fragment is still attached to the activity
                                if (isAdded() && getActivity() != null) {
                                    requireActivity().runOnUiThread(() -> {
                                        presenter.getFavoriteMeals().observe(getViewLifecycleOwner(), meals -> {
                                            adapter.submitList(updatedMeals);
                                        });
                                    });
                                }
                            }
                        }).start();
                    });
        }
    }





    private void showCustomDialog(String mealName) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        Button yesButton = dialogView.findViewById(R.id.cancel_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_Ok);

        AlertDialog dialog = builder.create();

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

    /*private void deleteFavorite(String mealName) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        new Thread(() -> {
            try {
                appDatabase.mealDao().deleteMeal(mealName);
            }catch (Exception e){

            }
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
        }).start();

    }*/

    private void deleteFavorite(String mealName) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        new Thread(() -> {
            try {
                // Delete from the local Room database
                appDatabase.mealDao().deleteMeal(mealName);

                if (currentUser != null) {
                    // Delete from Firestore
                    firestore.collection("users").document(currentUser.getUid())
                            .collection("favorites").document(mealName)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                if (isAdded() && getActivity() != null) {
                                    requireActivity().runOnUiThread(() -> {
                                        showMessage("Meal removed from favorites and Firestore!");
                                        // Update UI with the latest data from Firestore
                                        syncFavoritesFromFirestore();
                                    });
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (isAdded() && getActivity() != null) {
                                    requireActivity().runOnUiThread(() ->
                                            showMessage("Error removing from Firestore: " + e.getMessage()));
                                }
                            });
                } else {
                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() ->
                                showMessage("User not logged in, unable to remove from Firestore."));
                    }
                }
            } catch (Exception e) {
                Log.e("DeleteError", "Error deleting meal: " + mealName, e);
            }
        }).start();
    }


    private void showMessage(String s) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDeleteClick(String mealName) {
        requireActivity().runOnUiThread(() -> showCustomDialog(mealName));
    }
}


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
        });

 */

/* private void syncFavoritesFromFirestore() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid())
                    .collection("favorites")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            new Thread(() -> {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    MealEntity meal = document.toObject(MealEntity.class);
                                    int count = appDatabase.mealDao().isMealInFavorites(meal.getStrMeal());

                                    if (count == 0) {
                                        appDatabase.mealDao().insert(meal);
                                    }
                                }

                                // Notify the adapter to refresh the data
                                requireActivity().runOnUiThread(() -> {
                                    presenter.getFavoriteMeals().observe(getViewLifecycleOwner(), meals -> {
                                        adapter.submitList(meals);
                                    });
                                });
                            }).start();
                        } else {
                            Toast.makeText(requireContext(), "Error syncing with Firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }*/