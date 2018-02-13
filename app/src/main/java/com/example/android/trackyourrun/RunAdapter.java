package com.example.android.trackyourrun;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

/**
 * {@link RunAdapter} is an {@link RecyclerView.Adapter} that can provide the layout
 * for each list item based on a data source, which is a list of {@link Run} objects.
 */
public class RunAdapter extends RecyclerView.Adapter<RunAdapter.MyViewHolder> {

    /**
     * Create a new list of {@link Run} object.
     */
    private List<Run> mRunsList;

    /**
     * Context passed in through the constructor.
     */
    private Context mContext;

    /**
     * Create a new {@link RunAdapter} object.
     *
     * @param context  is the context of the activity.
     * @param runsList is a list of {@link Run} object.
     */
    public RunAdapter(Context context, List<Run> runsList) {
        mContext = context;
        mRunsList = runsList;
    }

    /**
     * Create a RecyclerView OnItemClickListener object.
     */
    private OnItemClickListener mOnItemClickListener;

    /**
     * Setup the RecyclerView item click listener.
     *
     * @param OnItemClickListener is the interface of RecyclerVIew OnItemClickListener.
     */
    public void setOnItemClickListener(OnItemClickListener OnItemClickListener) {
        mOnItemClickListener = OnItemClickListener;
    }

    /**
     * The interface of RecyclerVIew OnItemClickListener.
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mRunsList.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // Get the data model based on position.
        Run run = mRunsList.get(position);

        // Set the distance circle color according to the value.
        GradientDrawable distanceCircle = (GradientDrawable) holder.runDistanceView.getBackground();
        distanceCircle.setColor(getDistanceColor(run.getDistance()));

        holder.runDistanceView.setText(String.valueOf(run.getDistance()));
        // Append a new line of distance unit to the distance circle.
        holder.runDistanceView.append("\n" + run.getDistanceUnit());

        holder.runDateView.setText(run.getDate());
        holder.runTimeView.setText(run.getTime());
        holder.runDuration.setText(run.getDuration());

        // Set onItemClick listener to the list item container.
        if (mOnItemClickListener != null) {
            holder.listItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Open CatalogActivity with item data to edit a run.
                    Intent intent = new Intent(mContext, EditorActivity.class);
                    intent.putExtra("itemId",
                            mRunsList.get(holder.getAdapterPosition()).getId());
                    intent.putExtra("itemDate",
                            mRunsList.get(holder.getAdapterPosition()).getDate());
                    intent.putExtra("itemTime",
                            mRunsList.get(holder.getAdapterPosition()).getTime());
                    intent.putExtra("itemDuration",
                            mRunsList.get(holder.getAdapterPosition()).getDuration());
                    intent.putExtra("itemDistance",
                            mRunsList.get(holder.getAdapterPosition()).getDistance());
                    intent.putExtra("itemDistanceUnit",
                            mRunsList.get(holder.getAdapterPosition()).getDistanceUnit());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout listItemContainer;
        public TextView runDistanceView, runDateView, runTimeView, runDuration;

        public MyViewHolder(View view) {
            super(view);

            listItemContainer = view.findViewById(R.id.item_container);
            runDistanceView = view.findViewById(R.id.item_distance);
            runDateView = view.findViewById(R.id.item_date);
            runTimeView = view.findViewById(R.id.item_time);
            runDuration = view.findViewById(R.id.item_duration);
        }
    }

    /**
     * Helper method that get the color for the distance circle based on the distance of the run.
     *
     * @param distance of the run.
     * @return the color resource id of the distance circle.
     */
    private int getDistanceColor(double distance) {
        int distanceColorResourceId;
        if (distance <= 10) {
            distanceColorResourceId = R.color.distance_one;
        } else if (distance <= 20) {
            distanceColorResourceId = R.color.distance_two;
        } else if (distance <= 30) {
            distanceColorResourceId = R.color.distance_three;
        } else if (distance <= 40) {
            distanceColorResourceId = R.color.distance_four;
        } else if (distance <= 50) {
            distanceColorResourceId = R.color.distance_five;
        } else if (distance <= 60) {
            distanceColorResourceId = R.color.distance_six;
        } else if (distance <= 70) {
            distanceColorResourceId = R.color.distance_seven;
        } else if (distance <= 80) {
            distanceColorResourceId = R.color.distance_eight;
        } else if (distance <= 90) {
            distanceColorResourceId = R.color.distance_nine;
        } else {
            distanceColorResourceId = R.color.distance_ten;
        }

        return ContextCompat.getColor(mContext, distanceColorResourceId);
    }

    /**
     * Helper method that clear the list of {@link RecyclerView} and notify it of the removal.
     */
    public void clear() {
        mRunsList.clear();
        notifyDataSetChanged();
    }

    /**
     * Helper method that pass in the list of {@link RecyclerView} and notify it of the data change.
     *
     * @param runs is a reference of the {@link List<Run>}.
     */
    public void addAll(List<Run> runs) {
        mRunsList.addAll(runs);
        notifyDataSetChanged();
    }

    /**
     * Helper method that remove an item at the position.
     *
     * @param position where the item needs to be removed.
     */
    public void removeItem(int position) {
        mRunsList.remove(position);
        // notify the item removed by position.
        notifyItemRemoved(position);
    }

    /**
     * Helper method that restore the item which just was removed.
     *
     * @param run      is the {@link Run} object which needs to restore.
     * @param position where the item needs to be restored.
     */
    public void restoreItem(Run run, int position) {
        mRunsList.add(position, run);
        // notify item added by position.
        notifyItemInserted(position);
    }
}
