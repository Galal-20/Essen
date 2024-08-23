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
import com.example.essen.room.AppDatabase;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    ImageView imageView;
    SwipeRefreshLayout swipeRefreshLayout;
    private AuthService authService;
    private ImageView imageBack;
    private TextView userTitle;
    private TextView userEmail;
    private FirebaseAuth firebaseAuth;

    private static Context updateLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        android.content.res.Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
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

        // get current selected language from SharedPreferences
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


    public void signOut() {
        authService.signOut(this, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                showMessage("Sign out Successfully");
                clearLocalFavorites();
                Intent intent = new Intent(ProfileActivity.this, Welcome_Screen.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                showMessage("Check your internet connection");
            }
        });
    }


    private void clearLocalFavorites() {
        // Clear favorite data from local database if needed
        AppDatabase database = AppDatabase.getDatabase(this);
        new Thread(() -> {
            database.mealDao().deleteAllFavorites(); // Assuming you have a method to delete all favorites
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        firebaseAuth = FirebaseAuth.getInstance();
        authService = new AuthService(this);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userTitle.setText(currentUser.getDisplayName());
            userEmail.setText(currentUser.getEmail());
        }
        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));
        imageView.setOnClickListener(v -> signOut());
        imageBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });


    }

    // Updated setLocale method to change language and restart activity
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

    // Apply locale before activity initialization
    private void applyLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String selectedLanguage = sharedPreferences.getString("selectedLanguage", "ar");
        updateLocale(this, selectedLanguage);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferences = newBase.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String selectedLanguage = sharedPreferences.getString("selectedLanguage", "ar");
        Context context = updateLocale(newBase, selectedLanguage);
        super.attachBaseContext(context);
    }


    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}





  /* private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        SecurePreferences securePreferences = new SecurePreferences(this, PREFS_NAME, "1234567", true);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedLanguage", languageCode);
        editor.apply();
        securePreferences.put("selectedLanguage", "true");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }*/

 /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        savedLanguage = sharedPreferences.getString("selectedLanguage", "Select Language");

        if (savedLanguage.equals("English")) {
            languageSpinner.setSelection(adapter.getPosition("English"));
        } else if (savedLanguage.equals("Arabic")) {
            languageSpinner.setSelection(adapter.getPosition("Arabic"));
        } else {
            languageSpinner.setSelection(adapter.getPosition("System"));
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = (String) parent.getItemAtPosition(position);
                if (selectedLanguage.equals("English")) {
                    setLocale("en");
                } else if (selectedLanguage.equals("Arabic")) {
                    setLocale("ar");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/