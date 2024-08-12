package com.example.essen.Activities.AuthActivities.SignUp;

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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.Activities.AuthActivities.Login.Login_Screen;
import com.example.essen.R;
import com.example.essen.Util.NetworkChangeReceiver;
import com.google.android.material.snackbar.Snackbar;

public class SignUp_Screen extends AppCompatActivity implements AuthViewSiginUp {
    private EditText FullName;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPassword;
    private TextView titleTextView;
    private TextView registerTextView;
    private TextView doYouTextView;
    private TextView loginTextView;
    private Button regButton;
    private SignUpPresenter presenter;
    private static final String PREFS_NAME = "MyPrefsFile";
    private ProgressBar progressBar;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private boolean isGuest; // Flag to check if the user is a guest


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_sign_up_screen);
        findViewsById();
        applyFadeInAnimation();
        presenter = new SignUpPresenter(this, this);
        HidePassword();
        HideConfirmPassword();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        isGuest = sharedPreferences.getBoolean("isGuest", true);


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

    private void applyFadeInAnimation() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(2000);


        emailInput.startAnimation(fadeIn);
        passwordInput.startAnimation(fadeIn);
        confirmPassword.startAnimation(fadeIn);
        titleTextView.startAnimation(fadeIn);
        registerTextView.startAnimation(fadeIn);
        doYouTextView.startAnimation(fadeIn);
        loginTextView.startAnimation(fadeIn);
        FullName.startAnimation(fadeIn);
        regButton.startAnimation(fadeIn);
    }

    public void findViewsById() {
        FullName = findViewById(R.id.full_name);
        emailInput = findViewById(R.id.Email);
        passwordInput = findViewById(R.id.password_toggle);
        confirmPassword = findViewById(R.id.password_Confirm);
        progressBar = findViewById(R.id.progress_cir);
        titleTextView = findViewById(R.id.text_reg);
        registerTextView = findViewById(R.id.Register);
        doYouTextView = findViewById(R.id.Login_a);
        loginTextView = findViewById(R.id.Text_Login);
        regButton = findViewById(R.id.button_register);
    }

    public void register_button(View view) {
        String fullName = FullName.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmP = confirmPassword.getText().toString();
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmP.isEmpty()) {
            Snackbar.make(view, "Please fill all fields", Snackbar.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            presenter.signUp(fullName, email, password);
        }

    }
    @Override
    public void showSignUpSuccess(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("isGuest", false);
        editor.apply();
        navigateToLoginScreen();
    }

    @Override
    public void showSignUpError(String message) {
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

    public void login_text(View view) {
        startActivity(new Intent(this, Login_Screen.class));
        finish();
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(this, Login_Screen.class);
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

    @SuppressLint("ClickableViewAccessibility")
    public void HideConfirmPassword() {
        confirmPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (confirmPassword.getRight() - confirmPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (isConfirmPasswordVisible) {
                        confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        confirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.key, 0, R.drawable.hidden, 0);
                        isConfirmPasswordVisible = false;
                    } else {
                        confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        confirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.key, 0, R.drawable.eye_icon, 0);
                        isConfirmPasswordVisible = true;
                    }
                    return true;
                }
            }
            return false;
        });
    }
}
