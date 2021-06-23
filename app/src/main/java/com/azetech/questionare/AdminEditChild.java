package com.azetech.questionare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminEditChild extends AppCompatActivity {

    private EditText nameText, parentText;
    private Button button;
    private Spinner categorySpinner;
    private ProgressBar progressBar;

    private DatabaseReference mChildRef;
    private FirebaseAuth firebaseAuth;

    ArrayAdapter adapter;

    Convert convert = new Convert();
    String uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);

        uid = getIntent().getStringExtra("id");
        init();

        showData();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                validate();
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void showData() {
        mChildRef.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String name = convert.ObjectToString(dataSnapshot.child("name").getValue());
                            String parent = convert.ObjectToString(dataSnapshot.child("parent").getValue());
                            String category = convert.ObjectToString(dataSnapshot.child("category").getValue());

                            nameText.setText(name);
                            parentText.setText(parent);
                            if (category != null){
                                int pos = adapter.getPosition(category);
                                categorySpinner.setSelection(pos);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdminEditChild.this, AdminChildren.class));
        finish();
    }

    private void validate() {
        String name = convert.ObjectToString(nameText.getText()).trim();
        String category = convert.ObjectToString(categorySpinner.getSelectedItem());
        String parent = convert.ObjectToString(parentText.getText()).trim();

        if (name.equals("")) {
            nameText.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (parent.equals("")) {
            parentText.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else {
            update(name, category, parent);
        }

    }

    private void update(final String name, final String category, final String parent) {
        mChildRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child("name").getRef().setValue(name);
                        dataSnapshot.child("parent").getRef().setValue(parent);
                        dataSnapshot.child("category").getRef().setValue(category);

                        Toast.makeText(AdminEditChild.this, "Successful..", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(AdminEditChild.this, AdminChildren.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
        nameText = (EditText) findViewById(R.id.edit_child_name);
        parentText = (EditText) findViewById(R.id.edit_parent_name);

        categorySpinner = (Spinner) findViewById(R.id.edit_category_selector);
        adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        button = (Button) findViewById(R.id.edit_child_button);

        progressBar = (ProgressBar) findViewById(R.id.edit_child_progress);
        progressBar.setVisibility(View.GONE);

        mChildRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mChildRef.keepSynced(true);

        firebaseAuth = FirebaseAuth.getInstance();
    }
}