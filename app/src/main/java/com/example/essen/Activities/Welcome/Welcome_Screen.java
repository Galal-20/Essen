package com.example.essen.Activities.Welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.Activities.AuthActivities.Login.Login_Screen;
import com.example.essen.Activities.AuthActivities.SignUp.SignUp_Screen;
import com.example.essen.Activities.MainActivity.MainActivity;
import com.example.essen.R;

public class Welcome_Screen extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_welcome_screen);
        applyFadeInAnimation();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToMainActivity();
        }
    }

    private void applyFadeInAnimation() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(4000);

        TextView textWelcome = findViewById(R.id.Text_welcome);
        Button buttonRegWelcome = findViewById(R.id.button_reg_Welcome);
        Button buttonLoginWelcome = findViewById(R.id.login_button_welcome);
        Button buttonGustWelcome = findViewById(R.id.gust_button_welcome);

        textWelcome.startAnimation(fadeIn);
        buttonRegWelcome.startAnimation(fadeIn);
        buttonLoginWelcome.startAnimation(fadeIn);
        buttonGustWelcome.startAnimation(fadeIn);
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

    public void register_Screen(View view) {
        startActivity(new Intent(Welcome_Screen.this, SignUp_Screen.class));
        finish();
    }

    public void Login_screen(View view) {
        startActivity(new Intent(this, Login_Screen.class));
        finish();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void Guest(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("isGuest", true);
        editor.apply();

        navToMain();
    }
}


