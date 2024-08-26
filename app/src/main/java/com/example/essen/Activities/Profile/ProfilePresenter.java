package com.example.essen.Activities.Profile;

import com.example.essen.model.AuthService;
import com.example.essen.repository.MealRepository;
import com.google.firebase.auth.FirebaseUser;

public class ProfilePresenter implements ProfileContract.Presenter {
    private MealRepository mealRepository;
    private ProfileContract.View view;

    public ProfilePresenter(MealRepository mealRepository, ProfileContract.View view) {
        this.mealRepository = mealRepository;
        this.view = view;
    }

    @Override
    public void loadUserProfile() {
        FirebaseUser user = mealRepository.getCurrentUser();
        if (user != null) {
            view.showUserProfile(user.getDisplayName(), user.getEmail());
        } else {
            view.showSignOutFailure("User not logged in.");
        }
    }

    @Override
    public void signOut() {
        mealRepository.signOut(new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                view.showSignOutSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.showSignOutFailure(message);
            }
        });
    }


    @Override
    public void changeLanguage(String languageCode) {
        mealRepository.saveSelectedLanguage(languageCode);
        view.updateLanguage(languageCode);
    }

    @Override
    public void onLanguageChangeRequested() {
        String currentLanguage = mealRepository.getSelectedLanguage();
        view.showLanguageChangeDialog(currentLanguage);
    }
}
