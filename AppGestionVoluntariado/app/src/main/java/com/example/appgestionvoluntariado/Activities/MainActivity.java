package com.example.appgestionvoluntariado.Activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appgestionvoluntariado.Fragments.Auth.LoginFragment;
import com.example.appgestionvoluntariado.R;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new LoginFragment())
                    .commit();
        }
    }
}