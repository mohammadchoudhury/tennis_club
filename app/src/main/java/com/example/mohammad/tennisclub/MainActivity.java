package com.example.mohammad.tennisclub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mohammad.tennisclub.model.User;
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

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users/"+user.getUid());
//        User cUser;
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


        // Dummy data
        String[] data = {
                "Court 1 - Single Session - 02 Feb 2017 12:00PM",
                "Court 2 - Group Session - 03 Feb 2017 15:00PM",
                "Court 1 - Private Session - 05 Feb 2017 18:00PM"
        };
        List<String> bookings = new ArrayList<>(Arrays.asList(data));
        ListAdapter bookingsAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                data
        );
        ListView mListView = (ListView) findViewById(R.id.lv_bookings);
        mListView.setAdapter(bookingsAdapter);
    }
}
