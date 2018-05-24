package com.example.mohammad.tennisclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohammad.tennisclub.model.Session;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ViewSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session);

        Intent intent = getIntent();
        final String sessionId = intent.getStringExtra("sessionId");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final FirebaseFirestore fsdb = FirebaseFirestore.getInstance();

        fsdb.document("sessions/" + sessionId);

        findViewById(R.id.textView_date);

        DocumentReference sessionRef = fsdb.document("sessions/" + sessionId);
        sessionRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Session session = documentSnapshot.toObject(Session.class);
                ImageView sessionImage = findViewById(R.id.iv_session_icon);
                String sessionType = "";
                if (session.getType().equalsIgnoreCase("private")) {
                    sessionImage.setImageResource(R.drawable.ic_racket);
                    sessionType = "Private Session";
                } else if (session.getType().equalsIgnoreCase("group")) {
                    sessionImage.setImageResource(R.drawable.ic_balls);
                    sessionType = "Group Session";
                } else {
                    sessionImage.setImageResource(R.drawable.ic_court);
                    sessionType = "Court Booking";
                }
                ((TextView) findViewById(R.id.textView_type)).setText(sessionType);
                ((TextView) findViewById(R.id.textView_date)).setText(session.getDateString());
                ((TextView) findViewById(R.id.textView_time)).setText(session.getTimeString());
                ((TextView) findViewById(R.id.textView_price)).setText(session.getPriceString());
            }
        });

        findViewById(R.id.btn_book_session).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference sessionRef = fsdb.document("sessions/" + sessionId);
                sessionRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                Map<String, Object> booking = snapshot.getData();
                                booking.put("coach", "Imran Uddin");
                                booking.put("court", "Court5");
                                booking.put("user", fsdb.document("users/" + user.getUid()));
                                fsdb.collection("bookings")
                                        .add(booking)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getApplicationContext(), "Session booked", Toast.LENGTH_LONG).show();
                                                sessionRef.delete();
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            }
                                        });

                            }
                        });


            }
        });

    }

}

