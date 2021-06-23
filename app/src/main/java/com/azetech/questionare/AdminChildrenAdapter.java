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

public class AdminChildrenAdapter extends RecyclerView.Adapter<AdminChildrenAdapter.ViewHolder> implements Filterable {

    List<childrenModel> childrenList;
    List<childrenModel> childrenListAll;
    Context context;

    public AdminChildrenAdapter(Context context, List<childrenModel> childrenList) {
        this.context = context;
        this.childrenList = childrenList;
        this.childrenListAll = childrenList;
    }

    @NonNull
    @Override
    public AdminChildrenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.admin_children_layout, parent, false);
        AdminChildrenAdapter.ViewHolder viewHolder = new AdminChildrenAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminChildrenAdapter.ViewHolder holder, final int position) {
        holder.parent.setText(childrenListAll.get(position).parent);
        holder.name.setText(childrenListAll.get(position).name);
        holder.category.setText(childrenListAll.get(position).category);

        holder.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdminEditChild.class);
                intent.putExtra("id", childrenListAll.get(position).uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return childrenListAll.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    childrenListAll = childrenList;
                } else {
                    List<childrenModel> filteredList = new ArrayList<>();
                    for (childrenModel row : childrenList) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase()) || row.parent.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    childrenListAll = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = childrenListAll;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                childrenListAll = (List<childrenModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, parent, category;
        RelativeLayout editLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = (TextView) itemView.findViewById(R.id.admin_children_layout_parent_name);
            name = (TextView) itemView.findViewById(R.id.admin_children_layout_child_name);
            category = (TextView) itemView.findViewById(R.id.admin_children_layout_category_name);
            editLayout = (RelativeLayout) itemView.findViewById(R.id.admin_children_layout_edit);
        }
    }
}
