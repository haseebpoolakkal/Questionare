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

public class Results extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LottieAnimationView progress, emptyAnim;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mChildRef, mUsersRef;

    String uid = null;
    Convert convert = new Convert();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Results.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        init();
        showData();
    }

    private void showData() {
        final List<resultModel> resultList = new ArrayList<>();
        mChildRef
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                String name = convert.ObjectToString(snapshot.getKey());
                                String date = convert.ObjectToString(snapshot.child("date").getValue());
                                String mark = convert.ObjectToString(snapshot.child("mark").getValue());
                                resultList.add(new resultModel(name, mark, date, null, null));
                            }
                            ResultAdapter adapter = new ResultAdapter(getApplicationContext(), resultList);

                            recyclerView.setAdapter(adapter);
                            progress.setVisibility(View.GONE);
                        }
                        else {
                            progress.setVisibility(View.GONE);
                            emptyAnim.setVisibility(View.VISIBLE);
                            emptyAnim.playAnimation();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.result_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progress = (LottieAnimationView) findViewById(R.id.result_progress);
        progress.playAnimation();
        emptyAnim = (LottieAnimationView) findViewById(R.id.result_empty_anim);
        emptyAnim.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();

        mChildRef = FirebaseDatabase.getInstance().getReference().child("Children");
        mChildRef.keepSynced(true);mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);
    }
}