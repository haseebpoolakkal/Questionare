package com.azetech.questionare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Tests extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LottieAnimationView progress, emptyAnim;

    private DatabaseReference mTestRef, mUsersRef, mChildRef;
    private FirebaseAuth firebaseAuth;

    String uid = null;
    QuestionsAdapter adapter;
    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);

        init();

        getDetails();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Tests.this, MainActivity.class));
        finish();
    }

    private void getDetails() {
        mUsersRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String category = convert.ObjectToString(dataSnapshot.child("category").getValue());
                            showData(category);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showData(String category) {
        final List testList = new ArrayList();
        mTestRef.child(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                String name = convert.ObjectToString(snapshot.getKey());
                                testList.add(name);
                            }
                            getAttemptedTest(testList);
                        }
                        else {
                            emptyAnim.setVisibility(View.VISIBLE);
                            emptyAnim.playAnimation();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getAttemptedTest(final List testList) {
        final List attemptedList = new ArrayList();
        mChildRef
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        String name = convert.ObjectToString(snapshot.getKey());
                        attemptedList.add(name);
                    }

                }
                getPendingList(testList, attemptedList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPendingList(List testList, List attemptedList) {
        List pendingList = new ArrayList();
        for (Object val : testList){
            if (!attemptedList.contains(val)){
                pendingList.add(val);
            }
        }

        adapter = new QuestionsAdapter(this, pendingList);
        recyclerView.setAdapter(adapter);
        progress.setVisibility(View.GONE);
        if (pendingList.size() == 0){
            emptyAnim.setVisibility(View.VISIBLE);
            emptyAnim.playAnimation();
        }
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.test_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progress = (LottieAnimationView) findViewById(R.id.test_progress);
        progress.playAnimation();
        emptyAnim = (LottieAnimationView) findViewById(R.id.test_empty_anim);
        emptyAnim.setVisibility(View.GONE);


        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        mTestRef = FirebaseDatabase.getInstance().getReference().child("Tests");
        mTestRef.keepSynced(true);
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);
        mChildRef = FirebaseDatabase.getInstance().getReference().child("Children");
        mChildRef.keepSynced(true);
    }
}