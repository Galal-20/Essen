package com.example.essen.Fragments.CountriesFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essen.R;
import com.example.essen.pojo.MainMeal;
import com.example.essen.retrofit.MealAPI;
import com.example.essen.retrofit.RetrofitInstance;

import java.util.List;

public class CountriesFragment extends Fragment implements CountriesContract.View {

    private CountriesPresenter presenter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MealAPI mealAPI = RetrofitInstance.getApi();

        presenter = new CountriesPresenter(this, mealAPI);
        presenter.getCountries();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void showCountries(List<MainMeal> countries) {
        CountriesAdapter adapter = new CountriesAdapter(getContext(), countries);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
