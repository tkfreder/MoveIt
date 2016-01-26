package com.tinakit.moveit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.tinakit.moveit.R;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 12/28/2015.
 */
public class UserStatsRecyclerAdapter extends RecyclerView.Adapter<UserStatsRecyclerAdapter.CustomViewHolder>  {

    private Context mContext;
    private FragmentActivity mActivity;
    private List<User> mUserList;


    public UserStatsRecyclerAdapter(Context context, FragmentActivity activity, List<User> userList) {

        mContext = context;
        mActivity = activity;
        mUserList = userList;

        }


    @Override
    public UserStatsRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stat_list_item_parent, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;

        }

    @Override
    public void onBindViewHolder(final UserStatsRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        User user = mUserList.get(i);

        //customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mActivity.getPackageName()));
        customViewHolder.userName.setText(user.getUserName());
        customViewHolder.points.setText(String.valueOf(user.getPoints()));
        customViewHolder.itemView.setTag(user);

        //customViewHolder.arcChart.configureAngles(280, 0);
        customViewHolder.arcChart.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                //.setInitialVisibility(true)
                //.setLineWidth(32f)
                .build());

        //Create data series track
        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                //.setLineWidth(32f)
                .build();

        int series1Index = customViewHolder.arcChart.addSeries(seriesItem1);

        /*customViewHolder.arcChart.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());*/
    }

    @Override
    public int getItemCount() {
        return (null != mUserList ? mUserList.size() : 0);
        }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        //protected ImageView avatar;
        protected TextView userName;
        protected TextView points;
        protected DecoView arcChart;

        public CustomViewHolder(View view) {
            super(view);

            //this.avatar = (ImageView) view.findViewById(R.id.avatar);
            this.userName= (TextView) view.findViewById(R.id.userName);
            this.points = (TextView) view.findViewById(R.id.points);
            this.arcChart = (DecoView)view.findViewById(R.id.arcChart);
        }
    }

}
