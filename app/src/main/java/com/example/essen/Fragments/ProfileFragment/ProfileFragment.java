package com.example.essen.Fragments.ProfileFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essen.Activities.Welcome.Welcome_Screen;
import com.example.essen.R;
import com.example.essen.Util.SecurePreferences;
import com.example.essen.model.AuthService;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class ProfileFragment extends Fragment {

    private static final String PREFS_NAME = "MyPrefsFile";
    ImageView imageView;
    SwipeRefreshLayout swipeRefreshLayout;
    private AuthService authService;
    private Spinner languageSpinner;
    String savedLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authService = new AuthService(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageView = view.findViewById(R.id.log_out);
        languageSpinner = view.findViewById(R.id.language_spinner);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
        });

        if (languageSpinner == null) {
            throw new NullPointerException("languageSpinner is null");
        }

        imageView.setOnClickListener(v -> {
            signOut();
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);


        // Set the spinner to show the saved language choice
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
                    languageSpinner.getSelectedItem();
                } else if (selectedLanguage.equals("Arabic")) {
                    setLocale("ar");
                    languageSpinner.getSelectedItem();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    public void signOut() {
        authService.signOut(requireActivity(), new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                showMessage("Sign out Successfully");
                Intent intent = new Intent(getContext(), Welcome_Screen.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(String message) {
                showMessage("Check your internet connection");
            }
        });
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        SecurePreferences securePreferences = new SecurePreferences(getContext(), PREFS_NAME, "1234567", true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedLanguage", languageCode);
        editor.apply();
        securePreferences.put("selectedLanguage", "true");

        // Restart the activity to apply the new locale
        Intent intent = new Intent(getActivity(), getActivity().getClass());
        startActivity(intent);
        getActivity().finish();
    }


    private void showMessage(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
    }
}

/* ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);*/
