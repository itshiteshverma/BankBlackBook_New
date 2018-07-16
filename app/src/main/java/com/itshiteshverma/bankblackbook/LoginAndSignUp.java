package com.itshiteshverma.bankblackbook;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginAndSignUp extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    String TAG = "TAG";
    SweetAlertDialog pDialog;
    LinearLayout LLReset;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseReference_User;
    //private StorageReference firebaseStorage;
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private EditText inputEmail, inputPassword, resetEmail;
    private Button btnSignUp, btnResetPassword, btnReset;
    private TextView Help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_sign_up);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        pDialog = new SweetAlertDialog(LoginAndSignUp.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(true);

        signInButton = findViewById(R.id.glogin);

        FirebaseApp.initializeApp(this);

        databaseReference_User = FirebaseDatabase.getInstance().getReference().child("BankBlackBook").child("users");
        databaseReference_User.keepSynced(true); //this will its stores the data offline

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginAndSignUp.this, MainPage.class));
                }
            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).
                enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginAndSignUp.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        btnReset = findViewById(R.id.btn_reset);
        LLReset = findViewById(R.id.llpasword_reset);
        resetEmail = findViewById(R.id.etrestEmail);
        Help = findViewById(R.id.tvHelp);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLReset.setVisibility(View.VISIBLE);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = resetEmail.getText().toString().trim();

                final SweetAlertDialog pDialog1 = new SweetAlertDialog(LoginAndSignUp.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog1.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog1.setTitleText("Re-setting Password");
                pDialog1.setCancelable(false);

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter Email ID!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    new SweetAlertDialog(LoginAndSignUp.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Instructions sent")
                                            .setContentText("Check Your Email")
                                            .show();

                                } else {
                                    //  Toast.makeText(LoginAndSignUp.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    new SweetAlertDialog(LoginAndSignUp.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Failed to send reset email!")
                                            .show();
                                }


                            }
                        });
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 3) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                pDialog.show();
                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginAndSignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Toast.makeText(LoginAndSignUp.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_LONG).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    mAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(LoginAndSignUp.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    // If sign in fails, display a message to the user. If sign in succeeds
                                                    // the auth state listener will be notified and logic to handle the
                                                    // signed in user can be handled in the listener.

                                                    if (!task.isSuccessful()) {
                                                        // there was an error
                                                        new SweetAlertDialog(LoginAndSignUp.this, SweetAlertDialog.ERROR_TYPE)
                                                                .setTitleText("Login Failed!")
                                                                .setContentText("Try Again")
                                                                .show();

                                                    } else {
                                                        Intent intent = new Intent(LoginAndSignUp.this, SplashScreen.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });

                                    pDialog.dismissWithAnimation();

                                } else {
                                    startActivity(new Intent(LoginAndSignUp.this, SplashScreen.class));
                                    finish();
                                    pDialog.dismissWithAnimation();
                                }
                            }
                        });

            }
        });

        Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(LoginAndSignUp.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Hitesh Verma")
                        .setContentText("Developer And Designer")
                        .setCustomImage(R.drawable.love)
                        .setConfirmText("Contact Me")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
//
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hiteshverma.typeform.com/to/zuEb4G"));
                                startActivity(browserIntent);

                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
        //updateUI(currentUser);
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        pDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SimpleDateFormat s = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                            String format = s.format(new Date());

                            final FirebaseUser user = mAuth.getCurrentUser();
                            final DatabaseReference current_user = databaseReference_User.child(user.getUid());

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            current_user.child("image").setValue(user.getPhotoUrl().toString());
                            current_user.child("name").setValue(user.getDisplayName());
                            current_user.child("email").setValue(user.getEmail());
                            current_user.child("time_stamp").setValue(format);
                            current_user.child("appId").setValue(FirebaseInstanceId.getInstance().getToken().toString());
                            Toast.makeText(LoginAndSignUp.this, "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginAndSignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            pDialog.dismiss();
                        }

                        // ...
                    }
                });
    }

}
