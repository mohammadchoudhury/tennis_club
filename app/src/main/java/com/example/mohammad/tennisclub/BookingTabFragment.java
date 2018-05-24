package com.example.mohammad.tennisclub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mohammad.tennisclub.model.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mohammad on 17/01/18.
 */

public class BookingTabFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    public BookingTabFragment() {
    }

    public static BookingTabFragment newInstance(int position) {
        BookingTabFragment fragment = new BookingTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_booking, container, false);

        // position: 1 = upcoming bookings, 2 = previous bookings
        int position = getArguments().getInt(ARG_POSITION, 0);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore fsdb = FirebaseFirestore.getInstance();

        final List<Booking> bookings = new ArrayList<>();

        final BookingListViewAdapter bookingsAdapter = new BookingListViewAdapter(bookings);
        ListView bookingsListView = (ListView) rootView.findViewById(R.id.lv_bookings);
        bookingsListView.setAdapter(bookingsAdapter);
        bookingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(parent.getContext(), "Clicked booking " + ++position, Toast.LENGTH_LONG).show();
            }
        });

        EventListener<QuerySnapshot> snapshotEventListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                bookings.clear();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Booking booking = document.toObject(Booking.class);
                        booking.setID(document.getId());
                        bookings.add(booking);
                    }
                }
                bookingsAdapter.notifyDataSetChanged();
            }
        };

//        fsdb.collection("bookings")
//                .whereEqualTo("user", fsdb.document("users/" + user.getUid()))
////                .whereGreaterThanOrEqualTo("date", new Date())
//                .addSnapshotListener(snapshotEventListener);
//
////
        Query query = fsdb.collection("bookings")
                .whereEqualTo("user", fsdb.document("users/" + user.getUid()));

        if (position == 0) query.whereGreaterThanOrEqualTo("date", new Date())
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(snapshotEventListener);
        else query.whereLessThan("date", new Date())
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(snapshotEventListener);


        return rootView;
    }

}
