package com.example.essen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essen.model.AuthService;
import com.example.essen.view.Auth.Welcome_Screen;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    Button signOutButton;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_status_Bar();
        setContentView(R.layout.activity_main);

        authService = new AuthService(this);
        signOutButton = findViewById(R.id.SignOut);


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

    public void signOut(View view) {

        authService.signOut(this, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Intent intent = new Intent(MainActivity.this, Welcome_Screen.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, "check your internet", Toast.LENGTH_SHORT).show();
            }


        });


    }
}