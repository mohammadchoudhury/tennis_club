package com.example.mohammad.tennisclub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mConfirmPasswordEditText;
    EditText mNameEditText;
    EditText mPhoneEditText;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (null != currentUser) {
            // TODO: Direct to index page
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = (EditText) findViewById(R.id.et_email);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.et_confirm_password);
        mNameEditText = (EditText) findViewById(R.id.et_name);
        mPhoneEditText = (EditText) findViewById(R.id.et_phone);

        ((Button) findViewById(R.id.btn_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setMessage("Registering ...");
                progressDialog.show();

                boolean valid = true;

                final String email = mEmailEditText.getText().toString();
                if (!isValidEmail(email)) {
                    mEmailEditText.setError("Enter a valid email address");
                    valid = false;
                }

                final String password = mPasswordEditText.getText().toString();
                if (password.isEmpty()) {
                    mPasswordEditText.setError("Enter a password");
                    valid = false;
                }

                final String confirmPassword = mConfirmPasswordEditText.getText().toString();
                if (confirmPassword.isEmpty()) {
                    mConfirmPasswordEditText.setError("Confirm your password");
                    valid = false;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do no match", Toast.LENGTH_LONG).show();
                    valid = false;
                }

                final String name = mNameEditText.getText().toString();
                if (name.isEmpty() || !isValidName(name)) {
                    mNameEditText.setError("Enter a valid name");
                    valid = false;
                }

                final String phone = mPhoneEditText.getText().toString();
                if (!isValidPhone(phone)) {
                    mPhoneEditText.setError("Enter a valid mobile phone number");
                    valid = false;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                if (valid) {
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
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    } else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });

        ((TextView) findViewById(R.id.tv_signin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private boolean isValidName(String name) {
        String pattern = "^[A-Za-z]+[A-Za-z- ]*[A-Za-z]+$";
        return Pattern.compile(pattern).matcher(name).matches();
    }

    private boolean isValidEmail(String email) {
        String pattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(pattern).matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        String pattern = "^(\\+44(0)?|0)7\\d{9}$";
        return Pattern.compile(pattern).matcher(phone).matches();
    }

}
