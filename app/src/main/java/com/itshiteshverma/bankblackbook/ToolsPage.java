package com.itshiteshverma.bankblackbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ToolsPage extends AppCompatActivity {

    public static final String MyPREFERENCES_Due_date = "MyPrefs_due_date";
    EditText NearestDueDatetoDisplay;
    EditText TokenAppId;
    SharedPreferences.Editor editor;
    Button RESET;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference2;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NearestDueDatetoDisplay = findViewById(R.id.etNearestDueDateToDisplay);
        TokenAppId = findViewById(R.id.etAppToknInd_firebase);

        String tkn = FirebaseInstanceId.getInstance().getToken();
        TokenAppId.setText(tkn);

        editor = getSharedPreferences(MyPREFERENCES_Due_date, MODE_PRIVATE).edit();

        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES_Due_date, MODE_PRIVATE);
        int restoredText = prefs.getInt("due_date", 50);
        if (restoredText > 0) {

            int idName = prefs.getInt("due_date", 50); //0 is the default value.
            String idName_tem = String.valueOf(idName);
            NearestDueDatetoDisplay.setText(idName_tem);
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference2 = database.getReference().child("BankBlackBook").child("data").child(user.getUid());


        RESET = findViewById(R.id.bDeleteAllData);
        RESET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(ToolsPage.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover the DATA!")
                        .setConfirmText("Yes,delete it!")
                        .setCancelText("No,cancel it")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                databaseReference2.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ToolsPage.this, "All Data Deleted", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(ToolsPage.this, MainPage.class));
                                    }
                                });
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            }

        });


    }

    public void onSaveSetting(View v) {
        int due_date = Integer.parseInt(NearestDueDatetoDisplay.getText().toString());
        editor.putInt("due_date", due_date);
        editor.commit();
        this.finish();
    }

}
