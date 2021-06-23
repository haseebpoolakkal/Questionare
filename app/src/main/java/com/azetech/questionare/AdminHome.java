package com.azetech.questionare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminHome extends AppCompatActivity {

    private RelativeLayout addTestLayout, childrenLayout, addChildLayout, resultLayout, logoutLayout,
            aboutLayout, questionLayout, privacyLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        init();

        addTestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHome.this,AdminAddTest.class));
                finish();
            }
        });

        resultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHome.this, AdminResult.class));
                finish();
            }
        });

        addChildLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHome.this,AdminAddChild.class));
                finish();
            }
        });

        childrenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHome.this,AdminChildren.class));
                finish();
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(AdminHome.this, Login.class));
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

        questionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHome.this, AdminQuestion.class));
                finish();
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

    private void init() {
        addTestLayout = (RelativeLayout) findViewById(R.id.admin_home_add_test_button);
        childrenLayout = (RelativeLayout) findViewById(R.id.admin_home_children_button);
        addChildLayout = (RelativeLayout) findViewById(R.id.admin_home_add_child_button);
        resultLayout = (RelativeLayout) findViewById(R.id.admin_home_result_button);
        logoutLayout = (RelativeLayout) findViewById(R.id.admin_home_logout_button);
        aboutLayout = (RelativeLayout) findViewById(R.id.admin_home_about_button);
        questionLayout = (RelativeLayout)  findViewById(R.id.admin_home_question_button);
        privacyLayout = (RelativeLayout)  findViewById(R.id.admin_home_privacy_button);

        firebaseAuth = FirebaseAuth.getInstance();
    }
}