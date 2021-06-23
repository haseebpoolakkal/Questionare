package com.azetech.questionare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminViewQuestion extends AppCompatActivity {

    private TextView questionNumber, testMark;
    private RecyclerView recyclerView;
    private LottieAnimationView progress, emptyAnim;

    private DatabaseReference mTestRef;
    AdminQuestionViewAdapter adapter;

    Convert convert = new Convert();
    String testName, category, totalMark = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_question);

        testName = getIntent().getStringExtra("test_name");
        category = getIntent().getStringExtra("category");

        init();

        showData();
    }

    private void showData() {
        try {
            final List numberList = new ArrayList();
            final HashMap questionMap = new HashMap();
            mTestRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            String test = convert.ObjectToString(snapshot.getKey());
                            String mark = convert.ObjectToString(snapshot.child("mark").getValue());
                            totalMark = convert.addition(totalMark, mark);
                            String question = convert.ObjectToString(snapshot.child("question").getValue());
                            numberList.add(test);
                            questionMap.put(test, question);
                        }
                        adapter = new AdminQuestionViewAdapter(getApplicationContext(), questionMap, numberList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        testMark.setText(totalMark);
                    }
                    else {
                        emptyAnim.setVisibility(View.VISIBLE);
                        emptyAnim.playAnimation();
                    }
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    emptyAnim.setVisibility(View.VISIBLE);
                    emptyAnim.playAnimation();
                    progress.setVisibility(View.GONE);
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        questionNumber = (TextView) findViewById(R.id.admin_view_question_test_name);
        questionNumber.setText(testName);
        testMark = (TextView) findViewById(R.id.admin_view_question_mark);

        recyclerView = (RecyclerView) findViewById(R.id.admin_view_question_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progress = (LottieAnimationView) findViewById(R.id.admin_view_question_progress);
        progress.playAnimation();
        emptyAnim = (LottieAnimationView) findViewById(R.id.admin_view_question_empty_anim);
        emptyAnim.setVisibility(View.GONE);

        mTestRef = FirebaseDatabase.getInstance().getReference().child("Tests").child(category).child(testName).child("Questions");
        mTestRef.keepSynced(true);
    }
}