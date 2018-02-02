package com.example.mohammad.tennisclub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final static String FRAGTAG = "NavDrawFragment";

    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(createNavigationItemSelectListenter());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new HomeFragment(), FRAGTAG);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private NavigationView.OnNavigationItemSelectedListener createNavigationItemSelectListenter() {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                String option = null;
                Fragment fragment = null;
                switch (id) {
                    case R.id.nav_home:
                        if (!(getSupportFragmentManager().findFragmentByTag(FRAGTAG) instanceof HomeFragment)) {
                            fragment = new HomeFragment();
                        }
                        break;
                    case R.id.nav_chat:
                        option = "Chat";
                        break;
                    case R.id.nav_payment:
                        option = "Payment History";
                        break;
                    case R.id.nav_account:
                        option = "Account";
                        break;
                    case R.id.nav_logout:
                        mAuth.signOut();
                        break;
                    case R.id.nav_settings:
                        option = "Settings";
                        break;
                    case R.id.nav_about:
                        option = "About Us";
                        break;
                    case R.id.nav_feedback:
                        option = "Feedback";
                        break;
                    case R.id.nav_help:
                        option = "Help";
                        break;
                }

                if (option != null) {
                    Toast.makeText(MainActivity.this, "Clicked " + option + " option", Toast.LENGTH_LONG).show();
                }

                if (fragment != null) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment, FRAGTAG);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        };
    }

}
