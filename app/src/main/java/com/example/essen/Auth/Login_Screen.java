package com.example.essen.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.essen.R;

public class Login_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void forgetPassword(View view) {
        startActivity(new Intent(this, ForgetPassword.class));
    }

    public void LoginGoogle(View view) {
        Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
    }

    public void LoginFacebook(View view) {
        Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();

    }

    public void Login_button(View view) {
        Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();

    }

    public void register_text(View view) {
        startActivity(new Intent(this, SignUp_Screen.class));
        finish();
    }

}