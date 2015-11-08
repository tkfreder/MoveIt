package com.tinakit.moveit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.EditActivity;
import com.tinakit.moveit.model.UnitSplit;

import java.util.List;

/**
 * Created by Tina on 10/20/2015.
 */
public class EditActivityRecyclerAdapter extends RecyclerView.Adapter<EditActivityRecyclerAdapter.CustomViewHolder> {

    private List<UnitSplit> mUnitSplitList;
    private Context mContext;
    private EditActivity mEditActivity;

    public EditActivityRecyclerAdapter(Context context, List<UnitSplit> unitList, EditActivity editActivity) {

        mContext = context;
        mUnitSplitList = unitList;
        mEditActivity = editActivity;
    }

    @Override
    public EditActivityRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_activity_list_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EditActivityRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        UnitSplit unitSplit = mUnitSplitList.get(i);

        customViewHolder.stats.setText("speed=" + String.format("%.0f", unitSplit.getSpeed()) + "mi/hr" + " calories=" + String.format("%.0f", unitSplit.getCalories()));

        Picasso.with(mContext)
                .load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + unitSplit.getLocation().getLatitude() + "," + unitSplit.getLocation().getLongitude() +
                        "&fov=90&heading=" + unitSplit.getBearing() + "&pitch=10" +
                        "&key=AIzaSyC5IJ88NXWXdHNazquwM6O5EDaCZ3Daf5Y") //TODO: save API key in some config file
                        //.resize(50, 50)
                        //.centerCrop()
                .into(customViewHolder.streetView);


    }

    @Override
    public int getItemCount() {
        return (null != mUnitSplitList ? mUnitSplitList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView streetView;
        protected TextView stats;

    public CustomViewHolder(View view) {
            super(view);

        this.streetView = (ImageView)view.findViewById(R.id.streetView);
        this.stats = (TextView)view.findViewById(R.id.stats);
        //do nothing for now.

        }
    }
}
