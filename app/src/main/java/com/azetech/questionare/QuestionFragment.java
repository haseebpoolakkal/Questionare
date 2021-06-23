package com.azetech.questionare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionFragment extends Fragment {

    private RelativeLayout questionContainer, scoreContainer, warningLayout;
    //    Question Container
    private TextView testName, currentNumberOfQue, totalNumberOfQue, numberOfSec, questionText, categoryText;
    private RadioButton radio1, radio2, radio3, radio4, radioButton;
    private RadioGroup optionGroup;
    private ProgressBar timeProgress;
    private Button nextButton;

    //    Score Container
    private TextView scoreText;
    private LottieAnimationView animationView;

    private DatabaseReference mTestRef, mUsersRef, mChildRef;
    private FirebaseAuth firebaseAuth;
    String uid = null, testNameText = null;
    private boolean isStart = true;

    Convert convert = new Convert();
    int timer = 0, totalMark = 0;
    CountDownTimer countDownTimer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_question, container, false);

        testNameText = getArguments().getString("test");


        init(view);

        getCategory();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                String total = convert.ObjectToString(totalNumberOfQue.getText());
                String current = convert.ObjectToString(currentNumberOfQue.getText());
                final String category = convert.ObjectToString(categoryText.getText());
                final String test = convert.ObjectToString(testName.getText());

                if (total.equals(current)) {
                    countDownTimer.cancel();
                    int selectedId = optionGroup.getCheckedRadioButtonId();

                    radioButton = (RadioButton) view.findViewById(selectedId);

                    addMark(convert.ObjectToString(radioButton.getText()), category, test, current, true);

                } else if (!radio1.isChecked() && !radio2.isChecked() && !radio3.isChecked() && !radio4.isChecked()) {

                    Toast.makeText(getActivity(), "Please Select an option", Toast.LENGTH_SHORT).show();

                } else {
                    countDownTimer.cancel();
                    int selectedId = optionGroup.getCheckedRadioButtonId();

                    radioButton = (RadioButton) view.findViewById(selectedId);

                    addMark(convert.ObjectToString(radioButton.getText()), category, test, current, false);

                }
            }
        });

        return view;
    }

    private void addMark(final String selectOption, final String category, final String test, final String position, final boolean isLast) {
        mTestRef.child(category)
                .child(test)
                .child("Questions")
                .child(position)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String answer = convert.ObjectToString(dataSnapshot.child("answer").getValue()).toLowerCase();
                            final String mark = convert.ObjectToString(dataSnapshot.child("mark").getValue());

                            if (answer.equals(selectOption.toLowerCase())) {
                                totalMark += convert.StringToInt(mark);
                            }
                            if (isLast) {
                                uploadMark();
                            } else {
                                nextQuestion(category, test);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getScorePage() {
        String test = convert.ObjectToString(testName.getText());
        mChildRef
                .child(firebaseAuth.getUid())
                .child(test)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String mark = convert.ObjectToString(dataSnapshot.child("mark").getValue());
                            scoreText.setText(mark);
                            questionContainer.setVisibility(View.GONE);
                            scoreContainer.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void uploadMark() {
        String test = convert.ObjectToString(testName.getText());
        if (totalMark == 0) {
            timeProgress.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Warning!!");
            builder.setMessage("You didn't attempt any question. Do you want to try again?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().recreate();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().startActivity(new Intent(getActivity(), Tests.class));
                    getActivity().finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            mChildRef.child(firebaseAuth.getUid())
                    .child(test)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.child("mark").getRef().setValue(convert.IntToString(totalMark));
                            dataSnapshot.child("date").getRef().setValue(convert.getDate());
                            getScorePage();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void nextQuestion(String category, String test) {
        countDownTimer.cancel();
        String total = convert.ObjectToString(totalNumberOfQue.getText());
        String current = convert.ObjectToString(currentNumberOfQue.getText());
        String currentNumber = convert.increment(current, 1);
        if (total.equals(current) || convert.StringToInt(currentNumber) > convert.StringToInt(total)) {
            uploadMark();
        } else {
            showQuestion(category, test, currentNumber);
            currentNumberOfQue.setText(currentNumber);
        }
    }

    private void startTimer(final String time) {
        final int totalTime = convert.StringToInt(time);
        timer = totalTime;
        numberOfSec.setText(convert.IntToString(timer) + "s");
        timeProgress.setVisibility(View.VISIBLE);
        timeProgress.setProgress(timer);
        countDownTimer = new CountDownTimer(totalTime * 1000, 1000) {
            @Override
            public void onTick(long l) {
                timer--;
                timeProgress.setProgress(timer);
                numberOfSec.setText(String.valueOf(l / 1000));
            }

            @Override
            public void onFinish() {
                timeProgress.setProgress(100);
                String category = convert.ObjectToString(categoryText.getText());
                String test = convert.ObjectToString(testName.getText());

                nextQuestion(category, test);
            }
        };
        countDownTimer.start();
    }

    private void hideWaring(final String time) {
        final int interval = 3000;
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                warningLayout.setVisibility(View.GONE);
                questionContainer.setVisibility(View.VISIBLE);
                isStart = false;
                startTimer(time);
            }
        };
        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);
    }


    private void showQuestion(String category, String test, String position) {
        String totalQue = convert.ObjectToString(totalNumberOfQue.getText());
        if (convert.StringToInt(position) <= convert.StringToInt(totalQue)) {
            optionGroup.clearCheck();
            final List optionList = new ArrayList();
            mTestRef.child(category)
                    .child(test)
                    .child("Questions")
                    .child(position)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String question = convert.ObjectToString(dataSnapshot.child("question").getValue());
                                String option1 = convert.ObjectToString(dataSnapshot.child("answer").getValue());
                                String option2 = convert.ObjectToString(dataSnapshot.child("option_1").getValue());
                                String option3 = convert.ObjectToString(dataSnapshot.child("option_2").getValue());
                                String option4 = convert.ObjectToString(dataSnapshot.child("option_3").getValue());
                                String time = convert.ObjectToString(dataSnapshot.child("time").getValue());
                                optionList.add(option1);
                                optionList.add(option2);
                                optionList.add(option3);
                                optionList.add(option4);
                                Collections.shuffle(optionList);

                                radio1.setText(convert.ObjectToString(optionList.get(0)));
                                radio2.setText(convert.ObjectToString(optionList.get(1)));
                                radio3.setText(convert.ObjectToString(optionList.get(2)));
                                radio4.setText(convert.ObjectToString(optionList.get(3)));

                                questionText.setText(question);
                                numberOfSec.setText(time + "s");

                                if (isStart) {
                                    hideWaring(time);
                                } else {
                                    startTimer(time);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void getQuestionNumbers(final String category, final String test) {
        mTestRef.child(category)
                .child(test)
                .child("Questions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String total = convert.ObjectToString(dataSnapshot.getChildrenCount());
                            totalNumberOfQue.setText(total);

                            String current = convert.ObjectToString(currentNumberOfQue.getText());
                            showQuestion(category, test, current);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getCategory() {
        mUsersRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String category = convert.ObjectToString(dataSnapshot.child("category").getValue());
                            categoryText.setText(category);

                            String test = convert.ObjectToString(testName.getText());
                            getQuestionNumbers(category, test);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void init(View view) {

        questionContainer = (RelativeLayout) view.findViewById(R.id.question_question_layout_container);
        questionContainer.setVisibility(View.GONE);
        scoreContainer = (RelativeLayout) view.findViewById(R.id.question_score_layout_container);
        scoreContainer.setVisibility(View.GONE);
        warningLayout = (RelativeLayout) view.findViewById(R.id.question_warning_container);
        warningLayout.setVisibility(View.VISIBLE);


        testName = (TextView) view.findViewById(R.id.question_test_name);
        testName.setText(testNameText);
        currentNumberOfQue = (TextView) view.findViewById(R.id.question_current_number);
        currentNumberOfQue.setText("1");
        totalNumberOfQue = (TextView) view.findViewById(R.id.question_total_number);
        numberOfSec = (TextView) view.findViewById(R.id.question_timer_count);
        questionText = (TextView) view.findViewById(R.id.question_question_text);
        categoryText = (TextView) view.findViewById(R.id.question_test_category);

        radio1 = (RadioButton) view.findViewById(R.id.option_1_radio);
        radio2 = (RadioButton) view.findViewById(R.id.option_2_radio);
        radio3 = (RadioButton) view.findViewById(R.id.option_3_radio);
        radio4 = (RadioButton) view.findViewById(R.id.option_4_radio);

        optionGroup = (RadioGroup) view.findViewById(R.id.radio_group_container);
        optionGroup.clearCheck();

        timeProgress = (ProgressBar) view.findViewById(R.id.question_timer_progress);
        timeProgress.setProgress(0);

        nextButton = (Button) view.findViewById(R.id.question_next_button);

        mTestRef = FirebaseDatabase.getInstance().getReference().child("Tests");
        mTestRef.keepSynced(true);
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);
        mChildRef = FirebaseDatabase.getInstance().getReference().child("Children");
        mChildRef.keepSynced(true);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();

        scoreText = (TextView) view.findViewById(R.id.question_score_result);

        animationView = (LottieAnimationView) view.findViewById(R.id.question_score_congrats_anim);
        animationView.playAnimation();
    }


}