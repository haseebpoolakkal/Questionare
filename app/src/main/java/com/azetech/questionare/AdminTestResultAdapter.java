package com.azetech.questionare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminTestResultAdapter extends RecyclerView.Adapter<AdminTestResultAdapter.TestsViewHolder> implements Filterable {

    List<String> nameList;
    List<String> nameListAll;
    Context ctx;

    public AdminTestResultAdapter(List<String> list, Context ctx) {
        this.nameList = list;
        this.nameListAll = list;
        this.ctx = ctx;
    }


    @NonNull
    @Override
    public AdminTestResultAdapter.TestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = layoutInflater.inflate(R.layout.admin_test_result_layout, viewGroup, false);
        AdminTestResultAdapter.TestsViewHolder viewAdapter = new AdminTestResultAdapter.TestsViewHolder(listItem);
        return viewAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull TestsViewHolder holder, int position) {
        final String name = nameListAll.get(position);
        holder.name.setText(name);

        holder.result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, AdminQuestionResult.class);
                intent.putExtra("test_name", name);
                ctx.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return nameListAll.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    nameListAll = nameList;
                } else {
                    List<String> filteredList = new ArrayList<>();
                    for (String row : nameList) {
                        if (row.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    nameListAll = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = nameListAll;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                nameListAll = (List<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class TestsViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        RelativeLayout result;

        public TestsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.test_layout_name);
            result = itemView.findViewById(R.id.admin_test_layout_view_result);
        }
    }

}
