package com.itshiteshverma.bankblackbook;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.itshiteshverma.bankblackbook.FD.AddFD;
import com.itshiteshverma.bankblackbook.Policy.AddPolicy;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView userName, userEmailId;
    CircleImageView userImage;
    RatingDialog ratingDialog;
    FloatingActionMenu materialDesignFAM;
    com.github.clans.fab.FloatingActionButton floatingActionButton1, floatingActionButton2;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int[] tabIcons = {
            R.drawable.ic_blur_circular_black_24dp,
            R.drawable.all_fd_policy_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0); //added
        userName = headerView.findViewById(R.id.tvUserName);
        userImage = headerView.findViewById(R.id.imageViewProfilePhoto);
        userEmailId = headerView.findViewById(R.id.tvUserEmailId);

        if (user != null) {
            userName.setText(user.getDisplayName());
        }
        if (user != null) {
            userEmailId.setText(user.getEmail());
        }

        if (user != null) {
            Picasso.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.user_green)
                    .error(R.drawable.user_red)
                    .into(userImage);
        }
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainPage.this, LoginAndSignUp.class));
                }
            }
        };


        if (!haveNetworkConnection()) {
            SnackbarManager.show(
                    com.nispok.snackbar.Snackbar.with(getApplicationContext()) // context
                            .text("No Internet Connectivity !!!") // text to be displayed
                            .textColor(Color.WHITE) // change the text color
                            .color(Color.RED) // change the background color
                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE) // make it shorter
                            .actionLabel("Action") // action button label
                            .actionColor(Color.BLACK) // action button label color
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                    Toast.makeText(MainPage.this, "Click", Toast.LENGTH_SHORT).show();
                                }
                            }) // action button's ActionClickListener
                    , MainPage.this); // activity where it is displayed


        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Current Policy & FD");
        adapter.addFragment(new TwoFragment(), "All Policy & FD");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_log_out) {
            mAuth.signOut();
            Toast.makeText(this, "Log Out Successfully", Toast.LENGTH_SHORT).show();

        }
        if (id == R.id.nav_manage) {
            startActivity(new Intent(MainPage.this, ToolsPage.class));
        }
        if (id == R.id.contact_us) {
            new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Hitesh Verma")
                    .setContentText("Developer And Designer")
                    .setCustomImage(R.drawable.love)
                    .setConfirmText("Contact Me")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
//                            Intent email = new Intent(Intent.ACTION_SEND);
//                            email.putExtra(Intent.EXTRA_SUBJECT, "Policy And FD Manager APP ");
//                            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"itshiteshverma@gmail.com"});
//                            email.setType("message/rfc822");
//                            startActivity(Intent.createChooser(email, "Send Mail :"));
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hiteshverma.typeform.com/to/zuEb4G"));
                            startActivity(browserIntent);

                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }

        if (id == R.id.completedPolicy) {
            startActivity(new Intent(MainPage.this, Completed_PolicyAndFD.class));
        }

        if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/*");
            String shareBody = "https://goo.gl/e1zBFA";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "https://goo.gl/e1zBFA");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        if (id == R.id.nav_rate) {
            Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getApplication().getPackageName())));
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);


        materialDesignFAM = findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = findViewById(R.id.material_design_floating_action_menu_item2);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked

                startActivity(new Intent(MainPage.this, AddFD.class));


            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                Intent i = new Intent(MainPage.this, AddPolicy.class);
                i.putExtra("Update", "False");
                startActivity(i);
            }
        });


    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
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
