package com.example.mohammad.tennisclub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
