package com.futronictech.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.R;
import com.futronictech.model.BWStaff_Datum;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This exposes a list of gitHub users data to a RecyclerView.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.BWStaffViewHolder> {

    private ArrayList<BWStaff_Datum> mListData;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final ListAdapterOnClickHandler mClickHandler;
    // Pass in a context
    private Context context;
    /**
     * The interface that receives onClick messages.
     */
    public interface ListAdapterOnClickHandler {
        void onClick(String[] profile);
    }

    /**
     * Constructor for the adapter that accepts clickHandler.
     */
    public ListAdapter(Context context, ListAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        this.context = context;
    }

    /**
     * Cache of the children views for a list item.
     */
    class BWStaffViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView listItemDevView;
        ImageView imageView;

        /*
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * Text and ImageViews.
         * @param itemView The View that was inflated previously.
         */
        BWStaffViewHolder(View itemView) {
            super(itemView);

            listItemDevView = itemView.findViewById(R.id.tv_dev_list);
            imageView = itemView.findViewById(R.id.iv_dev_list);
            itemView.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String firstName = mListData.get(adapterPosition).getFirstName();
            String lastName = mListData.get(adapterPosition).getLastName();
            String userName = lastName + " " + firstName;
            String photoUrl = "";
            String[] profile = {userName,photoUrl};
            mClickHandler.onClick(profile);
        }
    }

    /**
     * This get called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  Used when RecyclerView has more than one type of item.
     * @return 		   A new NumberViewHolder that holds the View for each list item.
     */
    @NonNull
    @Override
    public BWStaffViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.list_item, viewGroup, false);
        return new BWStaffViewHolder(view);
    }
    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, contents of the ViewHolder is updated to display the correct
     * indices in the list for this particular position.
     *
     * @param holder 	The ViewHolder which should be updated to represent the contents of the
     * 					item at the given position in the data set.
     * @param position	The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull BWStaffViewHolder holder, int position) {
        String firstName = mListData.get(position).getFirstName();
        String lastName = mListData.get(position).getLastName();
        String userName = lastName + " " + firstName;
        holder.listItemDevView.setText(userName);

        Picasso.with(context)
                .load("")
                .error(R.drawable.icon)
                .into(holder.imageView);
    }
    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available.
     */
    @Override
    public int getItemCount() {
        if (mListData == null) return 0;
        return mListData.size();
    }

    /**
     * This method is used to set the data on a DevAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new DevAdapter to display it.
     *
     * @param devData The new weather data to be displayed.
     */
    public void setData(ArrayList<BWStaff_Datum> devData) {
        mListData = devData;
        notifyDataSetChanged();
    }
}
