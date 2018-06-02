package com.itshiteshverma.bankblackbook.Policy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itshiteshverma.bankblackbook.R;
import com.squareup.picasso.Picasso;

import static com.itshiteshverma.bankblackbook.OneFragment.DataID;


/**
 * A simple {@link Fragment} subclass.
 */
public class Policy_Image extends Fragment {

    ImageView policy_image;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;


    public Policy_Image() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_policy__image, container, false);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
        databaseReference.child(DataID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    setData(dataSnapshot);
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });


        policy_image = view.findViewById(R.id.imageViewImagePolicy);


        return view;
    }


    public void setData(DataSnapshot dataSnapshot) {
        String image_data = (String) dataSnapshot.child("image").getValue();


        Picasso.with(getActivity())
                .load(image_data)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.loading_animation)
                .into(policy_image);


        policy_image.setOnTouchListener(new ImageMatrixTouchHandler(getActivity()));

    }


}
