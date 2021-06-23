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

public class AdminTestNameAdapter extends RecyclerView.Adapter<AdminTestNameAdapter.TestsViewHolder> implements Filterable {

    List<String> nameList;
    List<String> nameListAll;
    String category = null;
    Context ctx;

    public AdminTestNameAdapter(List<String> list, String category, Context ctx) {
        this.nameList = list;
        this.nameListAll = list;
        this.category = category;
        this.ctx = ctx;
    }


    @NonNull
    @Override
    public AdminTestNameAdapter.TestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = layoutInflater.inflate(R.layout.admin_test_layout, viewGroup, false);
        AdminTestNameAdapter.TestsViewHolder viewAdapter = new AdminTestNameAdapter.TestsViewHolder(listItem);
        return viewAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull TestsViewHolder holder, int position) {
        final String name = nameListAll.get(position);
        holder.name.setText(name);

        holder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, AdminViewQuestion.class);
                intent.putExtra("test_name", name);
                intent.putExtra("category", category);
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
        RelativeLayout show;

        public TestsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.admin_test_layout_name);
            show = itemView.findViewById(R.id.admin_test_layout_view_name);
        }
    }

}
