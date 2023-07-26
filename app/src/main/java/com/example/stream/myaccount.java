package com.example.stream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class myaccount extends AppCompatActivity {
    private FirebaseAuth auth;
    private ImageView profileImageView;
    private Button logoutbt,editbt,homebt;
    private TextView nameTextView, bioTextView,useridview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);
        editbt = findViewById(R.id.editbt);
        profileImageView = findViewById(R.id.picimg);
        profileImageView.setClipToOutline(true);
        nameTextView = findViewById(R.id.nameidtx);
        bioTextView = findViewById(R.id.bioidtx);
        useridview = findViewById(R.id.useridtx);
        auth = FirebaseAuth.getInstance();
        logoutbt= findViewById(R.id.Logoutbt);
        homebt = findViewById(R.id.button3);

        homebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myaccount.this, homepage.class);
                startActivity(intent);
            }
        });
        logoutbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(myaccount.this, signin.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Log.d("MyAccountActivity", "User ID: " + userId);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve user data from the dataSnapshot
                        HashMap<String, Object> userData = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (userData != null) {
                            if (dataSnapshot.child(userId).exists()) {

                                DataSnapshot userSnapshot = dataSnapshot.child(userId);
                                String name = (String) userSnapshot.child("name").getValue();
                                String profileUrl = (String) userSnapshot.child("profile").getValue();
                                String bio = (String) userSnapshot.child("bio").getValue();
                                String useridtx = (String) userSnapshot.child("userid").getValue();

                                Log.d("MyAccountActivity67", "User ID: " + name);

                                nameTextView.setText(name);
                                bioTextView.setText(bio);
                                useridview.setText(useridtx);


                                Glide.with(myaccount.this).load(profileUrl).into(profileImageView);
                            } else {
                                Log.d("MyAccountActivity67", "User data not found for this userid: " + userId);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors that may occur
                }
            });
        }
    }
}