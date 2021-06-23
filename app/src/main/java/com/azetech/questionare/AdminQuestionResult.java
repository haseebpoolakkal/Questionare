package com.azetech.questionare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

public class AdminQuestionResult extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LottieAnimationView progress, emptyAnim;
    private EditText searchInput;
    private TextView topName;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mChildRef, mUsersRef, mTestRef;
    AdminResultAdapter adapter;

    String uid = null;
    Convert convert = new Convert();
    String testName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_question_result);

        testName = getIntent().getStringExtra("test_name");

        init();

        getUserName();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void getUserName() {
        final HashMap nameMap = new HashMap();
        final HashMap parentMap = new HashMap();
        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        String key = convert.ObjectToString(snapshot.getKey());
                        String name = convert.ObjectToString(snapshot.child("name").getValue());
                        String parent = convert.ObjectToString(snapshot.child("parent").getValue());
                        nameMap.put(key, name);
                        parentMap.put(key, parent);
                    }

                    getData(nameMap, parentMap);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdminQuestionResult.this, AdminResult.class));
        finish();
    }

    private void getData(final HashMap nameMap, final HashMap parentMap) {
        final  List<resultModel> resultList = new ArrayList<resultModel>();
        mChildRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String key = convert.ObjectToString(snapshot.getKey());
                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                            String name = convert.ObjectToString(snapshot1.getKey());
                            String date = convert.ObjectToString(snapshot1.child("date").getValue());
                            String mark = convert.ObjectToString(snapshot1.child("mark").getValue());
                            if (name.equals(testName)) {
                                resultList.add(new resultModel(name, mark, date, convert.ObjectToString(nameMap.get(key)), convert.ObjectToString(parentMap.get(key))));
                            }
                        }
                    }
                    adapter = new AdminResultAdapter(AdminQuestionResult.this, resultList);
                    recyclerView.setAdapter(adapter);
                    progress.setVisibility(View.GONE);
                }
                else {
                    emptyAnim.setVisibility(View.VISIBLE);
                    emptyAnim.playAnimation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void init() {
        topName = (TextView) findViewById(R.id.admin_test_top_name);
        topName.setText(testName);

        searchInput = (EditText) findViewById(R.id.admin_test_search_input);

        recyclerView = (RecyclerView) findViewById(R.id.admin_test_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progress = (LottieAnimationView) findViewById(R.id.admin_test_progress);
        progress.playAnimation();
        emptyAnim = (LottieAnimationView) findViewById(R.id.admin_test_empty_anim);
        emptyAnim.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();

        mChildRef = FirebaseDatabase.getInstance().getReference().child("Children");
        mChildRef.keepSynced(true);mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);
        mTestRef = FirebaseDatabase.getInstance().getReference().child("Tests");
        mTestRef.keepSynced(true);
    }
}