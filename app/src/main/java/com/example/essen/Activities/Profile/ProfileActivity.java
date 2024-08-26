package com.example.essen.Activities.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essen.Activities.MainActivity.MainActivity;
import com.example.essen.Activities.Welcome.Welcome_Screen;
import com.example.essen.R;
import com.example.essen.model.AuthService;
import com.example.essen.repository.MealRepository;
import com.example.essen.repository.MealRepositoryImpl;
import com.example.essen.room.AppDatabase;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View {

    private static final String PREFS_NAME = "MyPrefsFile";
    ImageView imageView;
    SwipeRefreshLayout swipeRefreshLayout;
    private AuthService authService;
    private ImageView imageBack;
    private TextView userTitle;
    private TextView userEmail;
    private FirebaseAuth firebaseAuth;
    private MealRepository mealRepository;
    private ProfilePresenter presenter;

    private static Context updateLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        android.content.res.Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }


    private void setLocale(String languageCode) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedLanguage", languageCode);
        editor.apply();

        // Restart activity to apply language change
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mealRepository = new MealRepositoryImpl(getSharedPreferences(PREFS_NAME, MODE_PRIVATE), FirebaseAuth.getInstance(), AppDatabase.getDatabase(this));
        applyLocale();
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_profile);

        imageView = findViewById(R.id.log_out);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        imageBack = findViewById(R.id.image_back);
        userTitle = findViewById(R.id.UserNameValue);
        userEmail = findViewById(R.id.Email_profile_value);


        ConstraintLayout constraintLang = findViewById(R.id.constraint_lang);
        constraintLang.setOnClickListener(v -> showCustomBottomSheet());
        ConstraintLayout constraintinfo = findViewById(R.id.constraint_info);
        constraintinfo.setOnClickListener(v -> showCustomBottomSheetInfo());

        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));
        imageBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        imageView.setOnClickListener(v -> {
                    presenter.signOut();
                    startActivity(new Intent(this, Welcome_Screen.class));
                }
        );


        MealRepository mealRepository = new MealRepositoryImpl(getSharedPreferences(PREFS_NAME, MODE_PRIVATE), FirebaseAuth.getInstance(), AppDatabase.getDatabase(this));
        presenter = new ProfilePresenter(mealRepository, this);


        presenter.loadUserProfile();



    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferences = newBase.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String selectedLanguage = sharedPreferences.getString("selectedLanguage", "en");
        Context context = updateLocale(newBase, selectedLanguage);
        super.attachBaseContext(context);
    }


    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void applyLocale() {
        mealRepository.updateLocale(this, mealRepository.getSelectedLanguage());
    }

    private void showCustomBottomSheetInfo() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_help, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        dialog.show();
    }


    private void showCustomBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_language, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        RadioButton radioEnglish = view.findViewById(R.id.radio_english);
        RadioButton radioArabic = view.findViewById(R.id.radio_arabic);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String selectedLanguage = sharedPreferences.getString("selectedLanguage", "en");

        if ("en".equals(selectedLanguage)) {
            radioEnglish.setChecked(true);
        } else if ("ar".equals(selectedLanguage)) {
            radioArabic.setChecked(true);
        }

        radioEnglish.setOnClickListener(v -> {
            dialog.dismiss();
            setLocale("en");
        });

        radioArabic.setOnClickListener(v -> {
            dialog.dismiss();
            setLocale("ar");
        });

        dialog.show();
    }


    public void Hide_status_Bar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }


    @Override
    public void showUserProfile(String name, String email) {
        userTitle.setText(name);
        userEmail.setText(email);
    }

    @Override
    public void showSignOutSuccess() {
        startActivity(new Intent(this, Welcome_Screen.class));
        finish();
    }

    @Override
    public void showSignOutFailure(String error) {
        showMessage(error);
    }

    @Override
    public void showLanguageChangeDialog(String currentLanguage) {
        showCustomBottomSheet();
    }

    @Override
    public void updateLanguage(String languageCode) {
        setLocale(languageCode);
    }


}



