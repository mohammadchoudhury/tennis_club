package com.example.mohammad.tennisclub;

import android.content.Intent;
import android.support.annotation.NonNull;
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
    EditText mEmailEditText;
    EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = (EditText) findViewById(R.id.et_email);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);

        ((Button) findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                if (valid) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Authentication successful.",
                                                Toast.LENGTH_SHORT).show();
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
                                        }
//                                        Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException(),
//                                                Toast.LENGTH_LONG).show();
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

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }
}
