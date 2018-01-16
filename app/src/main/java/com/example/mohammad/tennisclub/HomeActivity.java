package com.example.mohammad.tennisclub;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
                Toast.makeText(HomeActivity.this, databaseError.getDetails(), Toast.LENGTH_LONG).show();
            }
        });


        // Dummy data
        String[] data = {
                "Court 1|Single Session|02 Feb 2017|12:00PM",
                "Court 2|Group Session|03 Feb 2017|15:00PM",
                "Court 1|Private Session|05 Feb 2017|18:00PM"
        };
        List<String> bookings = new ArrayList<>(Arrays.asList(data));
        LVAdapter bookingsAdapter = new LVAdapter(bookings);
        ListView bookingsListView = (ListView) findViewById(R.id.lv_bookings);
        bookingsListView.setAdapter(bookingsAdapter);
        bookingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(parent.getContext(), "Clicked booking " + ++position, Toast.LENGTH_LONG).show();
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
        if (v instanceof  Label) {
            sessionType = ((Label) v).getText().toString();
        } else {
            sessionType = ((FloatingActionButton) v).getLabelText();
        }
        Snackbar.make(v, sessionType, Snackbar.LENGTH_LONG).show();
    }

    private class LVAdapter extends BaseAdapter {

        List<String> bookings;

        LVAdapter(List<String> bookings) {
            this.bookings = bookings;
        }

        @Override
        public int getCount() {
            return bookings.size();
        }

        @Override
        public String getItem(int position) {
            return bookings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return bookings.get(position).hashCode();
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            if (listItemView == null) {
                listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            }
            final String[] booking = getItem(position).split("\\|");
            ((TextView) listItemView.findViewById(R.id.tv_item_court)).setText(booking[0]);
            ((TextView) listItemView.findViewById(R.id.tv_item_session_type)).setText(booking[1]);
            ((TextView) listItemView.findViewById(R.id.tv_item_date)).setText(booking[2]);
            ((TextView) listItemView.findViewById(R.id.tv_item_time)).setText(booking[3]);
            return listItemView;
        }

    }

}
