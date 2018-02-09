package com.example.mohammad.tennisclub;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mohammad.tennisclub.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

    private EditText mNameEditText;
    private EditText mPhoneEditText;
    private EditText mCurrentPasswordEditText;
    private EditText mNewPasswordEditText;
    private EditText mConfirmPasswordEditText;

    private TextInputLayout mNameEditTextLayout;
    private TextInputLayout mPhoneEditTextLayout;
    private TextInputLayout mCurrentPasswordEditTextLayout;
    private TextInputLayout mNewPasswordEditTextLayout;
    private TextInputLayout mConfirmPasswordEditTextLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
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

        mNameEditText = rootView.findViewById(R.id.et_name);
        mPhoneEditText = rootView.findViewById(R.id.et_phone);
        mNameEditTextLayout = rootView.findViewById(R.id.etl_name);
        mPhoneEditTextLayout = rootView.findViewById(R.id.etl_phone);

        mCurrentPasswordEditText = rootView.findViewById(R.id.et_password);
        mNewPasswordEditText = rootView.findViewById(R.id.et_new_password);
        mConfirmPasswordEditText = rootView.findViewById(R.id.et_confirm_password);
        mCurrentPasswordEditTextLayout = rootView.findViewById(R.id.etl_password);
        mNewPasswordEditTextLayout = rootView.findViewById(R.id.etl_new_password);
        mConfirmPasswordEditTextLayout = rootView.findViewById(R.id.etl_confirm_password);

        rootView.findViewById(R.id.btn_change_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidDetailForm()) {
                    mUserRef.child("name").setValue(mNameEditText.getText().toString());
                    mUserRef.child("phone").setValue(mPhoneEditText.getText().toString());
                    Snackbar.make(rootView, "Update successful", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        rootView.findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidPasswordForm()) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), mCurrentPasswordEditText.getText().toString());

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(mNewPasswordEditText.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Snackbar.make(rootView, "Update successful", Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        mCurrentPasswordEditTextLayout.setErrorEnabled(false);
                                        mCurrentPasswordEditText.setText("");
                                        mNewPasswordEditText.setText("");
                                        mConfirmPasswordEditText.setText("");
                                    } else {
                                        mCurrentPasswordEditTextLayout.setError(getString(R.string.error_password_incorrect));
                                    }
                                }
                            });
                }
            }
        });
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

    private boolean isValidDetailForm() {
        boolean valid = true;
        if (!Utility.isValidName(mNameEditText.getText().toString().trim())) {
            mNameEditTextLayout.setError(getString(R.string.error_name));
            valid = false;
        } else {
            mNameEditTextLayout.setErrorEnabled(false);
        }
        if (!Utility.isValidPhone(mPhoneEditText.getText().toString().trim())) {
            mPhoneEditTextLayout.setError(getString(R.string.error_phone));
            valid = false;
        } else {
            mPhoneEditTextLayout.setErrorEnabled(false);
        }
        return valid;
    }

    private boolean isValidPasswordForm() {
        boolean valid = true;
        if (mCurrentPasswordEditText.getText().toString().isEmpty()) {
            mCurrentPasswordEditTextLayout.setError(getString(R.string.error_password_incorrect));
            valid = false;
        } else {
            mCurrentPasswordEditTextLayout.setErrorEnabled(false);
        }
        String newPassword = mNewPasswordEditText.getText().toString();
        if (!Utility.isValidPassword(newPassword)) {
            mNewPasswordEditTextLayout.setError(getString(R.string.error_password));
            valid = false;
        } else {
            mNewPasswordEditTextLayout.setErrorEnabled(false);
        }
        String confirmPassword = mConfirmPasswordEditText.getText().toString();
        if (confirmPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            mConfirmPasswordEditTextLayout.setError(getString(R.string.error_confirm_password));
            valid = false;
        } else {
            mConfirmPasswordEditTextLayout.setErrorEnabled(false);
        }
        return valid;
    }

}
