package com.example.mohammad.tennisclub;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohammad.tennisclub.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextInputLayout mEmailEditTextLayout, mPasswordEditTextLayout, mConfirmPasswordEditTextLayout, mNameEditTextLayout, mPhoneEditTextLayout;
    EditText mEmailEditText, mPasswordEditText, mConfirmPasswordEditText, mNameEditText, mPhoneEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mEmailEditTextLayout = (TextInputLayout) findViewById(R.id.etl_email);
        mEmailEditText = (EditText) findViewById(R.id.et_email);

        mPasswordEditTextLayout = (TextInputLayout) findViewById(R.id.etl_password);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);

        mConfirmPasswordEditTextLayout = (TextInputLayout) findViewById(R.id.etl_confirm_password);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.et_confirm_password);

        mNameEditTextLayout = (TextInputLayout) findViewById(R.id.etl_name);
        mNameEditText = (EditText) findViewById(R.id.et_name);

        mPhoneEditTextLayout = (TextInputLayout) findViewById(R.id.etl_phone);
        mPhoneEditText = (EditText) findViewById(R.id.et_phone);

        ((Button) findViewById(R.id.btn_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidForm()) {
                    final String email = mEmailEditText.getText().toString().trim();
                    final String password = mPasswordEditText.getText().toString();
                    final String name = mNameEditText.getText().toString().trim();
                    final String phone = mPhoneEditText.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        FirebaseFirestore fsdb = FirebaseFirestore.getInstance();
                                        fsdb.document("users/" + user.getUid())
                                                .set(new User(email, name, phone));
                                        finish();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    } else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_LONG).show();
                }
            }
        });

        ((TextView) findViewById(R.id.tv_sign_in)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    private boolean isValidForm() {
        boolean valid = true;
        if (!Utility.isValidEmail(mEmailEditText.getText().toString().trim())) {
            mEmailEditTextLayout.setError(getString(R.string.error_email));
            valid = false;
        } else {
            mEmailEditTextLayout.setErrorEnabled(false);
        }
        String password = mPasswordEditText.getText().toString();
        if (!Utility.isValidPassword(password)) {
            mPasswordEditTextLayout.setError(getString(R.string.error_password));
            valid = false;
        } else {
            mPasswordEditTextLayout.setErrorEnabled(false);
        }
        String confirm_password = mConfirmPasswordEditText.getText().toString();
        if (confirm_password.isEmpty() || !password.equals(confirm_password)) {
            mConfirmPasswordEditTextLayout.setError(getString(R.string.error_confirm_password));
            valid = false;
        } else {
            mConfirmPasswordEditTextLayout.setErrorEnabled(false);
        }
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

}
