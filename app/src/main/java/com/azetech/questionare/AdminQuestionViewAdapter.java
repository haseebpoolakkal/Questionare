package com.azetech.questionare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminQuestionViewAdapter extends RecyclerView.Adapter<AdminQuestionViewAdapter.ViewHolder> {

    HashMap questionMap;
    List numberList;
    Context context;

    public AdminQuestionViewAdapter(Context context, HashMap questionMap, List numberList) {
        this.context = context;
        this.questionMap = questionMap;
        this.numberList = numberList;
    }

    @NonNull
    @Override
    public AdminQuestionViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.admin_question_view_layout, parent, false);
        AdminQuestionViewAdapter.ViewHolder viewHolder = new AdminQuestionViewAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminQuestionViewAdapter.ViewHolder holder, int position) {
        String que = String.valueOf(numberList.get(position));
        holder.number.setText(que);
        holder.question.setText(String.valueOf(questionMap.get(que)));
    }

    @Override
    public int getItemCount() {
        return questionMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView question, number;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.admin_question_view_question_number);
            question = (TextView) itemView.findViewById(R.id.admin_question_view_question);
        }
    }
}
