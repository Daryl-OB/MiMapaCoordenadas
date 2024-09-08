package com.arathdev.mymap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoadStarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_started);

        //Esto hará que se cambie automaticamente a otro activity desde de un tiempo
        // definido, en este caso 4 segundos (4000 ms)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadStarted.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000); // 4000 milisegundos = 4 segundos

    }
}