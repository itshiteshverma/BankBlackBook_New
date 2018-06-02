package com.itshiteshverma.bankblackbook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itshiteshverma.bankblackbook.FD.FDDetails;
import com.itshiteshverma.bankblackbook.Policy.PolicyDetails;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Completed_PolicyAndFD extends AppCompatActivity {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataBaseReference;
    RecyclerView recyclerView;
    Query querySortAcctoMaturityDate;
    SimpleDateFormat s = new SimpleDateFormat("MM-dd"), fulldate = new SimpleDateFormat("yyyy-MM-dd");

    DateTime today = new DateTime();
    String current_Date = s.format(new Date()), CurrentDateFull = fulldate.format(new Date());
    FirebaseUser user;
    RelativeLayout mainLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseRecyclerAdapter<OneFragment_GetterSetter, BlogViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed__policy_and_fd);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        recyclerView = findViewById(R.id.Complete_rvDataList);
        mainLayout = findViewById(R.id.Complete_mainPageRelativeLayout);
        swipeRefreshLayout = findViewById(R.id.Complete_activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        user = FirebaseAuth.getInstance().getCurrentUser();

        checkStatus();

        dataBaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
        //Toast.makeText(getActivity(), "current date" + current_Date, Toast.LENGTH_SHORT).show();

        querySortAcctoMaturityDate = dataBaseReference.orderByChild("maturity_date").endAt(CurrentDateFull);


        dataBaseReference.keepSynced(true);

        final SweetAlertDialog pDialog = new SweetAlertDialog(Completed_PolicyAndFD.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(true);
        pDialog.show();

        querySortAcctoMaturityDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pDialog.dismiss();
                if (dataSnapshot.getChildrenCount() != 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Completed_PolicyAndFD.this, "Error", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();

            }
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void Refresh() {

        firebaseRecyclerAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();

    }


    private void checkStatus() {

    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OneFragment_GetterSetter, BlogViewHolder>
                (
                        OneFragment_GetterSetter.class,
                        R.layout.data_card_layout,
                        BlogViewHolder.class,
                        querySortAcctoMaturityDate


                ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, final OneFragment_GetterSetter model, final int position) {
                viewHolder.setPolicy_Name(model.getPolicy_name());
                viewHolder.setDue_date(model.getDue_date());
                viewHolder.setBank_Name(model.getBank_name());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.getType().equals("POLICY")) {
                            OneFragment.DataID = getRef(position).getKey();
                            Intent i = new Intent(Completed_PolicyAndFD.this, PolicyDetails.class);
                            startActivity(i);
                        } else if (model.getType().equals("FD")) {
                            OneFragment.DataID = getRef(position).getKey();
                            Intent i = new Intent(Completed_PolicyAndFD.this, FDDetails.class);
                            startActivity(i);
                        }
                    }
                });

                viewHolder.setBackgroundColor(model.getType());

            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);
        //MainLayout.setVisibility(View.VISIBLE);
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView Policy_Name, DueDate, BankName, typeofDate;
        LinearLayout mainLayout;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Policy_Name = mView.findViewById(R.id.tvPolicyName);
            DueDate = mView.findViewById(R.id.tvDueDate);
            mainLayout = mView.findViewById(R.id.mainLayoutCard);
            typeofDate = mView.findViewById(R.id.tv_typeofDate);
            BankName = mView.findViewById(R.id.tvBankName);

        }

        public void setPolicy_Name(String Policy_Name) {

            this.Policy_Name.setText(Policy_Name);
        }

        public void setDue_date(String due_date) {

            this.DueDate.setText(due_date);
        }

        public void setBank_Name(String bank_name) {
            this.BankName.setText(bank_name);
        }

        public void setBackgroundColor(String type) {

            switch (type) {
                case "POLICY":
                    // this.mainLayout.setBackgroundColor(Color.parseColor("#a91aa6f1"));
                    this.mainLayout.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.border_round_blue));
                    break;

                case "FD":
                    this.mainLayout.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.border_round_red));
                    this.typeofDate.setText("Maturity Date");
                    break;


            }

        }


    }


}
