package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Users> mConnectors;
 //   private Context context;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public ContactsAdapter(List<Users> connectors) {
        mConnectors = connectors;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.contact_view);
            messageButton = (Button) itemView.findViewById(R.id.message_button);
            //messageButton.setOnClickListener(this); // future purpose.
        }

        //future purpose
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent (v.getContext(), MainActivity.class);
//            Log.d("DB","intent"+intent);
//            v.getContext().startActivity(intent);
//           // connect.change_activity(mConnectors.get(position),intent);
//        }
    }

    // ... constructor and member variables

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.recycler_items, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ContactsAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Users connect = mConnectors.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(connect.username);
        Button button = viewHolder.messageButton;
        button.setText("Connect");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DB", "onClick: view"+viewHolder.getAdapterPosition());
                Log.d("DB","user data for selection"+mConnectors.get(position)+" "+connect);
                //ContactsAdapter.this.context.startActivity(new Intent(ContactsAdapter.this.context,Message_activity.class));
                Intent intent = new Intent (v.getContext(), Message_activity_new.class);
                intent.putExtra("Object",mConnectors.get(position));
                Log.d("DB","intent"+intent);
                v.getContext().startActivity(intent);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mConnectors.size();
    }
}