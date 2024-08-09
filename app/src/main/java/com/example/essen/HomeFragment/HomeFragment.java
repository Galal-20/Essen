package com.example.essen.HomeFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.essen.MealActivity.MealActivity;
import com.example.essen.R;
import com.example.essen.pojo.Category;
import com.example.essen.pojo.Meal;

import java.util.List;


public class HomeFragment extends Fragment implements HomeContract.View {

    public static final String Cat = "com.example.essen.HomeFragment.strCat";
    public static final String NAME_MEAL = "com.example.essen.HomeFragment.nameMeal";
    public static final String THUMB_MEAL = "com.example.essen.HomeFragment.thumbMeal";
    public static final String LOCATION = "com.example.essen.HomeFragment.location";
    public static final String INSTRUCTIONS = "com.example.essen.HomeFragment.Instructions";
    public static final String YOUTUBE = "com.example.essen.HomeFragment.youtube";
    RecyclerView categoryRecyclerView;
    private ImageView imageView;
    private HomeContract.Presenter presenter;
    private CardView cardView;
    private RecyclerView recyclerView;
    private PopularFoodAdapter popularFoodAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Meal randomMeal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new HomePresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageView = view.findViewById(R.id.imageView);
        cardView = view.findViewById(R.id.cardF);
        recyclerView = view.findViewById(R.id.recycleP);
        categoryRecyclerView = view.findViewById(R.id.recycleC);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        presenter.getRandomMeal();
        presenter.getPopularMeals();
        presenter.getCategories();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.getRandomMeal();
            presenter.getPopularMeals();
            presenter.getCategories();
        });

        cardView.setOnClickListener(v -> {
            if (randomMeal != null) {
                Intent intent = new Intent(getContext(), MealActivity.class);
                intent.putExtra(Cat, randomMeal.getStrCategory());
                intent.putExtra(NAME_MEAL, randomMeal.getStrMeal());
                intent.putExtra(THUMB_MEAL, randomMeal.getStrMealThumb());
                intent.putExtra(LOCATION, randomMeal.getStrArea());
                intent.putExtra(INSTRUCTIONS, randomMeal.getStrInstructions());
                intent.putExtra(YOUTUBE, randomMeal.getStrYoutube());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Meal data not available", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    @Override
    public void showRandomMeal(Meal meal) {
        randomMeal = meal;
        Glide.with(getContext())
                .load(meal.getStrMealThumb())
                .into(imageView);

        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showPopularMeals(List<Meal> meal) {

        if (popularFoodAdapter == null) {
            popularFoodAdapter = new PopularFoodAdapter(getContext(), meal);
            recyclerView.setAdapter(popularFoodAdapter);

        } else {
            popularFoodAdapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void showCategories(List<Category> categories) {
        if (categories != null && !categories.isEmpty()) {
            categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns, vertical
            // orientation
            CategoryAdapter categoryAdapter = new CategoryAdapter();
            categoryAdapter.setCategoriesList(categories);
            categoryRecyclerView.setAdapter(categoryAdapter);

            // Set a click listener if needed
            categoryAdapter.setOnItemClicked(category -> {
                // Handle the category click event here
                Toast.makeText(getContext(), "Clicked: " + category.getStrCategory(), Toast.LENGTH_SHORT).show();
            });
        } else {
            showError("No categories available");
        }
    }


}



