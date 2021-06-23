package com.azetech.questionare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    List<resultModel> resultList = new ArrayList();
    Context context;

    public ResultAdapter(Context context, List<resultModel> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.result_layout, parent, false);
        ResultAdapter.ViewHolder viewHolder = new ResultAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ViewHolder holder, int position) {
        holder.name.setText(resultList.get(position).name);
        holder.mark.setText(resultList.get(position).mark);
        holder.date.setText(resultList.get(position).date);
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, date, mark;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.result_layout_name);
            date = (TextView) itemView.findViewById(R.id.result_layout_date);
            mark = (TextView) itemView.findViewById(R.id.result_layout_mark);
        }
    }
}
