package com.example.stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class signin extends AppCompatActivity {

    private TextView typingTextView;
    private String textToType;
    private int currentIndex = 0;
    private static final long DELAY_MS = 100;
    ImageView googlesignin;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        typingTextView = findViewById(R.id.textview);
        googlesignin = (ImageView)findViewById(R.id.imageView);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


        textToType = "WELCOME TO STREAM!";
        startTypingAnimation();


        googlesignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
            }
        });

        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(signin.this,myaccount.class);
            startActivity(intent);
            finish();
        }




    }




    private void startTypingAnimation() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentIndex < textToType.length()) {
                    typingTextView.setText(textToType.substring(0, currentIndex + 1));
                    currentIndex++;
                    handler.postDelayed(this, DELAY_MS);
                }
            }
        };
        handler.postDelayed(runnable, DELAY_MS);
    }

    private void signin(){
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firbaseAuth(account.getIdToken());

            }
            catch (Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firbaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String uid = user.getUid();

                            // Check if the user exists in the database
                            database.getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // User already exists in the database, proceed to myaccount activity
                                        Intent intent = new Intent(signin.this, myaccount.class);
                                        startActivity(intent);
                                    } else {
                                        // User doesn't exist in the database, update data and proceed to myaccount activity
                                        String displayName = user.getDisplayName();
                                        String randomPart = generateRandomString();
                                        String halfDisplayName = displayName.substring(0, displayName.length() / 2);
                                        String userid = halfDisplayName + randomPart;

                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("id", uid);
                                        map.put("name", displayName);
                                        map.put("profile", user.getPhotoUrl().toString());
                                        map.put("bio", "");
                                        map.put("userid", userid);
                                        database.getReference().child("users").child(uid).setValue(map);

                                        Intent intent = new Intent(signin.this, myaccount.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle any potential database error
                                    Toast.makeText(signin.this, "Database error. Try Again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(signin.this, "Something went wrong. Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private String generateRandomString() {
        // Generate a random alphanumeric string of length 8
        int length = 8;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }

        return randomString.toString();
    }


}