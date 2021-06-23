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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminChildren extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LottieAnimationView progress, emptyAnim;
    private EditText searchInput;
    private Spinner categorySpinner;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mChildRef, mUsersRef, mTestRef;
    AdminChildrenAdapter adapter;
    List<childrenModel> childList;

    String uid = null;
    Convert convert = new Convert();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_childern);

        init();
        String category = convert.ObjectToString(categorySpinner.getSelectedItem());
        showTests(category);

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

    private void addCategory() {
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (childList != null) {
                    childList.clear();
                    adapter.notifyDataSetChanged();
                    String category = convert.ObjectToString(categorySpinner.getSelectedItem());
                    showTests(category);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdminChildren.this, AdminHome.class));
        finish();
    }

    private void showTests(final String category) {
        childList = new ArrayList<>();
        if (!category.equals("")){
            mUsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            String cat = convert.ObjectToString(snapshot.child("category").getValue());
                            if (cat.equals(category)){
                                String uid = convert.ObjectToString(snapshot.getKey());
                                String name = convert.ObjectToString(snapshot.child("name").getValue());
                                String parent = convert.ObjectToString(snapshot.child("parent").getValue());
                                childList.add(new childrenModel(uid, name, parent, cat));
                            }
                        }

                        adapter = new AdminChildrenAdapter(AdminChildren.this, childList);
                        recyclerView.setAdapter(adapter);
                        if (childList == null){
                            emptyAnim.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        emptyAnim.setVisibility(View.VISIBLE);
                        emptyAnim.playAnimation();
                    }
                    progress.setVisibility(View.GONE);
                    addCategory();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                    emptyAnim.setVisibility(View.VISIBLE);
                }
            });
        }
        else {
            addCategory();
        }
    }


    private void init() {
        searchInput = (EditText) findViewById(R.id.admin_children_search_input);

        recyclerView = (RecyclerView) findViewById(R.id.admin_children_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categorySpinner = (Spinner) findViewById(R.id.admin_children_category_spinner);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.category, R.layout.spinner_item);

        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);

        progress = (LottieAnimationView) findViewById(R.id.admin_children_progress);
        progress.playAnimation();
        emptyAnim = (LottieAnimationView) findViewById(R.id.admin_children_empty_anim);
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