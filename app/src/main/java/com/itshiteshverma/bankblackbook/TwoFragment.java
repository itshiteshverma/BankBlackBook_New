package com.itshiteshverma.bankblackbook;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import com.itshiteshverma.bankblackbook.HelperClass.BlogViewHolder_OneFragment;
import com.itshiteshverma.bankblackbook.Policy.PolicyDetails;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataBaseReference;
    RecyclerView recyclerView;
    Query querySortAcctoMaturityDate;
    SimpleDateFormat s = new SimpleDateFormat("MM-dd");
    SimpleDateFormat fulldate = new SimpleDateFormat("yyyy-MM-dd");
    String CurrentDateFull = fulldate.format(new Date());
    RelativeLayout mainLayout;
    boolean _areLecturesLoaded = false;


    public TwoFragment() {
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
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        recyclerView = view.findViewById(R.id.rvDataListAll);
        mainLayout = view.findViewById(R.id.mainPageRelativeLayout2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        dataBaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
        querySortAcctoMaturityDate = dataBaseReference.orderByChild("maturity_date").startAt(CurrentDateFull);
        dataBaseReference.keepSynced(true);


        return view;


    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !_areLecturesLoaded) {

            final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(true);
            pDialog.show();


            dataBaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Toast.makeText(getActivity(), "Data loaded", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    if (dataSnapshot.hasChildren()) {
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();

                }
            });


            FirebaseRecyclerAdapter<OneFragment_GetterSetter, BlogViewHolder_OneFragment> firebaseRecyclerAdapter = new
                    FirebaseRecyclerAdapter<OneFragment_GetterSetter, BlogViewHolder_OneFragment>
                            (
                                    OneFragment_GetterSetter.class,
                                    R.layout.data_card_layout,
                                    BlogViewHolder_OneFragment.class,
                                    querySortAcctoMaturityDate


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
                                        OneFragment.DataID = getRef(position).getKey();
                                        startActivity(i);
                                    } else if (model.getType().equals("FD")) {
                                        Intent i = new Intent(getActivity(), FDDetails.class);
                                        OneFragment.DataID = getRef(position).getKey();
                                        startActivity(i);
                                    }
                                }
                            });

                            viewHolder.setBackgroundColor(model.getType());


                        }
                    };


            recyclerView.setAdapter(firebaseRecyclerAdapter);
            _areLecturesLoaded = true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //MainLayout.setVisibility(View.VISIBLE);
    }
}
