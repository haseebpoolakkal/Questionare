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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminAddChild extends AppCompatActivity {

    private EditText usernameInput, passwordInput, nameInput, parentInput;
    private Button button;
    private Spinner categorySpinner;
    private ProgressBar progressBar;

    private DatabaseReference mChildRef;
    private FirebaseAuth firebaseAuth;

    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        init();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                validate();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdminAddChild.this, AdminHome.class));
        finish();
    }

    private void validate() {
        String name = convert.ObjectToString(nameInput.getText()).trim();
        String category = convert.ObjectToString(categorySpinner.getSelectedItem());
        String parent = convert.ObjectToString(parentInput.getText()).trim();
        String username = convert.ObjectToString(usernameInput.getText()).trim();
        String password = convert.ObjectToString(passwordInput.getText()).trim();

        if (name.equals("")) {
            nameInput.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (parent.equals("")) {
            parentInput.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (username.equals("")) {
            usernameInput.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (password.equals("")) {
            passwordInput.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else {
            signup(username, password, name, category, parent);
        }

    }

    private void signup(String username, String password, final String name, final String category, final String parent) {
        firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            addChild(name, category, parent);
                        }
                        else {
                            Toast.makeText(AdminAddChild.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void addChild(String name, String category, String parent) {
        HashMap childMap = new HashMap();
        childMap.put("name", name);
        childMap.put("category", category);
        childMap.put("parent", parent);
        try {
            mChildRef.child(firebaseAuth.getUid())
                    .setValue(childMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                nameInput.setText("");
                                parentInput.setText("");
                                usernameInput.setText("");
                                passwordInput.setText("");
                                FirebaseAuth.getInstance().signOut();
                                FirebaseAuth.getInstance().signInWithEmailAndPassword("admin@gmail.com", "admin123456");
                                Toast.makeText(AdminAddChild.this, "Child Added Successfully..", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(AdminAddChild.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    private void init() {
        nameInput = (EditText) findViewById(R.id.child_name);
        parentInput = (EditText) findViewById(R.id.parent_name);
        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);

        categorySpinner = (Spinner) findViewById(R.id.category_selector);

        button = (Button) findViewById(R.id.add_child_button);

        progressBar = (ProgressBar) findViewById(R.id.add_child_progress);
        progressBar.setVisibility(View.GONE);

        mChildRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mChildRef.keepSynced(true);

        firebaseAuth = FirebaseAuth.getInstance();
    }
}