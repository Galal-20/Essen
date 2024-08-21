package com.example.essen.Activities.AuthActivities.SignUp;

public interface AuthViewSiginUp {
    void showSignUpSuccess(String message);

    void showSignUpError(String message);

    void checkNetworkStatus(String message);
}
