package com.example.essen.Activities.MainActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essen.Activities.AuthActivities.Login.Login_Screen;
import com.example.essen.Fragments.CountriesFragment.CountriesFragment;
import com.example.essen.Fragments.FavoritFragment.FavoritFragment;
import com.example.essen.Fragments.HomeFragment.HomeFragment;
import com.example.essen.Fragments.MealPlanFragment.MealPlanFragment;
import com.example.essen.Fragments.SearchFragment.SearchFragment;
import com.example.essen.R;
import com.example.essen.Util.NetworkChangeReceiver;
import com.example.essen.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {

    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isConnected;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean wasDisconnected = false;
    private boolean isGuest;


    private ActivityResultLauncher<String> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getDeviceToken();
                } else {
                }
            }
    );

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        isConnected = NetworkChangeReceiver.isConnectedToInternet(this);
        checkNetworkStatus();

        requestPermission();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        isGuest = sharedPreferences.getBoolean("isGuest", true);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                if (currentFragment != null) {
                    replaceFragment(currentFragment);
                }
                swipeRefreshLayout.setRefreshing(false);
            });
        } else {
            Log.e(TAG, "SwipeRefreshLayout is null. Check the XML layout file.");
        }

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            binding.bottomNavigationView.setSelectedItemId(R.id.homeMenu);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d(TAG, "Selected item ID: " + itemId);
            if (itemId == R.id.homeMenu) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.searchMenu) {
                replaceFragment(new SearchFragment());
            } else if (itemId == R.id.favMenu) {
                if (isGuest) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.Please_login_to_access_your_favorites);
                    builder.setMessage(R.string.Are_you_want_to_join_with_us);

                    builder.setPositiveButton(R.string.Go_to_login, (dialog, which) -> {
                        startActivity(new Intent(getApplicationContext(), Login_Screen.class));
                        finish();
                        dialog.dismiss();
                    });

                    builder.setNegativeButton(R.string.Still_Guest, (dialog, which) -> {
                        dialog.dismiss();
                        replaceFragment(new HomeFragment());
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    replaceFragment(new FavoritFragment());
                }


            } else if (itemId == R.id.countries) {
                replaceFragment(new CountriesFragment());

            } else if (itemId == R.id.meal_plan) {
                if (isGuest) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.Please_login_to_show_meal_plan);
                    builder.setMessage(R.string.Are_you_want_to_join_with_us);

                    builder.setPositiveButton(R.string.Go_to_login, (dialog, which) -> {
                        startActivity(new Intent(getApplicationContext(), Login_Screen.class));
                        finish();
                        dialog.dismiss();
                    });

                    builder.setNegativeButton(R.string.Still_Guest, (dialog, which) -> {
                        dialog.dismiss();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    replaceFragment(new MealPlanFragment());
                }

            }
            return true;
        });
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            getDeviceToken();
        }
    }

    
    public void getDeviceToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.v("FirebaseMessaging", "Token: " + token);
            } else {
                Log.e("FirebaseMessaging", "Failed to fetch token", task.getException());
            }
        });
    }


    private void replaceFragment(Fragment fragment) {
        Log.d(TAG, "Replacing fragment with: " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        networkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {

        if (isConnected) {
            if (wasDisconnected) {
                showCustomSnackbar("Back to connection", Snackbar.LENGTH_SHORT);
                wasDisconnected = false;
            }
        } else {
            showCustomSnackbar("Internet is not available", Snackbar.LENGTH_SHORT);
            wasDisconnected = true;
        }
    }

    private void checkNetworkStatus() {
        if (isConnected) {
            showCustomSnackbar("Connected to the Internet", Snackbar.LENGTH_SHORT);
        } else {
            showCustomSnackbar("No Internet connection", Snackbar.LENGTH_INDEFINITE);
        }
    }


    private void showCustomSnackbar(String message, int duration) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "", duration);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customSnackbarView = inflater.inflate(R.layout.snackbar_custom, null);
        snackbarLayout.addView(customSnackbarView, 0);
        TextView textView = customSnackbarView.findViewById(R.id.snackbar_text);
        textView.setText(message);
        ImageView iconView = customSnackbarView.findViewById(R.id.snackbar_icon);
        if (message.contains("Internet is not available")) {
            iconView.setImageResource(R.drawable.ic_no_internet);
        } else {
            iconView.setImageResource(R.drawable.ic_wifi);
        }

        snackbar.show();
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

}

