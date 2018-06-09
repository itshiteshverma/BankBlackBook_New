package com.itshiteshverma.bankblackbook.Policy;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itshiteshverma.bankblackbook.HelperClass.AddPremiumDetails;
import com.itshiteshverma.bankblackbook.MainPage;
import com.itshiteshverma.bankblackbook.Policy.Extra.PolicyPremium_GetterSetter;
import com.itshiteshverma.bankblackbook.Policy.Extra.ViewHolder_PolicyPremium;
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
public class Policy_Data extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, databaseReference2;

    CircularProgressBar circularProgressBar;
    TextView PolicyNumber, PolicyName, PolicyHolderName, PremiumType,
            BankName, MaturityAmount, TimeStamp, ProgressBarText, Nominee, Remarks, PremiumAmount, InitialAmount, TotalTime, PolicyType, PremiumMethod, InsuredEvent;
    Button StartDate, EndDate, DueDate, DeletePolicy, UpdatePolicy, LastPremiumDate;
    TextView PremiumData;
    int percentage = 0;
    ImageView sharePolicy;

    String EditedStartDate;
    String EditedEndDate;
    String EditedDueDate;
    String EditedLastPremiumDate;

    float daysBetweenStartAndEnd = 0, daysBetweenStartAndCurrentDay = 0,
            yearsinBetweenStartAndEnd = 0, monthsinBetweenStartAndEnd = 0,
            yearsinBetweenCurrentAndEnd = 0, monthsinBetweenCurrentAndEnd = 0;

    File imagePath;
    View view;
    RecyclerView recyclerView;
    DatabaseReference dataBaseReference_forPremiumData;
    Query querySortAcctoDueDate;
    FirebaseRecyclerAdapter<PolicyPremium_GetterSetter, ViewHolder_PolicyPremium> firebaseRecyclerAdapter;


    public Policy_Data() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_policy__data, container, false);


        initilize();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference2 = database.getReference().child("BankBlackBook").child("data").child(user.getUid()).child(DataID);

        databaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
        databaseReference.child(DataID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        dataBaseReference_forPremiumData = databaseReference2.child("premium_data");
        querySortAcctoDueDate = dataBaseReference_forPremiumData.orderByChild("premium_date");
        //        querySortAcctoMaturityDate = dataBaseReference.orderByChild("maturity_date").startAt(CurrentDateFull);
        dataBaseReference_forPremiumData.keepSynced(true);


        querySortAcctoDueDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                // pDialog.dismiss();

            }
        });


        return view;
    }

    private void initilize() {
        circularProgressBar = view.findViewById(R.id.pbPolicyProgressbar);
        PolicyNumber = view.findViewById(R.id.tvPolicyNumber);
        PolicyName = view.findViewById(R.id.tvPolicyName);
        BankName = view.findViewById(R.id.tvBankName);
        PolicyHolderName = view.findViewById(R.id.tvPolicyHolderName);
        PremiumType = view.findViewById(R.id.tvTypeofPolicy);
        StartDate = view.findViewById(R.id.bStartDate);
        EndDate = view.findViewById(R.id.bEndDate);
        DueDate = view.findViewById(R.id.bDueDate);
        LastPremiumDate = view.findViewById(R.id.bLastPremiumDate);
        MaturityAmount = view.findViewById(R.id.tvMaturityAmount);
        TimeStamp = view.findViewById(R.id.tvTimeStamp);
        ProgressBarText = view.findViewById(R.id.tvPercentageDisplay);
        Nominee = view.findViewById(R.id.tvNomineePolicy);
        Remarks = view.findViewById(R.id.tvRemarksPolicy);
        PremiumAmount = view.findViewById(R.id.tvPremiumAmount);
        InitialAmount = view.findViewById(R.id.tvInitialAmount);
        DeletePolicy = view.findViewById(R.id.bDeletePolicy);
        PolicyType = view.findViewById(R.id.tvPolicyType);
        PremiumMethod = view.findViewById(R.id.tvPremiumMethod);
        InsuredEvent = view.findViewById(R.id.tvInsuredEvent);
        DeletePolicy.setOnClickListener(new View.OnClickListener() {
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
                                        Toast.makeText(getActivity(), "Policy Deleted", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), MainPage.class));
                                    }
                                });
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();


            }
        });

        UpdatePolicy = view.findViewById(R.id.bUpdatePolicy);
        UpdatePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Do You Want to Update ?")
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                Toast.makeText(getActivity(), "Please Update", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getActivity(), AddPolicy.class);
                                i.putExtra("Update", "True");
                                i.putExtra("policy_number", PolicyNumber.getText().toString().trim());
                                i.putExtra("policy_name", PolicyName.getText().toString().trim());
                                i.putExtra("bank", BankName.getText().toString().trim());
                                i.putExtra("premium_amount", PremiumAmount.getText().toString().trim());
                                i.putExtra("matured_amount", MaturityAmount.getText().toString().trim());
                                i.putExtra("policy_holder_name", PolicyHolderName.getText().toString().trim());
                                i.putExtra("type", PremiumType.getText().toString().trim());
                                i.putExtra("nominee", Nominee.getText().toString().trim());
                                i.putExtra("remarks", Remarks.getText().toString().trim());
                                i.putExtra("policy_number", PolicyNumber.getText().toString().trim());
                                i.putExtra("initial_amount", InitialAmount.getText().toString().trim());
                                i.putExtra("start_date", EditedStartDate);
                                i.putExtra("due_date", EditedDueDate);
                                i.putExtra("end_date", EditedEndDate);

                                startActivity(i);
                            }

                        })
                        .show();
            }
        });

        circularProgressBar.setOnClickListener(new View.OnClickListener() {
            float mod2 = (monthsinBetweenCurrentAndEnd) % 12;

            @Override
            public void onClick(View view) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Details")
                        .setContentText("Total Duration : " + (int) yearsinBetweenStartAndEnd + " Y & " +
                                (int) monthsinBetweenStartAndEnd % 12 + " M. \n \n" + "Total Remaining : " + (int) yearsinBetweenCurrentAndEnd + " Y & " +
                                (int) monthsinBetweenCurrentAndEnd % 12 + " M.")

                        .show();

            }
        });


        sharePolicy = view.findViewById(R.id.imageViewSharePolicy);
        sharePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Please Wait ....", Toast.LENGTH_LONG).show();
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
                shareIt();
            }
        });

        TotalTime = view.findViewById(R.id.textViewTotalTime);

        PremiumData = view.findViewById(R.id.tvAddPremmiumData);
        PremiumData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddPremiumDetails.class);
                i.putExtra("POLICY_NAME", PolicyName.getText().toString());
                startActivity(i);
            }
        });


        recyclerView = view.findViewById(R.id.rvPolicyData);
        recyclerView.setNestedScrollingEnabled(false);
        // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }


    public void setData(DataSnapshot dataSnapshot) {
        String policy_name = (String) dataSnapshot.child("policy_name").getValue();
        String policy_number = (String) dataSnapshot.child("policy_number").getValue();
        String bank_name = (String) dataSnapshot.child("bank_name").getValue();
        String due_date = (String) dataSnapshot.child("due_date").getValue();
        //String due_day_ofthe_year = (String) dataSnapshot.child("due_day_ofthe_year").getValue();
        String guranteed_matured_sum = (String) dataSnapshot.child("guranteed_matured_sum").getValue();
        String premium_amount = (String) dataSnapshot.child("premium_amount").getValue();
        String maturity_date = (String) dataSnapshot.child("maturity_date").getValue();

        String last_premium_date = null, insured_event = null, premium_method = null, policy_type = null;
        Boolean last_premium_date_Boolean = false, insured_event_Boolean = false, premium_method_Boolean = false, policy_type_Boolean = false;
        if (dataSnapshot.hasChild("last_premium_date")) {
            last_premium_date = (String) dataSnapshot.child("last_premium_date").getValue();
            last_premium_date_Boolean = true;
        }

        if (dataSnapshot.hasChild("insured_event")) {
            insured_event = (String) dataSnapshot.child("insured_event").getValue();
            insured_event_Boolean = true;
        }

        if (dataSnapshot.hasChild("policy_type")) {
            policy_type = (String) dataSnapshot.child("policy_type").getValue();
            policy_type_Boolean = true;
        }

        if (dataSnapshot.hasChild("premium_method")) {
            premium_method = (String) dataSnapshot.child("premium_method").getValue();
            premium_method_Boolean = true;
        }


        String nameof_policy_holder = (String) dataSnapshot.child("nameof_policy_holder").getValue();
        String start_date = (String) dataSnapshot.child("start_date").getValue();
        String timestamp = (String) dataSnapshot.child("timestamp").getValue();
        String premium_type = (String) dataSnapshot.child("premium_payment_mode").getValue();
        String nominee_data = (String) dataSnapshot.child("nominee").getValue();
        String remarks_data = (String) dataSnapshot.child("remarks").getValue();
        String initial_amount = (String) dataSnapshot.child("initial_amount").getValue();


        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null, date2 = null, date3 = null, date4 = null;
        float daysBetweenStartAndEnd = 0, daysBetweenStartAndCurrentDay = 0;
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String current_Date = s.format(new Date());

        try {
            date1 = formatter.parse(start_date);
            date2 = formatter.parse(maturity_date);
            date3 = formatter.parse("2017-" + due_date);

            if (last_premium_date_Boolean) {
                date4 = formatter.parse(last_premium_date);
            }
            daysBetweenStartAndEnd = Days.daysBetween(new DateTime(date2), new DateTime(date1)).getDays();
            daysBetweenStartAndCurrentDay = Days.daysBetween(new DateTime(current_Date), new DateTime(date1)).getDays();

            yearsinBetweenStartAndEnd = Years.yearsBetween(new DateTime(date1), new DateTime(date2)).getYears();
            monthsinBetweenStartAndEnd = Months.monthsBetween(new DateTime(start_date), new DateTime(maturity_date)).getMonths();
            //   Toast.makeText(getActivity().getApplicationContext(), "Months:" +monthsinBetweenStartAndEnd%12 +"Years: "+ yearsinBetweenStartAndEnd, Toast.LENGTH_SHORT).show();

            yearsinBetweenCurrentAndEnd = Years.yearsBetween(new DateTime(current_Date), new DateTime(maturity_date)).getYears();
            monthsinBetweenCurrentAndEnd = Months.monthsBetween(new DateTime(current_Date), new DateTime(maturity_date)).getMonths();
            // Toast.makeText(getActivity().getApplicationContext(), "Months:" +monthsinBetweenCurrentAndEnd%12, Toast.LENGTH_SHORT).show();

            TotalTime.setText(yearsinBetweenStartAndEnd + " Years");

            int percentage_temp = (int) ((daysBetweenStartAndCurrentDay / daysBetweenStartAndEnd) * 100);
            if (percentage_temp <= 100)
                percentage = (int) ((daysBetweenStartAndCurrentDay / daysBetweenStartAndEnd) * 100);
            else
                percentage = 100;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat newFormat2 = new SimpleDateFormat("dd MMMM");
        EditedStartDate = newFormat.format(date1);
        EditedEndDate = newFormat.format(date2);
        EditedDueDate = newFormat2.format(date3);
        if (last_premium_date_Boolean) {
            EditedLastPremiumDate = newFormat.format(date4);
        }

        ///////setting the progress bar
        circularProgressBar.setColor(ContextCompat.getColor(getActivity(), R.color.progressBarColor));
        circularProgressBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.backgroundProgressBarColor));
        circularProgressBar.setProgressBarWidth(40);
        circularProgressBar.setBackgroundProgressBarWidth(40);
        int animationDuration = 1000; // 2500ms = 2,5s
        circularProgressBar.setProgressWithAnimation(percentage, animationDuration); // Default duration = 1500ms
        PolicyNumber.setText(policy_number);
        PolicyName.setText(policy_name);
        BankName.setText(bank_name);
        PolicyHolderName.setText(nameof_policy_holder);
        PremiumType.setText(premium_type);
        StartDate.setText(EditedStartDate);
        EndDate.setText(EditedEndDate);
        DueDate.setText(EditedDueDate);

//        Locale locale = new Locale("en", "IN");
//        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
//        currencyFormatter.format(Integer.parseInt(guranteed_matured_sum));
//

        if (guranteed_matured_sum != null && !guranteed_matured_sum.isEmpty()) {
            MaturityAmount.setText(new DecimalFormat("##,##,##0").format(Integer.parseInt(guranteed_matured_sum)));
        }

        if (premium_amount != null && !premium_amount.isEmpty()) {
            PremiumAmount.setText(new DecimalFormat("##,##,##0").format(Integer.parseInt(premium_amount)));
        }

        if (initial_amount != null && !initial_amount.isEmpty()) {
            InitialAmount.setText(new DecimalFormat("##,##,##0").format(Integer.parseInt(initial_amount)));
        }

        if (policy_type_Boolean)
            PolicyType.setText(policy_type);
        if (insured_event_Boolean)
            InsuredEvent.setText(insured_event);
        if (premium_method_Boolean)
            PremiumMethod.setText(premium_method);

        TimeStamp.setText(timestamp);
        ProgressBarText.setText(percentage + "%");
        Nominee.setText(nominee_data);
        Remarks.setText(remarks_data);
        if (last_premium_date_Boolean) {
            LastPremiumDate.setText(EditedLastPremiumDate);
        }

    }

    public Bitmap takeScreenshot() {
        View rootView = view.getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String FileName = PolicyName.getText() + "_" + timeStamp + ".png";
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


    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PolicyPremium_GetterSetter, ViewHolder_PolicyPremium>
                (
                        PolicyPremium_GetterSetter.class,
                        R.layout.premium_data_card,
                        ViewHolder_PolicyPremium.class,
                        querySortAcctoDueDate


                ) {

            @Override
            protected void populateViewHolder(ViewHolder_PolicyPremium viewHolder, final PolicyPremium_GetterSetter model, final int position) {
                viewHolder.setReciptNo(model.getRecipt_no());
                viewHolder.setPremiumDate(model.getPremium_date());
                if (!model.getAmount_paid().equals("")) {
                    viewHolder.setAmountPaid(model.getAmount_paid());
                }
                viewHolder.setPaymentMode(model.getMode_of_payment());
                viewHolder.setRemarks(model.getRemarks());
                viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover this")
                                .setConfirmText("Yes,delete it!")
                                .setCancelText("No,cancel it")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        dataBaseReference_forPremiumData.child(getRef(position).getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    }
                });


            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);
        //MainLayout.setVisibility(View.VISIBLE);
    }
}
