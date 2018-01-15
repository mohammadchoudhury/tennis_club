package com.example.mohammad.tennisclub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextInputLayout mEmailEditTextLayout, mPasswordEditTextLayout;
    EditText mEmailEditText, mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmailEditTextLayout = (TextInputLayout) findViewById(R.id.etl_email);
        mEmailEditText = (EditText) findViewById(R.id.et_email);
        mPasswordEditTextLayout = (TextInputLayout) findViewById(R.id.etl_password);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);

        ((Button) findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidForm()) {
                    String email = mEmailEditText.getText().toString().trim();
                    String password = mPasswordEditText.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        finish();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    } else {
                                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                            Toast.makeText(LoginActivity.this, "User account does not exist.",
                                                    Toast.LENGTH_LONG).show();
                                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            Toast.makeText(LoginActivity.this, "User password incorrect.",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });

        ((TextView) findViewById(R.id.tv_sign_up)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RegisterActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (null != currentUser) {
            finish();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
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
        if (password.isEmpty()) {
            mPasswordEditTextLayout.setError(getString(R.string.error_password_empty));
            valid = false;
        } else {
            mPasswordEditTextLayout.setErrorEnabled(false);
        }
        return valid;
    }


    private boolean isValidEmail(String email) {
        final String pattern = "^[_AZaz09\\+]+(\\.[_AZaz09]+)*@[AZaz09]+(\\.[AZaz09]+)*(\\.[AZaz]{2,})$";
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
