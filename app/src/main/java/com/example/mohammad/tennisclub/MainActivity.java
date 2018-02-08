package com.example.mohammad.tennisclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.mohammad.tennisclub.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ValueEventListener mValueEventListener;
    private DatabaseReference mUserRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };

        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mUserRef = database.getReference("users/" + user.getUid());
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User cUser = dataSnapshot.getValue(User.class);
                ((TextView) findViewById(R.id.nav_tv_name)).setText(cUser.getName());
                ((TextView) findViewById(R.id.nav_tv_email)).setText(cUser.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(createNavigationItemSelectListener());
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.nav_chat).setEnabled(false);
        navMenu.findItem(R.id.nav_payment).setEnabled(false);
        navMenu.findItem(R.id.nav_settings).setEnabled(false);
        navMenu.findItem(R.id.nav_about).setEnabled(false);
        navMenu.findItem(R.id.nav_feedback).setEnabled(false);
        navMenu.findItem(R.id.nav_help).setEnabled(false);
        navMenu.findItem(R.id.nav_chat).setEnabled(false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new HomeFragment());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    /**
     * Creates a listener for nav view to perform action when items are selected
     *
     * @return OnNavigationItemSelectedListener
     */
    private NavigationView.OnNavigationItemSelectedListener createNavigationItemSelectListener() {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                Fragment fragment = null;
                switch (id) {
                    case R.id.nav_home:
                        if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof HomeFragment)) {
                            fragment = new HomeFragment();
                        }
                        break;
                    case R.id.nav_chat:
                        break;
                    case R.id.nav_payment:
                        break;
                    case R.id.nav_account:
                        if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof AccountFragment)) {
                            fragment = new AccountFragment();
                        }
                        break;
                    case R.id.nav_logout:
                        mAuth.signOut();
                        break;
                    case R.id.nav_settings:
                        break;
                    case R.id.nav_about:
                        break;
                    case R.id.nav_feedback:
                        break;
                    case R.id.nav_help:
                        break;
                }

                if (fragment != null) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE );
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
        mUserRef.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUserRef.removeEventListener(mValueEventListener);
        mAuth.removeAuthStateListener(mAuthStateListener);
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

}
