package com.itshiteshverma.bankblackbook.FD;

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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.itshiteshverma.bankblackbook.FD.AutoCompleteText.CustomAutoCompleteTextChangedListener_AccountNo;
import com.itshiteshverma.bankblackbook.FD.AutoCompleteText.CustomAutoCompleteTextChangedListener_BankName;
import com.itshiteshverma.bankblackbook.FD.AutoCompleteText.CustomAutoCompleteTextChangedListener_Name;
import com.itshiteshverma.bankblackbook.FD.AutoCompleteText.CustomAutoCompleteTextChangedListener_Nominee;
import com.itshiteshverma.bankblackbook.HelperClass.CustomAutoCompleteView;
import com.itshiteshverma.bankblackbook.HelperClass.DatabaseHandler_AutoComplete;
import com.itshiteshverma.bankblackbook.HelperClass.MyObject;
import com.itshiteshverma.bankblackbook.MainPage;
import com.itshiteshverma.bankblackbook.R;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddFD extends AppCompatActivity implements IPickResult {
    private static final int REUEST_CODE = 1, PDF_REQUEST_CODE = 2;
    final String DB_BankName = "Bank_Name";
    final String DB_User_Name = "User_Name";
    final String DB_AccountNo = "AccountNo";
    public CustomAutoCompleteView BankName, NameOfFD_Holder, Nominee, AccountNumber;
    // adapter for auto-complete
    public ArrayAdapter<String> myAdapter_BankName, myAdapter_Name, myAdapter_AccountNo, myAdapter_Nominee;
    // just to add some initial value
    public String[] item = new String[]{"Please search..."};
    MaterialEditText FDNumber, GuranteedMaturedSum,
            PrincipalAmount, IntrestRate, Remark;
    Button maturityDate, startDate;
    Button FDPhoto, FDPdf;
    String gotMaturityDate, gotStartDate, due_day;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataBaseReference;
    StorageReference mStorageReference;
    Uri image_uri;
    ImageView Image;
    SweetAlertDialog pDialog;
    CheckBox autoRenewal;
    Boolean AutoRenewalChecked = false;
    String timeStamp;
    // for database operations
    DatabaseHandler_AutoComplete dB_BankName, db_Name, db_AccountNumber;
    String PDF_filePath = null;
    String ImageURIString = "NO", PDF_URI_String = "NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fd);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initilise();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dataBaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        ImageView refresh = findViewById(R.id.submitEditNote);
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = getIntent();
//                finish();
//                startActivity(intent);
//                // Toast.makeText(AddFD.this, "Clickd", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public void insertSampleData() {

        // CREATE
//        dB_BankName.create(new MyObject("Bank_Name", "Axis Bank"));
//        dB_BankName.create(new MyObject("Bank_Name", "Yes Bank"));
//        dB_BankName.create(new MyObject("Bank_Name", "ICICI Bank "));
//        dB_BankName.create(new MyObject("User_Name", "Hitesh Verma"));
//        dB_BankName.create(new MyObject("User_Name", "Nilesh Verma"));
//        dB_BankName.create(new MyObject("AccountNo", "1234567890"));
//        dB_BankName.create(new MyObject("AccountNo", "1234567891"));


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


    public String[] get_AccountNo_FromDb(String searchTerm) {

        // add items on the array dynamically
        List<MyObject> products = db_AccountNumber.read(searchTerm);
        int rowCount = products.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (MyObject record : products) {

            item[x] = record.objectValue;
            x++;
        }

        return item;
    }


    private void initilise() {
        FDNumber = findViewById(R.id.etFDNumber);

        GuranteedMaturedSum = findViewById(R.id.etFDGauranteedMaturedSum);
        PrincipalAmount = findViewById(R.id.etFDPrincipalAmount);
        IntrestRate = findViewById(R.id.etFDIntrestRate);
        Remark = findViewById(R.id.etFDRemarks);
        maturityDate = findViewById(R.id.bFDMaturityDate);
        startDate = findViewById(R.id.bFDStartDate);
        FDPhoto = findViewById(R.id.imageButtonAddFD);
        FDPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.setType("image/*");
//                startActivityForResult(i, REUEST_CODE);
                PickImageDialog.build(new PickSetup()).show(AddFD.this);

            }
        });

        Image = findViewById(R.id.imageViewPhotoAddFD);
        Image.setOnTouchListener(new ImageMatrixTouchHandler(AddFD.this));
        BankName = findViewById(R.id.etFDBankName);
        NameOfFD_Holder = findViewById(R.id.etNameofFDHolder);
        Nominee = findViewById(R.id.etFDNominee);
        AccountNumber = findViewById(R.id.etFDACNumber);
        autoRenewal = findViewById(R.id.checkbox_AutoRenewal);
        FDPdf = findViewById(R.id.imageButtonAddFD_PDF);
        FDPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(AddFD.this)
                        .withRequestCode(PDF_REQUEST_CODE)
                        .withTitle("Policy & FD Manager")
                        .withCloseMenu(true)
                        .withRootPath(String.valueOf(Environment.getExternalStorageDirectory()))
                        .start();
            }
        });


        try {

            // instantiate database handler
            dB_BankName = new DatabaseHandler_AutoComplete(AddFD.this, DB_BankName);
            db_Name = new DatabaseHandler_AutoComplete(AddFD.this, DB_User_Name);
            db_AccountNumber = new DatabaseHandler_AutoComplete(AddFD.this, DB_AccountNo);
            // put sample data to database
            insertSampleData();
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

            AccountNumber.addTextChangedListener(new CustomAutoCompleteTextChangedListener_AccountNo(this));
            myAdapter_AccountNo = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
            AccountNumber.setAdapter(myAdapter_AccountNo);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        autoRenewal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                    Toast.makeText(AddFD.this, "Clicked", Toast.LENGTH_SHORT).show();
//Toast.makeText(AddFD.this, "Not Clicked", Toast.LENGTH_SHORT).show();
                AutoRenewalChecked = autoRenewal.isChecked();
            }
        });

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
            FDPhoto.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_green_24dp, 0);

            Image.setImageURI(selectedImageUri);

        }

        if (requestCode == PDF_REQUEST_CODE && resulteCode == RESULT_OK) {
            PDF_filePath = intent.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file
            FDPdf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_green_24dp, 0, 0, 0);
            //  Toast.makeText(this, "File Path: " + PDF_filePath, Toast.LENGTH_SHORT).show();

        }
    }

    public void onDoneClick(View v) {

        pDialog = new SweetAlertDialog(AddFD.this, SweetAlertDialog.PROGRESS_TYPE);
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

        if (image_uri != null) {

            //Image is Present
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


    private void saveDatatoDB(String bank_Name, String nameOf_FDHolder, String nominee_Data, String account_Number) {
        dB_BankName.create(new MyObject(DB_BankName, bank_Name));
        dB_BankName.create(new MyObject(DB_User_Name, nameOf_FDHolder));
        dB_BankName.create(new MyObject(DB_User_Name, nominee_Data));
        dB_BankName.create(new MyObject(DB_AccountNo, account_Number));

    }

    private void saveData(String downloadUrl, String PDFDownloadURL) {


        final String FD_Number = FDNumber.getText().toString().trim();
        final String Bank_Name = BankName.getText().toString().trim();
        final String NameOf_FDHolder = NameOfFD_Holder.getText().toString().trim();
        final String Guranteed_Matured_Sum = GuranteedMaturedSum.getText().toString().trim();
        final String Principal_Amount = PrincipalAmount.getText().toString().trim();
        final String Intrest_Rate = IntrestRate.getText().toString().trim();
        final String Nominee_Data = Nominee.getText().toString().trim();
        final String Remark_Data = Remark.getText().toString().trim();
        final String Account_Number = AccountNumber.getText().toString().trim();

        if (!TextUtils.isEmpty(Intrest_Rate) && !TextUtils.isEmpty(Bank_Name)
                && !TextUtils.isEmpty(Principal_Amount) && !TextUtils.isEmpty(Guranteed_Matured_Sum)
                && !TextUtils.isEmpty(gotMaturityDate)
                && !TextUtils.isEmpty(gotStartDate)

                ) {
            String key = dataBaseReference.push().getKey(); // this will create a new unique key
            final Map<String, Object> value = new HashMap<>();
            value.put("type", "FD");
            value.put("fd_number", FD_Number);
            value.put("policy_name", "Fixed Deposit"); //remain this
            //  value.put("due_date", due_day);  // THis is not the due day ...its the maturity date in MM-dd format
            value.put("bank_name", Bank_Name);
            value.put("principal_amount", Principal_Amount);
            value.put("nameof_fd_holder", NameOf_FDHolder);
            value.put("guranteed_matured_sum", Guranteed_Matured_Sum);
            value.put("interest_rate", Intrest_Rate);
            value.put("nominee", Nominee_Data);
            value.put("remarks", Remark_Data);
            value.put("due_date", gotMaturityDate); //Maturity Date is set Due_date to fetch the plicy details as well in the MainPage.java
            value.put("maturity_date", gotMaturityDate);
            value.put("due_day_ofthe_year", "12345"); //This is just a dummy value
            value.put("start_date", gotStartDate);
            value.put("timestamp", timeStamp);
            value.put("account_number", Account_Number);
            value.put("image", (downloadUrl != null) ? downloadUrl : "NO");
            value.put("PDF", (PDFDownloadURL != null) ? PDFDownloadURL : "NO");
            value.put("status", "active");
            value.put("auto_renewal", AutoRenewalChecked);
            SimpleDateFormat formatTest = new SimpleDateFormat("yyyy-MM-dd");
            Date date1, date2, date3 = null;
            try {
                date1 = formatTest.parse(gotMaturityDate);
                date2 = formatTest.parse(timeStamp);
                date3 = formatTest.parse(gotStartDate);
                value.put("maturity_date_detail", date1);
                value.put("timestamp_date_detail", date2);
                value.put("initial_date_detail", date3);
                saveDatatoDB(Bank_Name, NameOf_FDHolder, Nominee_Data, Account_Number);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dataBaseReference.child(key).setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    pDialog.dismissWithAnimation();
                    Toast.makeText(AddFD.this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddFD.this, MainPage.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pDialog.dismissWithAnimation();
                    //Toast.makeText(AddPolicy.this, "Data Didnt Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddFD.this, MainPage.class));
                }
            });

        } else

        {

            pDialog.dismissWithAnimation();
            new SweetAlertDialog(AddFD.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please Enter All the Required Data")
                    .show();

        }

    }


    public void onMaturityDateClickFD(View v) {
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
                due_day = month_text + "-" + day;
                gotMaturityDate = year + "-" + month_text + "-" + day;

            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    public void onStartDateClickFD(View v) {
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
            //   Toast.makeText(this, "Photo is successfully Loaded ", Toast.LENGTH_SHORT).show();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();

            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();

        }
    }
}
