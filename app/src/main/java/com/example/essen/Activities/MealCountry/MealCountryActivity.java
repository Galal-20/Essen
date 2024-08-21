package com.example.essen.Activities.MealCountry;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essen.R;
import com.example.essen.pojo.MainMeal;

import java.util.ArrayList;
import java.util.List;

public class MealCountryActivity extends AppCompatActivity implements CountryContract.View {

    private RecyclerView recyclerView;
    private TextView titleText;
    private MealAdapter mealAdapter;
    private CountryContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_meal_country);

        recyclerView = findViewById(R.id.recyclerView);
        titleText = findViewById(R.id.text_title_country);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mealAdapter = new MealAdapter(this, new ArrayList<>(), presenter);
        recyclerView.setAdapter(mealAdapter);

        presenter = new MealCountryPresenter(this);

        String countryName = getIntent().getStringExtra("country_name");
        if (countryName != null) {
            presenter.loadMealsByCountry(countryName);
            setTitle(countryName);
        }
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

    @Override
    public void showMeals(List<MainMeal> meals) {
        if (meals != null && !meals.isEmpty()) {
            mealAdapter.updateMeals(meals);
        } else {
            Toast.makeText(this, "No meals found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setTitle(String title) {
        titleText.setText(title);
    }

}


