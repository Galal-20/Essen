package com.example.essen.Activities.Profile;

public interface ProfileContract {
    interface View {
        void showUserProfile(String name, String email);

        void showSignOutSuccess();

        void showSignOutFailure(String error);

        void showLanguageChangeDialog(String currentLanguage);

        void updateLanguage(String languageCode);
    }

    interface Presenter {
        void loadUserProfile();

        void signOut();

        void changeLanguage(String languageCode);

        void onLanguageChangeRequested();
    }
}
