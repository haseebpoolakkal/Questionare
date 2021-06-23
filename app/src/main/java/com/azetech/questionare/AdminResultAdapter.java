package com.azetech.questionare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminResultAdapter extends RecyclerView.Adapter<AdminResultAdapter.ViewHolder> implements Filterable {

    List<resultModel> resultList;
    List<resultModel> resultListAll;
    Context context;

    public AdminResultAdapter(Context context, List<resultModel> resultList) {
        this.context = context;
        this.resultList = resultList;
        this.resultListAll = resultList;
    }

    @NonNull
    @Override
    public AdminResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.admin_result_layout, parent, false);
        AdminResultAdapter.ViewHolder viewHolder = new AdminResultAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminResultAdapter.ViewHolder holder, int position) {
        holder.parent.setText(resultListAll.get(position).parent);
        holder.mark.setText(resultListAll.get(position).mark);
        holder.date.setText(resultListAll.get(position).date);
        holder.name.setText(resultListAll.get(position).childName);
    }

    @Override
    public int getItemCount() {
        return resultListAll.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    resultListAll = resultList;
                } else {
                    List<resultModel> filteredList = new ArrayList<>();
                    for (resultModel row : resultList) {
                        if (row.childName.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    resultListAll = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = resultListAll;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                resultListAll = (List<resultModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, parent, date, mark;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = (TextView) itemView.findViewById(R.id.admin_result_layout_parent_name);
            name = (TextView) itemView.findViewById(R.id.admin_result_layout_child_name);
            date = (TextView) itemView.findViewById(R.id.admin_result_layout_date);
            mark = (TextView) itemView.findViewById(R.id.admin_result_layout_mark);
        }
    }
}
