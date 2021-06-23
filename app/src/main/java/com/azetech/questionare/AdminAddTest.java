package com.azetech.questionare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddTest extends AppCompatActivity {

    private EditText nameInput;
    private Spinner categoryInput;
    private Button addQuestionButton;

    private DatabaseReference mTestRef, mUsersRef;

    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        init();

        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                validate();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdminAddTest.this, AdminHome.class));
        finish();
    }

    private void validate() {
        String name = convert.ObjectToString(nameInput.getText()).replaceAll("[^a-zA-Z0-9 ]", "").trim();
        String category = convert.ObjectToString(categoryInput.getSelectedItem());

        if (name.equals("")) {
            nameInput.setError("Please Enter");
        } else {
            Intent intent = new Intent(AdminAddTest.this, AdminAddQuestion.class);
            intent.putExtra("name", name);
            intent.putExtra("category", category);
            startActivity(intent);
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    private void init() {
        nameInput = (EditText) findViewById(R.id.add_test_name);
        categoryInput = (Spinner) findViewById(R.id.add_test_category);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.category, R.layout.spinner_item);

        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categoryInput.setAdapter(arrayAdapter);

        addQuestionButton = (Button) findViewById(R.id.add_test_add_question_button);

        mTestRef = FirebaseDatabase.getInstance().getReference().child("Tests");
        mTestRef.keepSynced(true);
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);
    }
}