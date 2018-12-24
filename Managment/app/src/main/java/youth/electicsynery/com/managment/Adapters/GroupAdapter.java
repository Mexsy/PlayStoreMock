package youth.electicsynery.com.managment.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import youth.electicsynery.com.managment.Dialogs.NewMemberDialog;
import youth.electicsynery.com.managment.Objects.GroupItem;
import youth.electicsynery.com.managment.Objects.ListItem;
import youth.electicsynery.com.managment.R;

/**
 * Created by Emeka on 10/25/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<GroupItem> listItems;
    private ArrayList<GroupItem> original_list;
    private ListItem listItem;
    private GroupItem GroupITEM;

    public GroupAdapter(ArrayList<GroupItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        this.original_list = listItems;
    }

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new GroupAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupITEM = listItems.get(position);
        /*
        for (int i = 0; i<listItems.size();i++){
            ListItem listItem1 = listItems.get(i);
            System.out.println("Name of youth: "+listItem1.getName()+" number of youth: "+listItem1.getNumber());
            System.out.println("Number to Call: "+listItem.getNumber());
        }
         */
        holder.mTextViewNumberOfMembers.setText(GroupITEM.getNumberOfMembers());
        holder.mTextViewGName.setText(GroupITEM.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        //holder.mTextViewDesc.setText(listItem.getNumber());
        //holder.callButton.setFocusable(false);
        //holder.emailButton.setFocusable(false);
        //holder.textButton.setFocusable(false);
        //holder.editButton.setFocusable(false);
    }

    @Override
    public int getItemCount() {

        return listItems.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listItems = (ArrayList<GroupItem>) results.values;
                GroupAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<GroupItem> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = original_list;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected List<GroupItem> getFilteredResults(String constraint) {
        List<GroupItem> results = new ArrayList<>();

        for (GroupItem item : original_list) {
            if (item.getName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public TextView mTextViewNumberOfMembers;
        public TextView mTextViewGName;
        public TextView mTextViewEmail;
        public ImageButton callButton;
        public ImageButton emailButton;
        public ImageButton textButton;
        public ImageButton editButton;
        public ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.group_card);
            mTextViewNumberOfMembers = v.findViewById(R.id.group_number);
            mTextViewGName = v.findViewById(R.id.group_name);
            mTextViewEmail = v.findViewById(R.id.email_address);
            callButton = v.findViewById(R.id.call);
            emailButton = v.findViewById(R.id.email_person);
            textButton = v.findViewById(R.id.text_message);
            editButton = v.findViewById(R.id.edit_person);
        }
    }

}
