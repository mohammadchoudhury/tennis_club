package com.example.mohammad.tennisclub;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.mohammad.tennisclub.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private ValueEventListener mValueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mUserRef = database.getReference("users/" + user.getUid());
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User cUser = dataSnapshot.getValue(User.class);
                ((EditText) rootView.findViewById(R.id.et_name)).setText(cUser.getName());
                ((EditText) rootView.findViewById(R.id.et_phone)).setText(cUser.getPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_account);
        mUserRef.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mUserRef.removeEventListener(mValueEventListener);
    }

}
