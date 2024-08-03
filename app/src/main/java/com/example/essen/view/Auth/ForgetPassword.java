package com.example.essen.view.Auth;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.R;
import com.example.essen.presenter.ForgetPasswordPresenter;


public class ForgetPassword extends AppCompatActivity implements AuthViewForgetPassword {
    private EditText emailInput;
    private ForgetPasswordPresenter presenter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_forget_password);
        emailInput = findViewById(R.id.Edit_forgetPassword);
        progressBar = findViewById(R.id.progress_circular);
        presenter = new ForgetPasswordPresenter(this, this);
    }

    public void SendForgetPassword(View view) {
        String email = emailInput.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        presenter.forgetPassword(email);
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
        // Navigate to login or main activity
    }

    @Override
    public void showForgetPasswordError(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}