package com.example.essen.SearchFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essen.R;
import com.example.essen.pojo.MainMeal;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private SearchResultAdapter adapter;
    private SearchPresenter presenter;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchResultAdapter(getContext());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    presenter.searchMeals(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    clearResults();
                } else {
                    presenter.searchMeals(newText);
                }
                return false;
            }
        });

        return view;
    }

    private void clearResults() {
        adapter.setMealList(new ArrayList<>());
        //Toast.makeText(getContext(), "Search cleared", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void showMeals(List<MainMeal> meals) {
        if (meals == null || meals.isEmpty()) {
            clearResults();
            //Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setMealList(meals);
        }
    }


    @Override
    public void showError(String error) {
        clearResults();
        //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

    }

     /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.searchMeals(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.searchMeals(newText);
                return false;
            }
        });*/
}
