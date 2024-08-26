package com.example.essen.Activities.AuthActivities.SignUp;

import android.app.Activity;

import com.example.essen.model.AuthService;
import com.example.essen.repository.MealRepository;
import com.example.essen.repository.MealRepositoryImpl;
import com.google.firebase.auth.FirebaseUser;

public class SignUpPresenter {
    private AuthViewSiginUp view;
    private MealRepository mealRepository;

    public SignUpPresenter(AuthViewSiginUp view, Activity activity) {
        this.view = view;
        this.mealRepository = new MealRepositoryImpl(activity);
    }

    public void signUp(String fullName, String email, String password) {
        mealRepository.signUp(fullName, email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                view.showSignUpSuccess("Sign Up Successful");
            }

            @Override
            public void onFailure(String message) {
                view.showSignUpError("Sign Up Failed: " + message);
                view.checkNetworkStatus("No Internet connection");
            }
        });
    }
}


