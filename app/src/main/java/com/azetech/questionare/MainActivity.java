package com.azetech.questionare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout testLayout, resultLayout, logoutLayout, aboutLayout, exitLayout, privacyLayout;
    private TextView nameText, completedText, pendingText, categoryText;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mUsersRef, mTestRef, mChildRef;

    String uid = null;
    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getChildDetails();

        testLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Tests.class));
                finish();
            }
        });

        resultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Results.class));
                finish();
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        });

        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutBottomSheet about = new AboutBottomSheet();
                about.show(getSupportFragmentManager(), "About Sheet");
            }
        });

        exitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        privacyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyPopup popup = new PrivacyPopup();
                popup.showPopupWindow(view);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    private void getChildDetails() {
        mUsersRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String name = convert.ObjectToString(dataSnapshot.child("name").getValue());
                            String category = convert.ObjectToString(dataSnapshot.child("category").getValue());
                            nameText.setText(name);
                            categoryText.setText(category);

                            getCompletedTests(category);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getCompletedTests(final String category) {
        final List testList = new ArrayList();
        mChildRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                String name = convert.ObjectToString(snapshot.getKey());
                                testList.add(name);
                            }
                            getPendingTests(testList, category, true);
                            String count = convert.ObjectToString(dataSnapshot.getChildrenCount());
                            completedText.setText(count);
                        }
                        else {
                            getPendingTests(testList, category, false);
                            completedText.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getPendingTests(final List completeList, String category, final boolean isComplete) {
        final List testList = new ArrayList();
        mTestRef.child(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (isComplete){
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    String name = convert.ObjectToString(snapshot.getKey());
                                    testList.add(name);
                                }
                                calculateCount(testList, completeList);
                            }
                            else {
                                String count = convert.ObjectToString(dataSnapshot.getChildrenCount());
                                pendingText.setText(count);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void calculateCount(List testList, List completeList) {
        if (testList != null && completeList != null){
            List finalList = new ArrayList();
            for (Object val : testList){
                if (!completeList.contains(val)){
                    finalList.add(val);
                }
            }
            int count = finalList.size();
            pendingText.setText(convert.IntToString(count));
        }
    }

    private void init() {
        testLayout = (RelativeLayout) findViewById(R.id.home_test_button);
        resultLayout = (RelativeLayout) findViewById(R.id.home_result_button);
        logoutLayout = (RelativeLayout) findViewById(R.id.home_logout_button);
        aboutLayout = (RelativeLayout) findViewById(R.id.home_about_button);
        exitLayout = (RelativeLayout)  findViewById(R.id.home_exit_button);
        privacyLayout = (RelativeLayout)  findViewById(R.id.home_privacy_button);

        nameText = (TextView) findViewById(R.id.child_name);
        categoryText = (TextView) findViewById(R.id.child_category);
        pendingText = (TextView) findViewById(R.id.test_pending_value);
        completedText = (TextView) findViewById(R.id.test_completed_value);

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