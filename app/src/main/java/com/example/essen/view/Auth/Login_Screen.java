package com.example.essen.view.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.R;
import com.example.essen.presenter.LoginPresenter;
import com.example.essen.view.AuthView;

public class Login_Screen extends AppCompatActivity implements AuthView {
    private EditText emailInput;
    private EditText passwordInput;
    private LoginPresenter presenter;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        presenter = new LoginPresenter(this, this);
    }

    public void Login_button(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        presenter.login(email, password);
    }

    public void LoginGoogle(View view) {
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        // Navigate to main activity
    }

    @Override
    public void showLoginError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSignUpSuccess(String message) {
        // Not needed in Login activity
    }

    @Override
    public void showSignUpError(String message) {
        // Not needed in Login activity
    }

    @Override
    public void showForgetPasswordSuccess(String message) {
        // Not needed in Login activity
    }

    @Override
    public void showForgetPasswordError(String message) {
        // Not needed in Login activity
    }

    public void forgetPassword(View view) {
        startActivity(new Intent(this, ForgetPassword.class));
    }

    public void register_text(View view) {
        startActivity(new Intent(this, SignUp_Screen.class));
    }
}
