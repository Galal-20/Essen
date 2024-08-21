package com.example.essen.Activities.CategoryMealActivity;

import static com.example.essen.Fragments.HomeFragment.HomeFragment.CATEGORY_NAME;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essen.R;
import com.example.essen.pojo.MealX;

import java.util.List;

public class CategoryMeal extends AppCompatActivity implements CategoryContract.View {

    TextView text_title;
    String name_of_title;
    private CategoryContract.presenter presenter;
    private RecyclerView categoryMealsRecyclerView;
    private CategoryMealsAdapter categoryMealsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();

        setContentView(R.layout.category_meal);

        categoryMealsRecyclerView = findViewById(R.id.rvMeals);
        text_title = findViewById(R.id.text_title);
        categoryMealsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        categoryMealsAdapter = new CategoryMealsAdapter(this);
        categoryMealsRecyclerView.setAdapter(categoryMealsAdapter);

        presenter = new CategoryPresenter(this);
        String categoryName = getIntent().getStringExtra(CATEGORY_NAME);
        name_of_title = getIntent().getStringExtra(CATEGORY_NAME);

        if (categoryName != null) {
            presenter.getCategoryByMeal(categoryName);
        } else {
            showError("Category name is not available");
        }
    }

    @Override
    public void showCategoryMeal(List<MealX> mealList) {
        categoryMealsAdapter.setMealsList(mealList);
        text_title.setText(name_of_title);

    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void Hide_status_Bar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
