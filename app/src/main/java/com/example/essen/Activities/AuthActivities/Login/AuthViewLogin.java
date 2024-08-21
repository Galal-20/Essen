package com.example.essen.Activities.AuthActivities.Login;


public interface AuthViewLogin {
    void showLoginSuccess(String message);
    void showLoginError(String message);

    void checkNetworkStatus(String message);
}
