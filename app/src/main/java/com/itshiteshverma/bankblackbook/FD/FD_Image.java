package com.itshiteshverma.bankblackbook.FD;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
public class FD_Image extends Fragment {

    ImageView image;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;


    public FD_Image() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fd__image, container, false);

        image = view.findViewById(R.id.imageViewImageFD);
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

            }
        });


        return view;
    }

    public void setData(DataSnapshot dataSnapshot) {

        String image_data = (String) dataSnapshot.child("image").getValue();

        Picasso.with(getActivity())
                .load(image_data)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.loading_animation)
                .into(image);


        image.setOnTouchListener(new ImageMatrixTouchHandler(getActivity()));

    }


}
