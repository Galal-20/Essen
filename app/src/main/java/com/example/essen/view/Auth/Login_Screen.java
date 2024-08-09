package com.example.essen.view.Auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.MainActivity.MainActivity;
import com.example.essen.R;
import com.example.essen.Util.NetworkChangeReceiver;
import com.example.essen.Util.SecurePreferences;
import com.example.essen.presenter.LoginPresenter;
import com.google.android.material.snackbar.Snackbar;

public class Login_Screen extends AppCompatActivity implements AuthViewLogin {
    private EditText emailInput;
    private EditText passwordInput;
    private LoginPresenter presenter;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String SERURE_KEY = "password_key_09@0";
    private static final int RC_SIGN_IN = 9001;
    private ProgressBar progressBar; // Declare ProgressBar
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_login_screen);
        findViewsById();
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

    public void findViewsById() {
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress);
    }

    public void Login_button(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(view, "Please fill all fields", Snackbar.LENGTH_SHORT).show();
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

        SecurePreferences securePreferences = new SecurePreferences(this, PREFS_NAME, SERURE_KEY, true);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
        securePreferences.put("isLoggedIn", "true");

        navigateToMainActivity();
    }

    @Override
    public void showLoginError(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void checkNetworkStatus(String message) {

        boolean isConnected = NetworkChangeReceiver.isConnectedToInternet(this);
        if (!isConnected) {
            showCustomSnackbar(message, Snackbar.LENGTH_SHORT);
        }
    }


    private void showCustomSnackbar(String message, int duration) {
        View rootView = findViewById(android.R.id.content);

        Snackbar snackbar = Snackbar.make(rootView, "", duration);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customSnackbarView = inflater.inflate(R.layout.snackbar_custom, null);

        snackbarLayout.addView(customSnackbarView, 0);

        TextView textView = customSnackbarView.findViewById(R.id.snackbar_text);
        textView.setText(message);

        ImageView iconView = customSnackbarView.findViewById(R.id.snackbar_icon);
        if (message.contains("No Internet")) {
            iconView.setImageResource(R.drawable.ic_no_internet);
        } else {
            iconView.setImageResource(R.drawable.ic_wifi);
        }

        snackbar.show();
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
