package com.example.mohammad.tennisclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.mohammad.tennisclub.model.User;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.Label;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users/" + user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User cUser = dataSnapshot.getValue(User.class);
                ((TextView) findViewById(R.id.tv_name)).setText("Welcome, " + cUser.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        final FloatingActionMenu fab = findViewById(R.id.fab);
        fab.setClosedOnTouchOutside(true);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if (tab.getPosition() == 0) {
                    Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                    fab.setVisibility(View.VISIBLE);
                    fab.startAnimation(fadeIn);
                } else {
                    Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                    fab.startAnimation(fadeOut);
                    fab.setVisibility(View.GONE);

                }
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (null == currentUser) {
            finish();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createBooking(View v) {
        String sessionType;
        if (v instanceof Label) {
            sessionType = ((Label) v).getText().toString();
        } else {
            sessionType = ((FloatingActionButton) v).getLabelText();
        }
        Snackbar.make(v, sessionType, Snackbar.LENGTH_LONG).show();
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new BookingTabFragment().newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
