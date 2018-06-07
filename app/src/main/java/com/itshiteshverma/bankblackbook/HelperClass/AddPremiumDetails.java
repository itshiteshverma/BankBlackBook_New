package com.itshiteshverma.bankblackbook.HelperClass;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itshiteshverma.bankblackbook.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.itshiteshverma.bankblackbook.OneFragment.DataID;

public class AddPremiumDetails extends AppCompatActivity {

    Button premiumDetailsButton;
    String gotPremiumDate;
    MaterialEditText ReciptNo, AmountPaid, Remarks;
    RadioButton radioButton;
    TextView title;
    private RadioGroup radioGroup;
    private DatabaseReference dataBaseReference;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_premium_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initilize();

        user = FirebaseAuth.getInstance().getCurrentUser();
        dataBaseReference = FirebaseDatabase.getInstance().getReference().child("BankBlackBook").child("data")
                .child(user.getUid()).child(DataID);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initilize() {
        premiumDetailsButton = findViewById(R.id.bLastPremiumDate);
        ReciptNo = findViewById(R.id.etReciptNo);
        AmountPaid = findViewById(R.id.etAmountPaid);
        Remarks = findViewById(R.id.etRemarks);
        radioGroup = findViewById(R.id.radioGroupModeofPayment);
        title = findViewById(R.id.tvTitle);

        title.setText("Policy Name: " + getIntent().getStringExtra("POLICY_NAME"));

    }

    public void onADDClick(View v) {
        try {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(selectedId);

            String reciptnumber = ReciptNo.getText().toString().trim();
            String amountpaid = AmountPaid.getText().toString().trim();
            String remarks = Remarks.getText().toString().trim();

            SimpleDateFormat s = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String format = s.format(new Date());

            //String key = dataBaseReference.push().getKey(); // this will create a new unique key
            String key = dataBaseReference.push().getKey();
            final Map<String, Object> value = new HashMap<>();
            value.put("recipt_no", reciptnumber);
            value.put("remarks", remarks);
            value.put("amount_paid", amountpaid);
            value.put("mode_of_payment", radioButton.getText().toString()); //remain this
            value.put("timestamp", format);
            value.put("premium_date", gotPremiumDate);
            SimpleDateFormat formatTest = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = null;

            date1 = formatTest.parse(gotPremiumDate);
            value.put("premium_date_detail", date1);


            //
            dataBaseReference.child("premium_data").child(key).setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddPremiumDetails.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    //startActivity(new Intent(AfterReplyClick.this, ReplyQuotations.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddPremiumDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(AfterReplyClick.this, ReplyQuotations.class));
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public void onPremiumDateDetailsClick(View v) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
                cal.set(Calendar.MONTH, monthOfYear);
                String month_name = dateFormat.format(cal.getTime());
                String Date = dayOfMonth + " " + month_name + " " + year;

                premiumDetailsButton.setText(Date);

                int month_temp = monthOfYear + 1;
                String month_text = String.valueOf(month_temp);

                //This is to Store the date in a lexicalogicalOrder
                String day = String.valueOf(dayOfMonth);
                if (month_temp < 10) {

                    month_text = "0" + month_temp;
                }
                if (dayOfMonth < 10) {

                    day = "0" + dayOfMonth;
                }
                gotPremiumDate = year + "-" + month_text + "-" + day;
                //Toast.makeText(AddPremiumDetails.this, "Date:" + gotPremiumDate, Toast.LENGTH_SHORT).show();

            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }
}
