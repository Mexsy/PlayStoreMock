package youth.electicsynery.com.managment.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import youth.electicsynery.com.managment.Objects.ListItem;
import youth.electicsynery.com.managment.R;

/**
 * Created by Emeka on 9/26/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {
    private String[] mDataset;
    private ArrayList<ListItem> listItems;
    private ArrayList<ListItem> original_list;
    private ListItem listItem;
    private String name;

    public MyAdapter(ArrayList<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        this.original_list = listItems;
    }

    private Context context;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listItems = (ArrayList<ListItem>) results.values;
                MyAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ListItem> filteredResults = null;
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

    protected List<ListItem> getFilteredResults(String constraint) {
        List<ListItem> results = new ArrayList<>();

        for (ListItem item : original_list) {
            if (item.getName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextViewHead;
        public TextView mTextViewDesc;
        public TextView mTextViewEmail;
        public ImageButton callButton;
        public ImageButton emailButton;
        public ImageButton textButton;
        public ImageButton editButton;
        public ViewHolder(View v) {
            super(v);
            mTextViewHead = v.findViewById(R.id.list_header);
            mTextViewDesc = v.findViewById(R.id.list_description);
            mTextViewEmail = v.findViewById(R.id.email_address);
            callButton = v.findViewById(R.id.call);
            emailButton = v.findViewById(R.id.email_person);
            textButton = v.findViewById(R.id.text_message);
            editButton = v.findViewById(R.id.edit_person);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);

        listItem = listItems.get(position);
        /*
        for (int i = 0; i<listItems.size();i++){
            ListItem listItem1 = listItems.get(i);
            System.out.println("Name of youth: "+listItem1.getName()+" number of youth: "+listItem1.getNumber());
            System.out.println("Number to Call: "+listItem.getNumber());
        }
         */
        name = listItem.getName();
        holder.mTextViewHead.setText(name);
        holder.mTextViewDesc.setText(listItem.getNumber());
        holder.mTextViewEmail.setText(listItem.getEmail());
        holder.callButton.setFocusable(false);
        holder.emailButton.setFocusable(false);
        holder.textButton.setFocusable(false);
        holder.editButton.setFocusable(false);
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItem = listItems.get(position);
                System.out.println("Position: "+ position);
                System.out.println("Name: "+listItem.getName());
                System.out.println("Number to Call: "+listItem.getNumber());
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+listItem.getNumber()));
                context.startActivity(intent);
            }
        });
        holder.textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItem = listItems.get(position);
                System.out.println("Name: "+listItem.getName());
                System.out.println("Number to Text: "+listItem.getNumber());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("address", listItem.getNumber());
                //intent.putExtra("sms_body","Body of Message");
                context.startActivity(intent);
            }
        });
        holder.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItem = listItems.get(position);
                System.out.println("Name: "+listItem.getEmail());
                System.out.println("Number to Text: "+listItem.getNumber());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { listItem.getEmail() });
                //intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                //intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                context.startActivity(Intent.createChooser(intent, ""));
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
