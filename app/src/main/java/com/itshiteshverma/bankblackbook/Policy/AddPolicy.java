package com.itshiteshverma.bankblackbook.Policy;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itshiteshverma.bankblackbook.HelperClass.CustomAutoCompleteView;
import com.itshiteshverma.bankblackbook.HelperClass.DatabaseHandler_AutoComplete;
import com.itshiteshverma.bankblackbook.HelperClass.MyObject;
import com.itshiteshverma.bankblackbook.MainPage;
import com.itshiteshverma.bankblackbook.Policy.AutoCompleteText.CustomAutoCompleteTextChangedListener_BankName;
import com.itshiteshverma.bankblackbook.Policy.AutoCompleteText.CustomAutoCompleteTextChangedListener_Name;
import com.itshiteshverma.bankblackbook.Policy.AutoCompleteText.CustomAutoCompleteTextChangedListener_Nominee;
import com.itshiteshverma.bankblackbook.R;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.joda.time.DateTime;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.itshiteshverma.bankblackbook.OneFragment.DataID;

public class AddPolicy extends AppCompatActivity implements IPickResult {

    private static final int REUEST_CODE = 1, PDF_REQUEST_CODE = 2;
    final String DB_BankName = "Bank_Name";
    final String DB_User_Name = "User_Name";
    final String DB_AccountNo = "AccountNo";
    public CustomAutoCompleteView BankName, NameOfFD_Holder, Nominee;
    // adapter for auto-complete
    public ArrayAdapter<String> myAdapter_BankName, myAdapter_Name, myAdapter_Nominee;
    // just to add some initial value
    public String[] item = new String[]{"Please search..."};
    MaterialEditText PolicyName, PolicyNumber, GuranteedMaturedSum, Remarks, InitialSum, PremiumSum;
    Button dueDate, maturityDate, startDate, bLastPremiumDate;
    String DueDate, Due_Day_ofthe_Year;
    String gotMaturityDate, gotStartDate, gotLastPremiumDate;
    LinearLayout LayoutUpdate;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataBaseReference;
    StorageReference mStorageReference;
    Uri image_uri = null;
    ImageView Image;
    SweetAlertDialog pDialog;
    SimpleDateFormat s;
    Button PolicyPhoto, PolicyPdf;
    // for database operations
    DatabaseHandler_AutoComplete dB_BankName, db_Name, db_AccountNumber;
    String PDF_filePath = null;
    String timeStamp;

    String ImageURIString = "NO", PDF_URI_String = "NO";

    private RadioGroup radioGroup;
    private RadioButton radioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_policy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initilise();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dataBaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        chekForPolicyUpdate();

        // Toast.makeText(this, "Last :" + gotLastPremiumDate, Toast.LENGTH_SHORT).show();

    }

    private void chekForPolicyUpdate() {
        if (getIntent().getStringExtra("Update").equals("True")) {
            Intent i = getIntent();
            PolicyNumber.setText(i.getStringExtra("policy_number"));
            PolicyName.setText(i.getStringExtra("policy_name"));
            BankName.setText(i.getStringExtra("bank"));
            PremiumSum.setText(i.getStringExtra("premium_amount"));
            GuranteedMaturedSum.setText(i.getStringExtra("matured_amount"));
            NameOfFD_Holder.setText(i.getStringExtra("etNameofFDHolder"));
            Nominee.setText(i.getStringExtra("nominee"));
            Remarks.setText(i.getStringExtra("remarks"));
            InitialSum.setText(i.getStringExtra("initial_amount"));
            startDate.setText(i.getStringExtra("start_date"));
            maturityDate.setText(i.getStringExtra("end_date"));
            dueDate.setText(i.getStringExtra("due_date"));
            LayoutUpdate.setVisibility(View.GONE);

        } else {
            LayoutUpdate.setVisibility(View.VISIBLE);
        }
    }

    private void initilise() {
        PolicyName = findViewById(R.id.etPolicyName);
        PolicyNumber = findViewById(R.id.etPolicyNumber);
        BankName = findViewById(R.id.etBank);
        NameOfFD_Holder = findViewById(R.id.etNameofFDHolder);
        GuranteedMaturedSum = findViewById(R.id.etGauranteedMaturedSum);
        PremiumSum = findViewById(R.id.etPremiumSum);
        InitialSum = findViewById(R.id.etInitialSum);
        dueDate = findViewById(R.id.bDueDate);
        maturityDate = findViewById(R.id.bMaturityDate);
        bLastPremiumDate = findViewById(R.id.bLastPremiumDate);
        startDate = findViewById(R.id.bStartDate);
        Nominee = findViewById(R.id.etFDNominee);
        Remarks = findViewById(R.id.etRemarks);
        PolicyPhoto = findViewById(R.id.imageButtonAddPolicy);
        LayoutUpdate = findViewById(R.id.LayourUpdateHide);
        PolicyPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.setType("image/*");
//                startActivityForResult(i, REUEST_CODE);
                PickImageDialog.build(new PickSetup()).show(AddPolicy.this);

            }
        });

        radioGroup = findViewById(R.id.radioGroup);

        Image = findViewById(R.id.imageViewPhotoPolicy);

        Image.setOnTouchListener(new ImageMatrixTouchHandler(AddPolicy.this));


        PolicyPdf = findViewById(R.id.imageButtonAddFD_PDF);
        PolicyPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(AddPolicy.this)
                        .withRequestCode(PDF_REQUEST_CODE)
                        .withTitle("Policy & FD Manager")
                        .withCloseMenu(true)
                        .withRootPath(String.valueOf(Environment.getExternalStorageDirectory()))
                        .start();
            }
        });

        try {

            // instantiate database handler
            dB_BankName = new DatabaseHandler_AutoComplete(AddPolicy.this, DB_BankName);
            db_Name = new DatabaseHandler_AutoComplete(AddPolicy.this, DB_User_Name);
            db_AccountNumber = new DatabaseHandler_AutoComplete(AddPolicy.this, DB_AccountNo);
            // put sample data to database
            // autocompletetextview is in activity_main.xml

            // add the listener so it will tries to suggest while the user types
            BankName.addTextChangedListener(new CustomAutoCompleteTextChangedListener_BankName(this));
            myAdapter_BankName = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
            BankName.setAdapter(myAdapter_BankName);

            NameOfFD_Holder.addTextChangedListener(new CustomAutoCompleteTextChangedListener_Name(this));
            myAdapter_Name = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
            NameOfFD_Holder.setAdapter(myAdapter_Name);

            Nominee.addTextChangedListener(new CustomAutoCompleteTextChangedListener_Nominee(this));
            myAdapter_Nominee = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
            Nominee.setAdapter(myAdapter_Nominee);


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // this function is used in CustomAutoCompleteTextChangedListener_BankName.java
    public String[] get_BankName_FromDb(String searchTerm) {

        // add items on the array dynamically
        List<MyObject> products = dB_BankName.read(searchTerm);
        int rowCount = products.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (MyObject record : products) {

            item[x] = record.objectValue;
            x++;
        }

        return item;
    }

    public String[] get_Name_FromDb(String searchTerm) {

        // add items on the array dynamically
        List<MyObject> products = db_Name.read(searchTerm);
        int rowCount = products.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (MyObject record : products) {

            item[x] = record.objectValue;
            x++;
        }

        return item;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resulteCode, Intent intent) {
        super.onActivityResult(requestCode, resulteCode, intent);

        if (requestCode == REUEST_CODE && resulteCode == Activity.RESULT_OK) {
            Uri selectedImageUri = intent.getData();
            PolicyPhoto.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_green_24dp, 0);

            Image.setImageURI(selectedImageUri);

        }

        if (requestCode == PDF_REQUEST_CODE && resulteCode == RESULT_OK) {
            PDF_filePath = intent.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file
            PolicyPdf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_green_24dp, 0, 0, 0);
            //  Toast.makeText(this, "File Path: " + PDF_filePath, Toast.LENGTH_SHORT).show();

        }
    }


    public void onDoneClick(View v) {
        pDialog = new SweetAlertDialog(AddPolicy.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Uploading Data");
        pDialog.setCancelable(true);
        pDialog.show();

        SimpleDateFormat s = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        timeStamp = s.format(new Date());
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Uri file_uri = null;
        if (PDF_filePath != null) {
            file_uri = Uri.fromFile(new File(PDF_filePath));
        }
        //Check if  its an Upadate
        if (getIntent().getStringExtra("Update").equals("True")) {

            if (image_uri != null) {
                StorageReference filePath = mStorageReference.child("Bank_Black_Book_Images").child(mAuth.getUid() + "_" + timeStamp);
                filePath.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//
//                        UpdateData(downloadUrl.toString());
                    }
                });

            } else {
                UpdateData("NO");
            }


        } else { // Not an Update
            if (image_uri != null) {  //Image is Present
                final StorageReference filePath = mStorageReference.child("Bank_Black_Book_Images").child(mAuth.getUid() + "_" + timeStamp);
                final Uri finalFile = file_uri;
                filePath.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ImageURIString = uri.toString();

                                if (finalFile != null) {  // Image as well as File is Present
                                    final StorageReference filePath2 = mStorageReference.child("Bank_Black_Book_Images").child(mAuth.getUid() + "_" + timeStamp + "_PDF");
                                    filePath2.putFile(finalFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//
                                            filePath2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    PDF_URI_String = uri.toString();
                                                    saveData(ImageURIString, PDF_URI_String);
                                                }
                                            });
//
                                        }
                                    });

                                } else { // Image but Not PDF
                                    saveData(ImageURIString, PDF_URI_String);
                                }
                            }
                        });


                    }
                });
            } else if (file_uri != null) {  // when we are saving only PDF no image
                final StorageReference filePath2 = mStorageReference.child("Bank_Black_Book_Images").child(mAuth.getUid() + "_" + timeStamp + "_PDF");
                filePath2.putFile(file_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        //  Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        filePath2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Toast.makeText(AddPolicy.this, "uri: " + uri.toString(), Toast.LENGTH_SHORT).show();
                                PDF_URI_String = uri.toString();
                                saveData(ImageURIString, PDF_URI_String);
                            }
                        });

                        //
                    }
                });
            } else { // No PDF , NO Image
                saveData(ImageURIString, PDF_URI_String);
            }

        }

    }


    private void UpdateData(String imageURL) {
        try {
            final String Policy_Name = PolicyName.getText().toString().trim();
            final String Policy_Number = PolicyNumber.getText().toString().trim();
            final String Bank_Name = BankName.getText().toString().trim();
            final String NameOf_PolicyHolder = NameOfFD_Holder.getText().toString().trim();
            if (!TextUtils.isEmpty(Policy_Number) && !TextUtils.isEmpty(Policy_Name) &&
                    !TextUtils.isEmpty(Bank_Name) && !TextUtils.isEmpty(NameOf_PolicyHolder)
                    ) {

                Map newUserData = new HashMap();
                newUserData.put("policy_number", PolicyNumber.getText().toString().trim());
                newUserData.put("policy_name", PolicyName.getText().toString().trim());
                newUserData.put("bank_name", BankName.getText().toString().trim());
                newUserData.put("nameof_policy_holder", NameOfFD_Holder.getText().toString().trim());
                newUserData.put("guranteed_matured_sum", GuranteedMaturedSum.getText().toString().trim());
                newUserData.put("nominee", Nominee.getText().toString().trim());
                newUserData.put("remarks", Remarks.getText().toString().trim());
                newUserData.put("initial_amount", InitialSum.getText().toString().trim());
                newUserData.put("premium_amount", PremiumSum.getText().toString().trim());
                newUserData.put("timestamp", timeStamp);
                newUserData.put("premium_amount", PremiumSum.getText().toString().trim());
                dataBaseReference.child(DataID).updateChildren(newUserData).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        pDialog.dismissWithAnimation();
                        Toast.makeText(AddPolicy.this, "Policy Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddPolicy.this, PolicyDetails.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialog.dismissWithAnimation();
                        new SweetAlertDialog(AddPolicy.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Please Check Your Internet Connection")
                                .show();
                    }
                });
            } else {
                pDialog.dismissWithAnimation();
                new SweetAlertDialog(AddPolicy.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Please Enter All the Required Data")
                        .show();
            }


        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
    }


    private void saveData(String downloadUrl, String PDFDownloadURL) {

        // when the post button is clicked
        final String Policy_Name = PolicyName.getText().toString().trim();
        final String Policy_Number = PolicyNumber.getText().toString().trim();
        final String Bank_Name = BankName.getText().toString().trim();
        final String NameOf_PolicyHolder = NameOfFD_Holder.getText().toString().trim();
        final String Guranteed_Matured_Sum = GuranteedMaturedSum.getText().toString().trim();
        final String Nominee_data = Nominee.getText().toString().trim();
        final String Remarks_data = Remarks.getText().toString().trim();
        final String premium_amount = PremiumSum.getText().toString().trim();
        final String initial_amount = InitialSum.getText().toString().trim();
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);


        if (!TextUtils.isEmpty(Policy_Number) && !TextUtils.isEmpty(Policy_Name) &&
                !TextUtils.isEmpty(Bank_Name) && !TextUtils.isEmpty(NameOf_PolicyHolder)
                && !TextUtils.isEmpty(gotStartDate)
                && !TextUtils.isEmpty(gotMaturityDate)
                && !TextUtils.isEmpty(DueDate)
                && !TextUtils.isEmpty(Bank_Name)) {


            DateTime day_ofthe_year = new DateTime(Due_Day_ofthe_Year);
            String key = dataBaseReference.push().getKey(); // this will create a new unique key
            Map<String, Object> value = new HashMap<>();
            value.put("type", "POLICY");
            value.put("policy_name", Policy_Name);
            value.put("policy_number", Policy_Number);
            value.put("bank_name", Bank_Name);
            value.put("nameof_policy_holder", NameOf_PolicyHolder);
            value.put("guranteed_matured_sum", Guranteed_Matured_Sum);
            value.put("due_date", DueDate);
            value.put("due_day_ofthe_year", String.valueOf(day_ofthe_year.getDayOfYear()));
            value.put("maturity_date", gotMaturityDate);

            if (gotLastPremiumDate != null && !gotLastPremiumDate.isEmpty()) { /* do your stuffs here */
                value.put("last_premium_date", gotLastPremiumDate);
            }
            value.put("start_date", gotStartDate);
            value.put("timestamp", timeStamp);
            value.put("nominee", Nominee_data);
            value.put("remarks", Remarks_data);
            value.put("image", (downloadUrl != null) ? downloadUrl : "NO");
            value.put("PDF", (PDFDownloadURL != null) ? PDFDownloadURL : "NO");
            value.put("initial_amount", initial_amount);
            value.put("premium_amount", premium_amount);
            value.put("status", "ACTIVE");
            value.put("premium_payment_mode", radioButton.getText());


            SimpleDateFormat formatTest = new SimpleDateFormat("yyyy-MM-dd");

            Date date1, date2, date4 = null, date3 = null;

            try {
                date1 = formatTest.parse(gotMaturityDate);
                date2 = formatTest.parse(timeStamp);
                date3 = formatTest.parse(gotStartDate);
                if (gotLastPremiumDate != null && !gotLastPremiumDate.isEmpty()) { /* do your stuffs here */
                    date4 = formatTest.parse(gotLastPremiumDate);
                }


                value.put("maturity_date_detail", date1);
                value.put("timestamp_date_detail", date2);
                value.put("initial_date_detail", date3);

                if (gotLastPremiumDate != null && !gotLastPremiumDate.isEmpty()) { /* do your stuffs here */
                    value.put("last_premium_date_detail", date4);
                }


                saveDatatoDB(Bank_Name, NameOf_PolicyHolder, Nominee_data);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            dataBaseReference.child(key).setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    pDialog.dismiss();
                    Toast.makeText(AddPolicy.this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddPolicy.this, MainPage.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pDialog.dismiss();
                    //Toast.makeText(AddPolicy.this, "Data Didnt Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddPolicy.this, MainPage.class));
                }
            });

        } else

        {
            pDialog.dismiss();
            new SweetAlertDialog(AddPolicy.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please Enter All the Required Data")
                    .show();

        }
    }

    private void saveDatatoDB(String bank_Name, String nameOf_FDHolder, String nominee_Data) {
        dB_BankName.create(new MyObject(DB_BankName, bank_Name));
        dB_BankName.create(new MyObject(DB_User_Name, nameOf_FDHolder));
        dB_BankName.create(new MyObject(DB_User_Name, nominee_Data));

    }

    public void onDueDateClick(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
                cal.set(Calendar.MONTH, monthOfYear);
                String month_name = dateFormat.format(cal.getTime());
                String Due_Date = dayOfMonth + " " + month_name;
                dueDate.setText(Due_Date);

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
                DueDate = month_text + "-" + day;
                Due_Day_ofthe_Year = year + "-" + month_text + "-" + day;

            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void onMaturityDateClick(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
                cal.set(Calendar.MONTH, monthOfYear);
                String month_name = dateFormat.format(cal.getTime());
                String maturity_Date = dayOfMonth + " " + month_name + " " + year;
                maturityDate.setText(maturity_Date);

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
                gotMaturityDate = year + "-" + month_text + "-" + day;

            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    public void onStartDateClick(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
                cal.set(Calendar.MONTH, monthOfYear);
                String month_name = dateFormat.format(cal.getTime());
                String start_date = dayOfMonth + " " + month_name + " " + year;
                startDate.setText(start_date);

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
                gotStartDate = year + "-" + month_text + "-" + day;

            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void onLastPremiumClick(View view) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
                cal.set(Calendar.MONTH, monthOfYear);
                String month_name = dateFormat.format(cal.getTime());
                String maturity_Date = dayOfMonth + " " + month_name + " " + year;
                bLastPremiumDate.setText(maturity_Date);

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
                gotLastPremiumDate = year + "-" + month_text + "-" + day;

            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            //Setting the real returned image.
            //getImageView().setImageURI(r.getUri());

            //If you want the Bitmap.
            // getImageView().setImageBitmap(r.getBitmap());
            image_uri = pickResult.getUri();
            Image.setVisibility(View.VISIBLE);
            Image.setImageURI(pickResult.getUri());
            //Image path
            //r.getPath();
            Toast.makeText(this, "Photo is successfully Loaded ", Toast.LENGTH_SHORT).show();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
