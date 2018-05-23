package com.example.mohammad.tennisclub;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NormalBookingFragment extends Fragment {

    static DatePickerDialog.OnDateSetListener mDateSetListener;
    static Calendar mCalendar;
    static ArrayList<String> mOptions;
    static ArrayAdapter mOptionsAdapter;
//    static ArrayList<String> mTimesTaken;
    static ArrayList<String> mCourtsTaken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_normal_booking, container, false);

        mOptions = new ArrayList<>();
        mOptions.add("Choose a date");

        mCalendar = Calendar.getInstance();

        mOptionsAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item_centred, mOptions);
        ListView lv = rootView.findViewById(R.id.lv_booking_options);
        lv.setAdapter(mOptionsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        new DatePickerFragment().show(getFragmentManager(), "datePicker");
                        break;
                    case 1:
                        new TimePickerFragment().show(getFragmentManager(), "timePicker");
                        break;
                    case 2:
                        new CourtPickerFragment().show(getFragmentManager(), "courtPicker");
                        break;
                }
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                while (mOptions.size() > 1) mOptions.remove(1);
                mCalendar.clear();
                mOptions.add("Choose a time");
                mCalendar.set(year, month, day);
                String dateString = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) + ", "
                        + mCalendar.get(Calendar.DAY_OF_MONTH) + " "
                        + mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " "
                        + mCalendar.get(Calendar.YEAR);
                mOptions.set(0, dateString);
                mOptionsAdapter.notifyDataSetChanged();

//                FirebaseFirestore fsdb = FirebaseFirestore.getInstance();
//                Date nDate = mCalendar.getTime();
//                nDate.setTime(mCalendar.getTimeInMillis() + 86400000);
//                fsdb.collection("bookings")
//                        .whereGreaterThanOrEqualTo("date", mCalendar.getTime())
//                        .whereLessThan("date", nDate)
//                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                            @Override
//                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                                mTimesTaken = new ArrayList<>();
//                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                                    Map booking = document.getData();
//                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.UK);
//                                    String time = sdf.format(booking.get("date"));
//                                    mTimesTaken.add(time);
//                                }
//                            }
//                        });

            }
        };

        rootView.findViewById(R.id.btn_book)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = "";
                        boolean valid = false;
                        if (mOptions.get(0).equals("Choose a date")) {
                            msg = "You must choose a date";
                        } else if (mOptions.get(1).equals("Choose a time")) {
                            msg = "You must choose a time";
                        } else if (!mCalendar.after(Calendar.getInstance())) {
                            msg = "Booking must be in the future";
                        } else if (mOptions.get(2).equals("Choose a court")) {
                            msg = "You must choose a court";
                        } else {
                            valid = true;
                        }
                        if (!valid) Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show();
                        else {
                            FirebaseFirestore fsdb = FirebaseFirestore.getInstance();
                            CollectionReference sessionsRef = fsdb.collection("bookings");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            Map<String, Object> booking = new HashMap<>();
                            booking.put("date", mCalendar.getTime());
                            booking.put("type", "court");
//                            booking.put("price", Double.parseDouble(((EditText) rootView.findViewById(R.id.et_price)).getText().toString()));
                            booking.put("price", 0.00);
                            booking.put("court", mOptions.get(2));
                            booking.put("user", fsdb.document("users/"+user.getUid()));
                            sessionsRef.add(booking);
                            Toast.makeText(getContext(), "Successfully created booking", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }


                    }
                });

        return rootView;
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), mDateSetListener, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1209600000);
            return datePickerDialog;
        }
    }

    public static class TimePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
            String[] timings = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"};
            ArrayList<String> timingsAL = new ArrayList<String>(Arrays.asList(timings));
//            timingsAL.removeAll(mTimesTaken);
            final ArrayList<String> times = timingsAL;
            ListAdapter timesAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item_centred, times);
            View title = View.inflate(getContext(), R.layout.dialog_title, null);
            ((TextView) title.findViewById(R.id.title)).setText("Choose a time");
            builder.setCustomTitle(title)
                    .setAdapter(timesAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int hourOfDay = Integer.parseInt(times.get(which).split(":")[0]);
                            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            mCalendar.set(Calendar.MINUTE, 0);
                            mOptions.set(1, times.get(which));
                            while (mOptions.size() > 2) mOptions.remove(2);
                            mOptions.add("Choose a court");
                            mOptionsAdapter.notifyDataSetChanged();

                            FirebaseFirestore fsdb = FirebaseFirestore.getInstance();
                            Date nDate = mCalendar.getTime();
                            fsdb.collection("bookings")
                                    .whereEqualTo("date", mCalendar.getTime())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                            mCourtsTaken = new ArrayList<>();
                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                Map booking = document.getData();
                                                mCourtsTaken.add((String) booking.get("court"));
                                            }
                                        }
                                    });


                        }
                    })
                    .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
    }

    public static class CourtPickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
            String[] courtsA = {"Court 1", "Court 2", "Court 3", "Court 4"};
            ArrayList<String> courtsAL = new ArrayList<String>(Arrays.asList(courtsA));
            courtsAL.removeAll(mCourtsTaken);
            final ArrayList<String> courts = courtsAL;

            if (courts.isEmpty()) {
                this.dismiss();
                Toast.makeText(getContext(), "No courts available", Snackbar.LENGTH_LONG).show();
            }

            ListAdapter courtsAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item_centred, courts);
            View title = View.inflate(getContext(), R.layout.dialog_title, null);
            ((TextView) title.findViewById(R.id.title)).setText("Choose a court");
            builder.setCustomTitle(title)
                    .setAdapter(courtsAdapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mOptions.set(2, courts.get(which));
                            mOptionsAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.text_cancel, null);
            return builder.create();
        }
    }
}
