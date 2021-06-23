package com.azetech.questionare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText usernameText, passwordText;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    Convert convert = new Convert();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            String uid = convert.ObjectToString(firebaseAuth.getUid());
            if (uid.equals("UIG15DufrIXr9Y5TUySnd7a40V12")){
                startActivity(new Intent(Login.this, AdminHome.class));
            }
            else {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                validate();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void validate() {
        String username = convert.ObjectToString(usernameText.getText()).trim();
        String password = convert.ObjectToString(passwordText.getText()).trim();

        if (username.equals("")){
            progressBar.setVisibility(View.GONE);
            usernameText.setError("Please Enter");
        }
        else if (password.equals("")){
            progressBar.setVisibility(View.GONE);
            passwordText.setError("Please Enter");
        }
        else {
            authenticate(username, password);
        }
    }

    private void authenticate(final String username, String password) {
        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if (username.equals("admin@gmail.com")){
                                startActivity(new Intent(Login.this, AdminHome.class));
                            }
                            else {
                                startActivity(new Intent(Login.this, MainActivity.class));
                            }
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    private void init() {
        usernameText = (EditText) findViewById(R.id.login_username);
        passwordText = (EditText) findViewById(R.id.login_password);

        loginButton = (Button) findViewById(R.id.login_button);

        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

    }
}