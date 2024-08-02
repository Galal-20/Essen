package com.example.essen.view.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.R;
import com.example.essen.presenter.SignUpPresenter;
import com.example.essen.view.AuthView;

public class SignUp_Screen extends AppCompatActivity implements AuthView {
    private EditText FullName;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPassword;
    private SignUpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        FullName = findViewById(R.id.full_name);
        emailInput = findViewById(R.id.Email);
        passwordInput = findViewById(R.id.password_toggle);
        confirmPassword = findViewById(R.id.password_Confirm);
        presenter = new SignUpPresenter(this, this);
    }

    public void register_button(View view) {
        String fullName = FullName.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmP = confirmPassword.getText().toString();
        presenter.signUp(fullName , email, password);
    }

    @Override
    public void showLoginSuccess(String message) {

    }

    @Override
    public void showLoginError(String message) {

    }

    @Override
    public void showSignUpSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        // Navigate to login or main activity
    }

    @Override
    public void showSignUpError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showForgetPasswordSuccess(String message) {

    }

    @Override
    public void showForgetPasswordError(String message) {

    }

    public void login_text(View view) {
        startActivity(new Intent(this, Login_Screen.class));
    }
}
