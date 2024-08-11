package com.example.essen.Activities.MealActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.essen.Fragments.HomeFragment.HomeFragment;
import com.example.essen.R;
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealEntity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

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

        getLifecycle().addObserver(youTubePlayerView);

        // Load the YouTube video
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                /*
                String videoId = youtubeLink.split("v=")[1];
                youTubePlayer.loadVideo(videoId, 0);*/
                /* youTubePlayer.loadVideo(getVideoIdFromUrl(youtubeLink), 0);*/
                String videoId = getVideoIdFromUrl(youtubeLink);
                if (videoId != null) {
                    //youTubePlayer.loadVideo(videoId, 0);
                    youTubePlayer.cueVideo(videoId, 0);

                } else {
                    showMessage("Invalid YouTube URL");
                }

            }
        });

    }

    public String getVideoIdFromUrl(String url) {
        String videoId = null;

        // Patterns to match various YouTube URL formats
        String[] patterns = {
                "https?://www.youtube.com/watch\\?v=([^&]+)",   // https://www.youtube.com/watch?v=VIDEO_ID
                "https?://youtu.be/([^?]+)",                    // https://youtu.be/VIDEO_ID
                "https?://www.youtube.com/embed/([^?]+)",       // https://www.youtube.com/embed/VIDEO_ID
        };

        for (String pattern : patterns) {
            Pattern compiledPattern = Pattern.compile(pattern);

            if (url != null && !url.isEmpty()) {
                Matcher matcher = compiledPattern.matcher(url);
                if (matcher.find()) {
                    return matcher.group(1); // Assuming you're capturing the first group
                }
            } else {
                Log.e("MealActivity", "The provided URL is null or empty");
            }
        }

        return videoId;
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

          /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);*/
        imageLinkYoutube.setVisibility(View.GONE);
        youTubePlayerView.setVisibility(View.VISIBLE);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                String videoId = url.split("v=")[1];
                youTubePlayer.loadVideo(videoId, 0);
            }
        });


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

    /* webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        // Convert YouTube URL to embed format if necessary
        if (url.contains("youtube.com/watch")) {
            url = url.replace("watch?v=", "embed/");
        }

        String html = "<html><body style=\"margin:0;padding:0;\"><iframe width=\"100%\" height=\"100%\" src=\"" + url + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

        // Load the HTML content in WebView
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);*/
       /* webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        // Convert YouTube URL to embed format if necessary
        if (url.contains("youtube.com/watch")) {
            url = url.replace("watch?v=", "embed/");
        }

        String html = "<html><body style=\"margin:0;padding:0;\"><iframe width=\"100%\" height=\"100%\" src=\"" + url + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);*/
       /* webView.loadData(url, "text/html", "utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());*/
