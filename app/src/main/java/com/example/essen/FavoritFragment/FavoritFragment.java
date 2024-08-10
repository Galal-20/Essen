package com.example.essen.FavoritFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essen.R;
import com.example.essen.room.AppDatabase;
import com.google.android.material.snackbar.Snackbar;

public class FavoritFragment extends Fragment implements FavoriteAdapter.OnDeleteClickListener {
    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FavoritePresenter presenter;
    private AppDatabase appDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorit, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new FavoriteAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        appDatabase = AppDatabase.getDatabase(requireContext());
        presenter = new FavoritePresenter(getActivity().getApplication());
        presenter.getFavoriteMeals().observe(getViewLifecycleOwner(), meals -> {
            adapter.submitList(meals);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    @Override
    public void onDeleteClick(String mealName) {
        new Thread(() -> {
            appDatabase.mealDao().deleteMeal(mealName);
            requireActivity().runOnUiThread(() -> {
                presenter.getFavoriteMeals().observe(getViewLifecycleOwner(), meals -> {
                    adapter.submitList(meals);
                });
                Snackbar.make(recyclerView, "Meal removed from favorites!", Snackbar.LENGTH_SHORT).show();
            });
        }).start();
    }
}

