package com.example.essen.Activities.AuthActivities.ForgetPassword;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.R;
import com.example.essen.model.AuthService;
import com.example.essen.repository.MealRepository;
import com.example.essen.repository.MealRepositoryImpl;
import com.example.essen.room.AppDatabase;
import com.google.firebase.auth.FirebaseAuth;


public class ForgetPassword extends AppCompatActivity implements AuthViewForgetPassword {
    private EditText emailInput;
    private ForgetPasswordPresenter presenter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_forget_password);
        findviewsById();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        AuthService authService = new AuthService(this);
        MealRepository mealRepository = new MealRepositoryImpl(sharedPreferences, firebaseAuth, appDatabase, authService);
        presenter = new ForgetPasswordPresenter(this, mealRepository);

    }

    public void findviewsById() {
        emailInput = findViewById(R.id.Edit_forgetPassword);
        progressBar = findViewById(R.id.progress_circular);
    }

    public void SendForgetPassword(View view) {
        String email = emailInput.getText().toString();
        if (!email.isEmpty()) {
            presenter.forgetPassword(email);
        } else {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
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
    public void showForgetPasswordSuccess(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showForgetPasswordError(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

}

