package com.example.essen.view.Auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.MainActivity;
import com.example.essen.R;
import com.example.essen.presenter.LoginPresenter;

public class Login_Screen extends AppCompatActivity implements AuthViewLogin {
    private EditText emailInput;
    private EditText passwordInput;
    private LoginPresenter presenter;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final int RC_SIGN_IN = 9001;
    private ProgressBar progressBar; // Declare ProgressBar
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_login_screen);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress);
        presenter = new LoginPresenter(this, this);

        HidePassword();
        SharedPreferences();


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

    public void Login_button(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            presenter.login(email, password);
        }
    }

    public void LoginGoogle(View view) {
        progressBar.setVisibility(View.VISIBLE);
        presenter.loginWithGoogle(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            presenter.handleGoogleSignInResult(data);
        }
    }

    @Override
    public void showLoginSuccess(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Save login state
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        // Navigate to main activity
        navigateToMainActivity();
    }

    @Override
    public void showLoginError(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void forgetPassword(View view) {
        startActivity(new Intent(this, ForgetPassword.class));
    }

    public void register_text(View view) {
        startActivity(new Intent(this, SignUp_Screen.class));
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void HidePassword() {
        passwordInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (isPasswordVisible) {
                        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.key, 0, R.drawable.hidden, 0);
                        isPasswordVisible = false;
                    } else {
                        passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.key, 0, R.drawable.eye_icon, 0);
                        isPasswordVisible = true;
                    }
                    return true;
                }
            }
            return false;
        });
    }


    public void SharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToMainActivity();
        }
    }
}
