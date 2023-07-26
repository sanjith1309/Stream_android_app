package com.example.stream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class homepage extends AppCompatActivity {
    Button profilebt,newDrop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        profilebt = findViewById(R.id.button1);
        newDrop = findViewById(R.id.button2);
        profilebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(homepage.this,myaccount.class);
                startActivity(i);
                finish();
            }
        });
         newDrop.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent i = new Intent(homepage.this,newdrop.class);
                 startActivity(i);
                 finish();
             }
         });
    }
}