package com.example.essen.Fragments.SearchFragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essen.R;
import com.example.essen.Util.NetworkChangeReceiver;
import com.example.essen.pojo.MainMeal;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private SearchResultAdapter adapter;
    private SearchPresenter presenter;
    Button searchCountryButton;
    Button searchIngredientButton;
    Button searchCategoryButton;
    private Button lastClickedButton = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SearchPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults);
        searchView = view.findViewById(R.id.searchView);

        searchCountryButton = view.findViewById(R.id.Searchcountry);
        searchIngredientButton = view.findViewById(R.id.SearchIngredient);
        searchCategoryButton = view.findViewById(R.id.SearchCategory);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchResultAdapter(getContext());
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private Handler handler = new Handler();
            private Runnable searchRunnable;

            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.trim();
                if (!query.isEmpty()) {
                    presenter.searchMeals(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String query = newText.trim();
                    if (query.isEmpty()) {
                        searchCountryButton.setBackgroundColor(getResources().getColor(R.color.white));
                        searchIngredientButton.setBackgroundColor(getResources().getColor(R.color.white));
                        searchCategoryButton.setBackgroundColor(getResources().getColor(R.color.white));
                        clearResults();
                    } else {
                        presenter.searchMeals(query);
                    }
                };
                handler.postDelayed(searchRunnable, 100);
                return false;
            }
        });

        View.OnClickListener searchButtonClickListener = v -> {
            String query = searchView.getQuery().toString().trim();
            if (!query.isEmpty()) {
                if (!NetworkChangeReceiver.isConnectedToInternet(getContext())) {
                    Toast.makeText(getContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("SearchButtonClick", "Button clicked: " + v.getId() + ", Query: " + query);

                if (lastClickedButton != null && lastClickedButton != v) {
                    lastClickedButton.setBackgroundColor(getResources().getColor(R.color.white));
                }

                v.setBackgroundColor(getResources().getColor(R.color.green));

                lastClickedButton = (Button) v;


                if (v == searchCountryButton) {
                    Log.d("SearchButtonClick", "Searching by country");
                    presenter.searchMealsByCountry(query);
                } else if (v == searchIngredientButton) {
                    Log.d("SearchButtonClick", "Searching by ingredient");
                    presenter.searchMealsByIngredient(query);
                } else if (v == searchCategoryButton) {
                    Log.d("SearchButtonClick", "Searching by category");
                    presenter.searchMealsByCategory(query);
                }
            } else {
                Toast.makeText(getContext(), "Please enter a search query", Toast.LENGTH_SHORT).show();
            }
        };
        searchCountryButton.setOnClickListener(searchButtonClickListener);
        searchIngredientButton.setOnClickListener(searchButtonClickListener);
        searchCategoryButton.setOnClickListener(searchButtonClickListener);

        return view;
    }



    private void clearResults() {
        adapter.setMealList(new ArrayList<>());
    }

    @Override
    public void showMeals(List<MainMeal> meals) {
        if (meals == null || meals.isEmpty()) {
            Log.d("SearchFragment", "Showing meals: " + meals);
            clearResults();
        } else {
            adapter.setMealList(meals);
        }
    }
    @Override
    public void showError(String error) {
        clearResults();
    }
}
