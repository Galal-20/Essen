package com.example.essen.Activities.Welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.essen.Activities.AuthActivities.Login.Login_Screen;
import com.example.essen.Activities.AuthActivities.SignUp.SignUp_Screen;
import com.example.essen.Activities.MainActivity.MainActivity;
import com.example.essen.R;
import com.example.essen.repository.MealRepository;
import com.example.essen.repository.MealRepositoryImpl;
import com.example.swipebutton_library.SwipeButton;

public class Welcome_Screen extends AppCompatActivity implements WelcomeContract.View {
    private static final String PREFS_NAME = "MyPrefsFile";

    TextView textWelcome;
    SwipeButton buttonRegWelcome;
    SwipeButton buttonLoginWelcome;
    SwipeButton swipeButton;
    LottieAnimationView lottieAnimationView;


    private Welcome_Presenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_welcome_screen);
        find_View_ID();


        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        MealRepository mealRepository = new MealRepositoryImpl(sharedPreferences);
        presenter = new Welcome_Presenter(this, mealRepository);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToMainActivity();
        }

        lottieAnimationView.setAnimation(R.raw.gray);
        lottieAnimationView.playAnimation();


        presenter.checkLoginStatus();


        swipeButton.setOnActiveListener(() -> Guest(null));
        buttonLoginWelcome.setOnActiveListener(() -> Login_screen(null));
        buttonRegWelcome.setOnActiveListener(() -> register_Screen(null));

        swipeButton.postDelayed(() -> {
            fadeInButton(swipeButton);
            fadeInButton(buttonRegWelcome);
            fadeInButton(buttonLoginWelcome);
            fadeInButton(textWelcome);
            fadeInButton(lottieAnimationView);
        }, 200);

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

    private void fadeInButton(View view) {
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(4000);
        fadeIn.setFillAfter(true);
        view.startAnimation(fadeIn);
    }

    public void find_View_ID() {
        swipeButton = findViewById(R.id.gust_button_welcome);
        textWelcome = findViewById(R.id.Text_welcome);
        buttonRegWelcome = findViewById(R.id.button_reg_Welcome);
        buttonLoginWelcome = findViewById(R.id.login_button_welcome);
        lottieAnimationView = findViewById(R.id.lottie_animation);
    }

    public void register_Screen(View view) {
        startActivity(new Intent(Welcome_Screen.this, SignUp_Screen.class));
        finish();
    }

    public void Login_screen(View view) {
        startActivity(new Intent(this, Login_Screen.class));
        finish();
    }

    public void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToSignUp() {
        startActivity(new Intent(this, SignUp_Screen.class));
        finish();
    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(this, Login_Screen.class));
        finish();
    }

    private void navToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void Guest(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isGuest", true);
        editor.apply();
        navToMain();
    }
}


