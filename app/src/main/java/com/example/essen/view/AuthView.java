package com.example.essen.view;

public interface AuthView {
    void showLoginSuccess(String message);
    void showLoginError(String message);
    void showSignUpSuccess(String message);
    void showSignUpError(String message);
    void showForgetPasswordSuccess(String message);
    void showForgetPasswordError(String message);
}
