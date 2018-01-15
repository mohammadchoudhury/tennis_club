package com.example.mohammad.tennisclub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohammad.tennisclub.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.Objects;
import java.util.regex.Pattern;

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
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference usersRef = database.getReference("users");
                                        User newUser = new User(email, name, phone);
                                        usersRef.child(user.getUid()).setValue(newUser);
                                        finish();
                                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
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

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (null != currentUser) {
            finish();
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
        }
    }

    private boolean isValidForm() {
        boolean valid = true;
        if (!isValidEmail(mEmailEditText.getText().toString().trim())) {
            mEmailEditTextLayout.setError(getString(R.string.error_email));
            valid = false;
        } else {
            mEmailEditTextLayout.setErrorEnabled(false);
        }
        String password = mPasswordEditText.getText().toString();
        if (!isValidPassword(password)) {
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
        if (!isValidName(mNameEditText.getText().toString().trim())) {
            mNameEditTextLayout.setError(getString(R.string.error_name));
            valid = false;
        } else {
            mNameEditTextLayout.setErrorEnabled(false);
        }
        if (!isValidPhone(mPhoneEditText.getText().toString().trim())) {
            mPhoneEditTextLayout.setError(getString(R.string.error_phone));
            valid = false;
        } else {
            mPhoneEditTextLayout.setErrorEnabled(false);
        }
        return valid;
    }

    private boolean isValidEmail(String email) {
        final String pattern = "^[_AZaz09\\+]+(\\.[_AZaz09]+)*@[AZaz09]+(\\.[AZaz09]+)*(\\.[AZaz]{2,})$";
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        final String pattern = "^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9 ]{8,}$";
        return Pattern.compile(pattern).matcher(password).matches();
    }

    private boolean isValidName(String name) {
        final String pattern = "^[AZaz]+[AZaz -]*[AZaz]*$";
        return Pattern.compile(pattern).matcher(name).matches();
    }

    private boolean isValidPhone(String phone) {
        String pattern = "^(\\+44(0)?|0)7\\d{9}$";
        return Pattern.compile(pattern).matcher(phone).matches();
    }

}
