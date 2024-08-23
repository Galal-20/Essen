package com.example.essen.Activities.MealActivity;

import static com.example.essen.Fragments.HomeFragment.HomeFragment.Cat;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.INGREDIENTS;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.INSTRUCTIONS;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.LOCATION;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.NAME_MEAL;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.THUMB_MEAL;
import static com.example.essen.Fragments.HomeFragment.HomeFragment.YOUTUBE;

import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.essen.Activities.AuthActivities.Login.Login_Screen;
import com.example.essen.Fragments.HomeFragment.HomeFragment;
import com.example.essen.R;
import com.example.essen.pojo.AllDetailsMeal;
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealEntity;
import com.example.essen.room.MealPlanEntity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MealActivity extends AppCompatActivity implements MealView {

    ImageView mealImageView;
    YouTubePlayerView youTubePlayerView;
    CollapsingToolbarLayout collapsingToolbar;
    TextView mealIdTextView;
    TextView locationTextView;
    TextView mealInstructions;
    ImageView imageLinkYoutube;
    TextView textIngredients;
    FloatingActionButton favoriteButton;
    LinearProgressIndicator progressBar;
    String mealCat;
    String mealName;
    String userId;
    String mealThumb;
    String location;
    String instructions;
    String youtubeLink;
    String textIngredient;
    private MealPresenter presenter;
    private AppDatabase appDatabase;
    Button makePlanButton;
    private boolean isGuest;
    DatePicker datePicker;
    RadioGroup mealTypeGroup;
    Button saveButton;
    int day;
    int month;
    int year;
    String dayName;
    String mealType;


    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_meal);
        findView();
        presenter = new MealPresenter(this);
        appDatabase = AppDatabase.getDatabase(this);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        getDataFromIntent();
        setYoutubeLinkClickListener();


        String mealId = getIntent().getStringExtra("MEAL_ID");
        if (mealId != null) {
            presenter.loadMealDetails(mealId);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        isGuest = sharedPreferences.getBoolean("isGuest", true);

        favoriteButton.setOnClickListener(v -> {
            if (isGuest) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.Please_login_to_save_your_mael);
                builder.setMessage(R.string.Are_you_want_to_join_with_us);

                builder.setPositiveButton(R.string.Go_to_login, (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), Login_Screen.class));
                    finish();
                    dialog.dismiss();
                });

                builder.setNegativeButton(R.string.Still_Guest, (dialog, which) -> {
                    dialog.dismiss();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else {
                if (isInternetAvailable()) {
                    saveMealToFavorites();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_SHORT).show();
                }
            }

        });

        makePlanButton = findViewById(R.id.make_plane);

        makePlanButton.setOnClickListener(v -> {
            if (isGuest) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.Please_login_to_Make_plan);
                builder.setMessage(R.string.Are_you_want_to_join_with_us);

                builder.setPositiveButton(R.string.Go_to_login, (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), Login_Screen.class));
                    finish();
                    dialog.dismiss();
                });

                builder.setNegativeButton(R.string.Still_Guest, (dialog, which) -> {
                    dialog.dismiss();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else {
                if (isInternetAvailable()) {
                    showBottomSheetDialog();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_SHORT).show();
                }

            }

        });

        getLifecycle().addObserver(youTubePlayerView);


        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                String videoId = getVideoIdFromUrl(youtubeLink);
                if (videoId != null) {
                    youTubePlayer.cueVideo(videoId, 0);

                } else {
                    showMessage("Video not available");
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPipMode() {
        Rational aspectRatio = new Rational(youTubePlayerView.getWidth(), youTubePlayerView.getHeight());
        PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
        pipBuilder.setAspectRatio(aspectRatio);

        enterPictureInPictureMode(pipBuilder.build());
    }


    @Override
    public void onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPipMode();
        }
        super.onUserLeaveHint();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            makePlanButton.setVisibility(View.GONE);
            favoriteButton.setVisibility(View.GONE);
            youTubePlayerView.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                resizePictureInPicture();
            }
        } else {
            makePlanButton.setVisibility(View.VISIBLE);
            favoriteButton.setVisibility(View.VISIBLE);
            youTubePlayerView.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void resizePictureInPicture() {
        Rational aspectRatio = new Rational(9, 16);
        PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
        pipBuilder.setAspectRatio(aspectRatio);
        setPictureInPictureParams(pipBuilder.build());
    }


    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.buttom_sheet, null);

        datePicker = bottomSheetView.findViewById(R.id.date_picker);
        mealTypeGroup = bottomSheetView.findViewById(R.id.meal_type_group);
        saveButton = bottomSheetView.findViewById(R.id.btn_save);


        saveButton.setOnClickListener(v -> {
            day = datePicker.getDayOfMonth();
            month = datePicker.getMonth();
            year = datePicker.getYear();

            int selectedMealId = mealTypeGroup.getCheckedRadioButtonId();
            RadioButton selectedMealType = bottomSheetView.findViewById(selectedMealId);
            mealType = selectedMealType != null ? selectedMealType.getText().toString() : "";

            dayName = getDayName(year, month, day);

            if (mealName == null || mealName.isEmpty() || mealType.isEmpty() || dayName == null || dayName.isEmpty()) {
                Toast.makeText(MealActivity.this, "The data is empty", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> {
                    int count = appDatabase.mealPlanDao().isMealInMealPlan(mealName, dayName);
                    if (count > 0) {
                        runOnUiThread(() -> showMessage("Meal already in Meal Plan!"));
                    } else {
                        try {
                            MealPlanEntity mealPlanEntity = createMealPlanEntity();
                            mealPlanEntity.setDayName(dayName);
                            appDatabase.mealPlanDao().insert(mealPlanEntity);

                            if (user != null) {
                                firestore.collection("users").document(user.getUid())
                                        .collection("mealPlans").document(mealName + "_" + dayName)
                                        .set(mealPlanEntity)
                                        .addOnSuccessListener(aVoid -> runOnUiThread(() -> showMessage("Plan saved successfully and to Firestore!")))
                                        .addOnFailureListener(e -> runOnUiThread(() -> showMessage("Error saving to Firestore: " + e.getMessage())));
                            }
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(MealActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }
                }).start();

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    private MealPlanEntity createMealPlanEntity() {
        MealPlanEntity mealPlanEntity = new MealPlanEntity();

        mealPlanEntity.setStrMeal(mealName);
        mealPlanEntity.setStrMealThumb(mealThumb);
        mealPlanEntity.setStrCategory(mealCat);
        mealPlanEntity.setStrArea(location);
        mealPlanEntity.setStrInstructions(instructions);
        mealPlanEntity.setIngredients(textIngredient);
        mealPlanEntity.setStrYoutube(youtubeLink);
        mealPlanEntity.setMealType(mealType);
        mealPlanEntity.setDayName(dayName);
        mealPlanEntity.setDayNumber(day);
        mealPlanEntity.setMonth(month);
        mealPlanEntity.setYear(year);
        return mealPlanEntity;
    }


    private String getDayName(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }


    public String getVideoIdFromUrl(String url) {
        String videoId = null;

        String[] patterns = {
                "https?://www.youtube.com/watch\\?v=([^&]+)",
                "https?://youtu.be/([^?]+)",
                "https?://www.youtube.com/embed/([^?]+)",
        };

        for (String pattern : patterns) {
            Pattern compiledPattern = Pattern.compile(pattern);
            if (url != null && !url.isEmpty()) {
                Matcher matcher = compiledPattern.matcher(url);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }

        return videoId;

    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
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
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        textIngredients = findViewById(R.id.textIngredients);
    }

    public void getDataFromIntent() {
        mealCat = getIntent().getStringExtra(Cat);
        mealName = getIntent().getStringExtra(NAME_MEAL);
        mealThumb = getIntent().getStringExtra(HomeFragment.THUMB_MEAL);
        location = getIntent().getStringExtra(HomeFragment.LOCATION);
        instructions = getIntent().getStringExtra(HomeFragment.INSTRUCTIONS);
        youtubeLink = getIntent().getStringExtra(HomeFragment.YOUTUBE);
        textIngredient = getIntent().getStringExtra(INGREDIENTS);
        presenter.loadMealData(mealCat, mealName, mealThumb, location, instructions, youtubeLink,
                textIngredient
        );
    }

    public void setYoutubeLinkClickListener() {
        imageLinkYoutube.setOnClickListener(v -> presenter.handleYoutubeLinkClick(youtubeLink));
    }

    @Override
    public void showMealName(String name) {
        collapsingToolbar.setTitle(name);
    }

    @Override
    public void showMeals(AllDetailsMeal meal) {
        Intent intent = new Intent(this, MealActivity.class);
        intent.putExtra(NAME_MEAL, meal.getStrMeal());
        intent.putExtra(THUMB_MEAL, meal.getStrMealThumb());
        intent.putExtra(Cat, meal.getStrCategory());
        intent.putExtra(LOCATION, meal.getStrArea());
        intent.putExtra(INSTRUCTIONS, meal.getStrInstructions());
        intent.putExtra(YOUTUBE, meal.getStrYoutube());
        intent.putExtra(INGREDIENTS,
                meal.getStrIngredient1() + "\n" +
                        meal.getStrIngredient2() + "\n" +
                        meal.getStrIngredient3() + "\n" +
                        meal.getStrIngredient4() + "\n" +
                        meal.getStrIngredient5() + "\n" +
                        meal.getStrIngredient6() + "\n" +
                        meal.getStrIngredient7() + "\n" +
                        meal.getStrIngredient8() + "\n" +
                        meal.getStrIngredient9() + "\n" +
                        meal.getStrIngredient10() + "\n" +
                        meal.getStrIngredient11() + "\n" +
                        meal.getStrIngredient12() + "\n" +
                        meal.getStrIngredient13() + "\n" +
                        meal.getStrIngredient14() + "\n" +
                        meal.getStrIngredient15() + "\n"
        )

        ;
        startActivity(intent);
        finish();

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
    public void showMeaIngredients(String ingredients) {
        textIngredients.setText(ingredients);
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
        imageLinkYoutube.setVisibility(View.GONE);
        youTubePlayerView.setVisibility(View.VISIBLE);
        if (url != null && !url.isEmpty()) {
            imageLinkYoutube.setVisibility(View.GONE);
            youTubePlayerView.setVisibility(View.VISIBLE);

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    String videoId = getVideoIdFromUrl(url);
                    if (videoId != null) {
                        youTubePlayer.cueVideo(videoId, 0);
                    } else {
                        showMessage("Invalid YouTube URL");
                    }
                }
            });
        } else {
            showMessage("YouTube link not available");
        }
    }

    private void saveMealToFavorites() {
        if (mealName != null) {
            new Thread(() -> {
                try {
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
                        mealEntity.setIngredients(textIngredient);

                        // Insert the meal into the local Room database
                        appDatabase.runInTransaction(() -> {
                            try {
                                appDatabase.mealDao().insert(mealEntity);
                            } catch (Exception e) {
                                runOnUiThread(() -> Toast.makeText(this, "Error inserting meal locally: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        });

                        if (user != null) {
                            firestore.runTransaction(transaction -> {
                                        DocumentReference mealRef = firestore.collection("users").document(user.getUid())
                                                .collection("favorites").document(mealName);

                                        MealEntity existingMeal = transaction.get(mealRef).toObject(MealEntity.class);

                                        if (existingMeal == null) {
                                            transaction.set(mealRef, mealEntity);
                                        }

                                        return null;
                                    }).addOnSuccessListener(aVoid -> runOnUiThread(() -> showMessage("Meal added to favorites and saved to Firestore!")))
                                    .addOnFailureListener(e -> runOnUiThread(() -> showMessage("Error saving to Firestore: " + e.getMessage())));
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Error processing favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        } else {
            showMessage("Meal data not saved.");
        }
    }
}

