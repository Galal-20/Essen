package com.example.essen.MealActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.essen.HomeFragment.HomeFragment;
import com.example.essen.R;
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealEntity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;


public class MealActivity extends AppCompatActivity implements MealView {

    ImageView mealImageView;
    CollapsingToolbarLayout collapsingToolbar;
    TextView mealIdTextView;
    TextView locationTextView;
    TextView mealInstructions;
    ImageView imageLinkYoutube;
    FloatingActionButton favoriteButton;
    LinearProgressIndicator progressBar;
    String mealCat;
    String mealName;
    String mealThumb;
    String location;
    String instructions;
    String youtubeLink;
    private MealPresenter presenter;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_meal);
        findView();
        presenter = new MealPresenter(this);
        appDatabase = AppDatabase.getDatabase(this); // Initialize Room Database
        getDataFromIntent();
        setYoutubeLinkClickListener();

        favoriteButton.setOnClickListener(v -> {
            saveMealToFavorites();
        });
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

    public void findView() {
        mealImageView = findViewById(R.id.maelDetail);
        collapsingToolbar = findViewById(R.id.CTPL);
        mealIdTextView = findViewById(R.id.tv_category);
        locationTextView = findViewById(R.id.tv_Area);
        mealInstructions = findViewById(R.id.tvInst);
        imageLinkYoutube = findViewById(R.id.linkY);
        progressBar = findViewById(R.id.proBar);
        favoriteButton = findViewById(R.id.float_button);
    }

    public void getDataFromIntent() {
        mealCat = getIntent().getStringExtra(HomeFragment.Cat);
        mealName = getIntent().getStringExtra(HomeFragment.NAME_MEAL);
        mealThumb = getIntent().getStringExtra(HomeFragment.THUMB_MEAL);
        location = getIntent().getStringExtra(HomeFragment.LOCATION);
        instructions = getIntent().getStringExtra(HomeFragment.INSTRUCTIONS);
        youtubeLink = getIntent().getStringExtra(HomeFragment.YOUTUBE);
        presenter.loadMealData(mealCat, mealName, mealThumb, location, instructions, youtubeLink);
    }

    public void setYoutubeLinkClickListener() {
        imageLinkYoutube.setOnClickListener(v -> presenter.handleYoutubeLinkClick(youtubeLink));
    }

    @Override
    public void showMealName(String name) {
        collapsingToolbar.setTitle(name);
    }

    @Override
    public void showMealImage(String imageUrl) {
        Glide.with(this).load(imageUrl).into(mealImageView);
    }

    @Override
    public void showMealCategory(String category) {
        mealIdTextView.setText("Category: " + category);
    }

    @Override
    public void showMealLocation(String location) {
        locationTextView.setText("Area: " + location);
    }

    @Override
    public void showMealInstructions(String instructions) {
        mealInstructions.setText(instructions);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void openYoutubeLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }


    private void saveMealToFavorites() {
        if (mealName != null) {
            new Thread(() -> {
                int count = appDatabase.mealDao().isMealInFavorites(mealName);

                if (count > 0) {
                    runOnUiThread(() -> showMessage("Meal is already in favorites!"));
                } else {
                    MealEntity mealEntity = new MealEntity();
                    mealEntity.setStrMeal(mealName);
                    mealEntity.setStrMealThumb(mealThumb);
                    mealEntity.setStrCategory(mealCat);
                    mealEntity.setStrArea(location);
                    mealEntity.setStrInstructions(instructions);
                    mealEntity.setStrYoutube(youtubeLink);

                    appDatabase.mealDao().insert(mealEntity);

                    runOnUiThread(() -> showMessage("Meal added to favorites!"));
                }
            }).start();
        } else {
            showMessage("Meal data is incomplete.");
        }
    }

     /*private void saveMealToFavorites() {
        if (mealName != null) {
            MealEntity mealEntity = new MealEntity();
            mealEntity.setStrMeal(mealName);
            mealEntity.setStrMealThumb(mealThumb);
            mealEntity.setStrCategory(mealCat);
            mealEntity.setStrArea(location);
            mealEntity.setStrInstructions(instructions);
            mealEntity.setStrYoutube(youtubeLink);

            new Thread(() -> {
                appDatabase.mealDao().insert(mealEntity);
                runOnUiThread(() -> showMessage("Meal added to favorites!"));
            }).start();
        } else {
            showMessage("Meal data is incomplete.");
        }
    }*/

}
