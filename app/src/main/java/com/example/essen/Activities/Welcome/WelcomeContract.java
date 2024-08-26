package com.example.essen.Activities.Welcome;

public interface WelcomeContract {
    interface View {
        void navigateToMainActivity();

        void navigateToSignUp();

        void navigateToLogin();
    }

    interface Presenter {
        void checkLoginStatus();

        void setGuestMode();

        void register();

        void login();
    }
}

