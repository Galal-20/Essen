package com.example.essen.Activities.AuthActivities.ForgetPassword;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.R;
import com.google.android.material.snackbar.Snackbar;


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
        presenter = new ForgetPasswordPresenter(this, this);
    }

    public void findviewsById() {
        emailInput = findViewById(R.id.Edit_forgetPassword);
        progressBar = findViewById(R.id.progress_circular);
    }

    public void SendForgetPassword(View view) {
        String email = emailInput.getText().toString();
        if (email.isEmpty()) {
            Snackbar.make(view, R.string.Please_fill_all_fields, Snackbar.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            presenter.forgetPassword(email);
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

}