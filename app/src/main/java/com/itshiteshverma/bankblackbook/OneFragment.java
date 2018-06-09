package com.itshiteshverma.bankblackbook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
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
import com.itshiteshverma.bankblackbook.HelperClass.BlogViewHolder_OneFragment;
import com.itshiteshverma.bankblackbook.Policy.PolicyDetails;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OneFragment extends Fragment {

    public static String DataID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataBaseReference;
    RecyclerView recyclerView;
    Query querySortAcctoDueDate;
    SimpleDateFormat s = new SimpleDateFormat("MM-dd"), fulldate = new SimpleDateFormat("yyyy-MM-dd");
    DateTime today = new DateTime();
    String current_Date = s.format(new Date()), CurrentDateFull = fulldate.format(new Date());
    FirebaseUser user;
    RelativeLayout mainLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseRecyclerAdapter<OneFragment_GetterSetter, BlogViewHolder_OneFragment> firebaseRecyclerAdapter;
    boolean _areLecturesLoaded = false;


    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        recyclerView = view.findViewById(R.id.rvDataList);
        mainLayout = view.findViewById(R.id.mainPageRelativeLayout);
        swipeRefreshLayout = view.findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        user = FirebaseAuth.getInstance().getCurrentUser();

        checkStatus();

        dataBaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
        //Toast.makeText(getActivity(), "current date" + current_Date, Toast.LENGTH_SHORT).show();
        querySortAcctoDueDate = dataBaseReference.orderByChild("due_date").startAt(current_Date);

//        querySortAcctoMaturityDate = dataBaseReference.orderByChild("maturity_date").startAt(CurrentDateFull);


        dataBaseReference.keepSynced(true);

        final SweetAlertDialog pDialog = new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(true);
        pDialog.show();


        querySortAcctoDueDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pDialog.dismiss();
                if (dataSnapshot.getChildrenCount() != 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();

            }
        });

        logUserFabrics();


        return view;


    }

    private void Refresh() {

        firebaseRecyclerAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();

    }


    private void checkStatus() {

    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OneFragment_GetterSetter, BlogViewHolder_OneFragment>
                (
                        OneFragment_GetterSetter.class,
                        R.layout.data_card_layout,
                        BlogViewHolder_OneFragment.class,
                        querySortAcctoDueDate


                ) {

            @Override
            protected void populateViewHolder(BlogViewHolder_OneFragment viewHolder, final OneFragment_GetterSetter model, final int position) {
                viewHolder.setPolicy_Name(model.getPolicy_name());
                viewHolder.setDue_date(model.getDue_date());
                viewHolder.setBank_Name(model.getBank_name());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.getType().equals("POLICY")) {
                            Intent i = new Intent(getActivity(), PolicyDetails.class);
                            DataID = getRef(position).getKey();
                            startActivity(i);
                        } else if (model.getType().equals("FD")) {
                            Intent i = new Intent(getActivity(), FDDetails.class);
                            DataID = getRef(position).getKey();
                            startActivity(i);
                        }
                    }
                });

                viewHolder.setBackgroundColor(model.getType());


            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }


    private void logUserFabrics() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(user.getUid());
        Crashlytics.setUserEmail(user.getEmail());
        Crashlytics.setUserName(user.getDisplayName());

    }

}
