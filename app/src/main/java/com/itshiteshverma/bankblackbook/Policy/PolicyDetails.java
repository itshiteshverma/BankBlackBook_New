package com.itshiteshverma.bankblackbook.Policy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itshiteshverma.bankblackbook.MainPage;
import com.itshiteshverma.bankblackbook.R;

import java.util.ArrayList;
import java.util.List;

import static com.itshiteshverma.bankblackbook.OneFragment.DataID;

public class PolicyDetails extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    DatabaseReference databaseReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Boolean imageTab = false, PDFTab = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_details);
        viewPager = findViewById(R.id.viewpagerPolicy);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabsPolicy);
        tabLayout.setupWithViewPager(viewPager);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = database.getReference().child("BankBlackBook").child("data").child(user.getUid());
        databaseReference.child(DataID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    imageTab = !dataSnapshot.child("image").getValue().equals("NO");
                    PDFTab = !dataSnapshot.child("PDF").getValue().equals("NO");

                } catch (Exception e) {

                } finally {
                    setupViewPager(viewPager);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setupViewPager(viewPager);
            }
        });

    }


    private void setupViewPager(ViewPager viewPager) {
        PolicyDetails.ViewPagerAdapter adapter = new PolicyDetails.ViewPagerAdapter(getSupportFragmentManager());


        adapter.addFragment(new Policy_Data(), "Policy Details");

        if (imageTab) {
            adapter.addFragment(new Policy_Image(), "Policy Photo");
        }

        if (PDFTab) {
            adapter.addFragment(new Policy_PDF(), "Policy PDF");
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PolicyDetails.this, MainPage.class));
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
