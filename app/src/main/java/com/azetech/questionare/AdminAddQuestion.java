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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminAddQuestion extends AppCompatActivity {

    private EditText questionInput, markInput, timeInput, answerInput, option1Input, option2Input, option3Input;
    private Button addQueButton;
    private TextView questionCount;
    private ProgressBar progressBar;
    Convert convert = new Convert();

    private DatabaseReference mQuestionRef;
    String name, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        name = getIntent().getStringExtra("name");
        category = getIntent().getStringExtra("category");

        init();

        getQuestionCount();
        addQueButton.setOnClickListener(new View.OnClickListener() {
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
        startActivity(new Intent(AdminAddQuestion.this, AdminAddTest.class));
        finish();
    }

    private void getQuestionCount() {
        mQuestionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String count = convert.ObjectToString(dataSnapshot.getChildrenCount());
                    questionCount.setText(count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validate() {
        String question = convert.ObjectToString(questionInput.getText()).trim();
        String mark = convert.ObjectToString(markInput.getText()).trim();
        String time = convert.ObjectToString(timeInput.getText()).trim();
        String answer = convert.ObjectToString(answerInput.getText()).trim();
        String option3 = convert.ObjectToString(option1Input.getText()).trim();
        String option2 = convert.ObjectToString(option2Input.getText()).trim();
        String option1 = convert.ObjectToString(option3Input.getText()).trim();

        if (question.equals("")) {
            questionInput.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (answer.equals("")) {
            answerInput.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (mark.equals("")) {
            markInput.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (time.equals("")) {
            timeInput.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (option1.equals("")) {
            option1Input.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (option2.equals("")) {
            option2Input.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else if (option3.equals("")) {
            option3Input.setError("Please Enter");
            progressBar.setVisibility(View.GONE);
        } else {

            HashMap queMap = new HashMap();
            queMap.put("question", question);
            queMap.put("mark", mark);
            queMap.put("answer", answer);
            queMap.put("option_1", option1);
            queMap.put("option_2", option2);
            queMap.put("option_3", option3);
            queMap.put("time", time);

            final String questionNumber = convert.increment(convert.ObjectToString(questionCount.getText()), 1);


            mQuestionRef.child(questionNumber).setValue(queMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                questionInput.setText("");
                                markInput.setText("");
                                answerInput.setText("");
                                timeInput.setText("");
                                option1Input.setText("");
                                option2Input.setText("");
                                option3Input.setText("");
                                Toast.makeText(AdminAddQuestion.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                                getQuestionCount();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
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
        questionCount = (TextView) findViewById(R.id.question_number_count);
        questionCount.setText("0");

        questionInput = (EditText) findViewById(R.id.popup_question_input);
        markInput = (EditText) findViewById(R.id.popup_mark_input);
        timeInput = (EditText) findViewById(R.id.popup_time_input);
        answerInput = (EditText) findViewById(R.id.popup_answer_input);
        option1Input = (EditText) findViewById(R.id.popup_option_1_input);
        option2Input = (EditText) findViewById(R.id.popup_option_2_input);
        option3Input = (EditText) findViewById(R.id.popup_option_3_input);

        addQueButton = (Button) findViewById(R.id.popup_add_question_button);

        progressBar = (ProgressBar) findViewById(R.id.popup_progress);
        progressBar.setVisibility(View.GONE);

        mQuestionRef = FirebaseDatabase.getInstance().getReference().child("Tests").child(category).child(name).child("Questions");
        mQuestionRef.keepSynced(true);
    }
}