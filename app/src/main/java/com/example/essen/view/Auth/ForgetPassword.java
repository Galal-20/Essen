package com.example.essen.view.Auth;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.R;
import com.example.essen.presenter.ForgetPasswordPresenter;
import com.example.essen.view.AuthView;

public class ForgetPassword extends AppCompatActivity implements AuthView {
    private EditText emailInput;
    private ForgetPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        emailInput = findViewById(R.id.Edit_forgetPassword);
        presenter = new ForgetPasswordPresenter(this, this);
    }

    public void SendForgetPassword(View view) {
        String email = emailInput.getText().toString();
        presenter.forgetPassword(email);
    }

    @Override
    public void showLoginSuccess(String message) {

    }

    @Override
    public void showLoginError(String message) {

    }

    @Override
    public void showSignUpSuccess(String message) {

    }

    @Override
    public void showSignUpError(String message) {

    }

    @Override
    public void showForgetPasswordSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        // Navigate to login or main activity
    }

    @Override
    public void showForgetPasswordError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
