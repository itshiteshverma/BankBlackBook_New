package com.itshiteshverma.bankblackbook.FD;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itshiteshverma.bankblackbook.MainPage;
import com.itshiteshverma.bankblackbook.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.itshiteshverma.bankblackbook.OneFragment.DataID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FD_Data extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, databaseReference2;
    CircularProgressBar circularProgressBar;
    TextView FDNumber, NameOftheFDHolderName, AccountNumber, Interest,
            BankName, MaturityAmount, TimeStamp, ProgressBarText, InitialAmount, ProfitAmount, Nominee, Remarks, TotalTime;
    Button StartDate, EndDate, Delete;
    int percentage = 0;
    SweetAlertDialog pDialog;
    ImageView autoRenewal;
    View view;
    File imagePath;
    ImageView shareFD;
    float daysBetweenStartAndEnd = 0, daysBetweenStartAndCurrentDay = 0,
            yearsinBetweenStartAndEnd = 0, monthsinBetweenStartAndEnd = 0,
            yearsinBetweenCurrentAndEnd = 0, monthsinBetweenCurrentAndEnd = 0;


    public FD_Data() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fd__data, container, false);

        initilize();

        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Data");
        pDialog.setCancelable(true);
        pDialog.show();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference2 = database.getReference().child("BankBlackBook").child("data").child(user.getUid()).child(DataID);

        databaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
        databaseReference.child(DataID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    setData(dataSnapshot);
                    pDialog.dismiss();
                } catch (Exception e) {

                }
                pDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pDialog.dismiss();
            }
        });


        return view;
    }

    private void initilize() {
        circularProgressBar = view.findViewById(R.id.pbPolicyProgressbar);
        FDNumber = view.findViewById(R.id.tvFDNumber);
        AccountNumber = view.findViewById(R.id.tvFDAccountNo);
        BankName = view.findViewById(R.id.tvFDBank);
        NameOftheFDHolderName = view.findViewById(R.id.tvFDNameoftheFDHolder);
        Interest = view.findViewById(R.id.tvFDInterest);
        StartDate = view.findViewById(R.id.bStartDate);
        EndDate = view.findViewById(R.id.bEndDate);
        MaturityAmount = view.findViewById(R.id.tvMaturityAmount);
        InitialAmount = view.findViewById(R.id.tvInitialAmount);
        Nominee = view.findViewById(R.id.tvNominee);
        Remarks = view.findViewById(R.id.tvRemarks);
        ProfitAmount = view.findViewById(R.id.tvProfitAmount);
        TimeStamp = view.findViewById(R.id.tvTimeStamp);
        ProgressBarText = view.findViewById(R.id.tvPercentageDisplay);
        autoRenewal = view.findViewById(R.id.imageViewAutoRenewal);
        Delete = view.findViewById(R.id.bDeleteFD);
        TotalTime = view.findViewById(R.id.textViewTotalTime);
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setConfirmText("Yes,delete it!")
                        .setCancelText("No,cancel it")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                databaseReference2.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "FD Deleted", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), MainPage.class));
                                    }
                                });
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            }
        });

        circularProgressBar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Details")
                        .setContentText("Total Duration : " + (int) yearsinBetweenStartAndEnd + " Years & " +
                                (int) monthsinBetweenStartAndEnd % 12 + " Months. \n \n" + "Total Remaining : " + (int) yearsinBetweenCurrentAndEnd + " Years & " +
                                (int) monthsinBetweenCurrentAndEnd % 12 + " Months.")

                        .show();

            }
        });

        shareFD = view.findViewById(R.id.imageViewShareFD);
        shareFD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Please Wait ....", Toast.LENGTH_LONG).show();
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
                shareIt();
            }
        });
    }


    public void setData(DataSnapshot dataSnapshot) {
        String fd_number = (String) dataSnapshot.child("fd_number").getValue();
        String bank_name = (String) dataSnapshot.child("bank_name").getValue();
        String maturity_date = (String) dataSnapshot.child("due_date").getValue();
        String guranteed_matured_sum = (String) dataSnapshot.child("guranteed_matured_sum").getValue();
        String Account_number = (String) dataSnapshot.child("account_number").getValue();
        String nameof_fd_holder = (String) dataSnapshot.child("nameof_fd_holder").getValue();
        String start_date = (String) dataSnapshot.child("start_date").getValue();
        String timestamp = (String) dataSnapshot.child("timestamp").getValue();
        String initial_Amount = (String) dataSnapshot.child("principal_amount").getValue();
        String remarks_data = (String) dataSnapshot.child("remarks").getValue();
        String nominee_data = (String) dataSnapshot.child("nominee").getValue();
        String interest_rate = (String) dataSnapshot.child("interest_rate").getValue();


        Boolean auto_renewal_Boolean = false;
        if (dataSnapshot.hasChild("auto_renewal")) {
            auto_renewal_Boolean = (Boolean) dataSnapshot.child("auto_renewal").getValue();

        }


        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null, date2 = null;
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String current_Date = s.format(new Date());

        try {
            date1 = formatter.parse(start_date);
            date2 = formatter.parse(maturity_date);
            daysBetweenStartAndEnd = Days.daysBetween(new DateTime(date2), new DateTime(date1)).getDays();
            daysBetweenStartAndCurrentDay = Days.daysBetween(new DateTime(current_Date), new DateTime(date1)).getDays();

            yearsinBetweenStartAndEnd = Years.yearsBetween(new DateTime(date1), new DateTime(date2)).getYears();
            monthsinBetweenStartAndEnd = Months.monthsBetween(new DateTime(start_date), new DateTime(maturity_date)).getMonths();
            //   Toast.makeText(getActivity().getApplicationContext(), "Months:" +monthsinBetweenStartAndEnd%12 +"Years: "+ yearsinBetweenStartAndEnd, Toast.LENGTH_SHORT).show();

            yearsinBetweenCurrentAndEnd = Years.yearsBetween(new DateTime(current_Date), new DateTime(maturity_date)).getYears();
            monthsinBetweenCurrentAndEnd = Months.monthsBetween(new DateTime(current_Date), new DateTime(maturity_date)).getMonths();
            // Toast.makeText(getActivity().getApplicationContext(), "Months:" +monthsinBetweenCurrentAndEnd%12, Toast.LENGTH_SHORT).show();


            int percentage_temp = (int) ((daysBetweenStartAndCurrentDay / daysBetweenStartAndEnd) * 100);
            if (percentage_temp <= 100)
                percentage = (int) ((daysBetweenStartAndCurrentDay / daysBetweenStartAndEnd) * 100);
            else
                percentage = 100;

            TotalTime.setText(yearsinBetweenStartAndEnd + " Years");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");
        String EditedStartDate = newFormat.format(date1);
        String EditedEndDate = newFormat.format(date2);

        ///////setting the progress bar
        circularProgressBar.setColor(ContextCompat.getColor(getActivity(), R.color.progressBarColor));
        circularProgressBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.backgroundProgressBarColor));
        circularProgressBar.setProgressBarWidth(40);
        circularProgressBar.setBackgroundProgressBarWidth(40);
        int animationDuration = 1000; // 2500ms = 2,5s
        circularProgressBar.setProgressWithAnimation(percentage, animationDuration); // Default duration = 1500ms


        FDNumber.setText(fd_number);
        AccountNumber.setText(Account_number);
        BankName.setText(bank_name);
        NameOftheFDHolderName.setText(nameof_fd_holder);
        StartDate.setText(EditedStartDate);
        EndDate.setText(EditedEndDate);
        Interest.setText(interest_rate);

        TimeStamp.setText(timestamp);
        ProgressBarText.setText(percentage + "%");

        int finalAmount = Integer.parseInt(guranteed_matured_sum), initialAmount = Integer.parseInt(initial_Amount);
        int profit = finalAmount - initialAmount;


        ProfitAmount.setText(new DecimalFormat("##,##,##0").format(profit));
        MaturityAmount.setText(new DecimalFormat("##,##,##0").format(finalAmount));
        InitialAmount.setText(new DecimalFormat("##,##,##0").format(initialAmount));

        Nominee.setText(nominee_data);
        Remarks.setText(remarks_data);
        if (auto_renewal_Boolean)
            autoRenewal.setImageDrawable(getResources().getDrawable(R.drawable.ic_icons8_checked_checkbox));
        else
            autoRenewal.setImageDrawable(getResources().getDrawable(R.drawable.ic_icons8_close_window));


    }


    public Bitmap takeScreenshot() {
        View rootView = view.getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String FileName = FDNumber.getText() + "_" + timeStamp + ".png";
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "PolicyAndFD_Manager");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {

            imagePath = new File(Environment.getExternalStorageDirectory() + "/PolicyAndFD_Manager/" + FileName);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e("GREC", e.getMessage(), e);
            } catch (IOException e) {
                Log.e("GREC", e.getMessage(), e);
            }

        } else {

            Toast.makeText(getActivity(), "Directory Not Created", Toast.LENGTH_SHORT).show();
        }


    }

    private void shareIt() {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "Details";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Details");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}
