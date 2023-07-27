package com.example.stream;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class editprofile extends AppCompatActivity {
    private ProgressBar progressBar;
    private static final int PROGRESS_DELAY = 5000;
    EditText nameTextView,bioTextView,useridview;
    Button updatebt;
    private ImageView profileImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        nameTextView = findViewById(R.id.nameinput);
        bioTextView = findViewById(R.id.bioinput);
        useridview = findViewById(R.id.useridinput);
        updatebt = findViewById(R.id.updatebt);
        profileImageView = findViewById(R.id.picimg);
        retrive();
        updatebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkandupdate();
            }
        });

    }

    private void checkandupdate() {
        String name = nameTextView.getText().toString();
        String bio = bioTextView.getText().toString();
        String userid = useridview.getText().toString();
        if (name.isEmpty()){
            nameTextView.setError("Please Enter Your Name");
            nameTextView.requestFocus();
        }
        else if (userid.isEmpty()){
            useridview.setError("Create a user id");
            useridview.requestFocus();
        }
        else if(userid.length()>20){
            useridview.setError("the length should be less than 25 char");
            useridview.requestFocus();
        }
        else if(nameTextView.length()>20){
            nameTextView.setError("the length should be less than 25 char");
            nameTextView.requestFocus();
        }
        else if(bio.length()>60){
            bioTextView.setError("The number of char should be less than 60");
            bioTextView.requestFocus();
        }
        else if(!userid.isEmpty()){
            if (isValidName(userid)) {
                useridview.setError(null);
            } else {
                useridview.setError("Invalid name. Only alphabets and integers are allowed.");
                useridview.requestFocus();
            }
        }




    }


    private void retrive() {
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


                                Glide.with(editprofile.this).load(profileUrl).into(profileImageView);
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
    private boolean isValidName(String name) {
        String regex = "^[a-zA-Z0-9]+$";
        return name.matches(regex);
    }


}