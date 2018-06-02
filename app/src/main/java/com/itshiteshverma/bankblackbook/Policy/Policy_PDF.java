package com.itshiteshverma.bankblackbook.Policy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itshiteshverma.bankblackbook.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class Policy_PDF extends Fragment {

    WebView webView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    String GmailPDFViewer = "http://drive.google.com/viewerng/viewer?embedded=true&url=";
    private SweetAlertDialog pDialog;


    public Policy_PDF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_policy__pdf, container, false);

        webView = view.findViewById(R.id.webView);


//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        databaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
//        databaseReference.child(DataID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                try {
//                    pDialog = new SweetAlertDialog(getActivity().getApplicationContext(), SweetAlertDialog.PROGRESS_TYPE);
//                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                    pDialog.setTitleText("Loading");
//                    pDialog.setCancelable(true);
//                    setData(dataSnapshot);
//
//                } catch (Exception e) {
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        return view;
    }

//    public void setData(DataSnapshot dataSnapshot) {
//
//
//        String pdf_url = (String) dataSnapshot.child("PDF").getValue();
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//            @Override
//            public void onPageFinished(WebView view, final String url) {
//                pDialog.dismissWithAnimation();
//            }
//        });
//       // webView.loadUrl("http://www.teluguoneradio.com/rssHostDescr.php?hostId=147");
//
//    }


}
