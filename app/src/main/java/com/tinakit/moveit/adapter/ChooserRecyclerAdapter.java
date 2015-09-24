package com.tinakit.moveit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.model.ActivityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 9/24/2015.
 */
public class ChooserRecyclerAdapter extends RecyclerView.Adapter<ChooserRecyclerAdapter.CustomViewHolder> {

    private List<ActivityType> mActivityTypeList = new ArrayList<>();
    private Context mContext;

    public ChooserRecyclerAdapter(Context context) {
        mContext = context;

        for (ActivityType activityType : ActivityType.values()){

            mActivityTypeList.add(activityType);
        }
    }

    @Override
    public ChooserRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_type_list_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChooserRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        ActivityType activityType = mActivityTypeList.get(i);

        // Populate data from ActivityType data object
        customViewHolder.activityName.setText(activityType.getName());
        customViewHolder.activityName.setTag(activityType);
        customViewHolder.activityIcon.setImageResource(mContext.getResources().getIdentifier(activityType.getName() + "_icon_small", "drawable", mContext.getPackageName()));

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView activityName;
        protected ImageView activityIcon;

        public CustomViewHolder(View view) {
            super(view);
            this.activityName = (TextView) view.findViewById(R.id.activityName);
            this.activityIcon = (ImageView) view.findViewById(R.id.activityIcon);
        }
    }

}
