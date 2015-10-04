package com.tinakit.moveit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.Login;
import com.tinakit.moveit.model.StatInfo;

import java.util.List;

/**
 * Created by Tina on 7/2/2015.
 */
public class StatListAdapter extends BaseAdapter {

    private Context mContext;
    private List<StatInfo> mStatList;
    LayoutInflater mInflater;

    public StatListAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<StatInfo> statList) {
        mStatList = statList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return ((mStatList == null) ? 0 : mStatList.size());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mStatList.get(position);
    }

    class ViewHolder {

        TextView userName;
        EditText coinCount;
        ImageView coinImage;
        //TODO: this should be saved as the rowId from SQLite
        //TextView userId;
        Button updateButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(R.layout.stat_list_item, null);
            holder = new ViewHolder();

            holder.userName = (TextView)convertView.findViewById(R.id.userName);
            holder.coinCount = (EditText)convertView.findViewById(R.id.coinTotal);
            holder.coinImage = (ImageView)convertView.findViewById(R.id.coinImage);
            //holder.userId = (TextView)convertView.findViewById(R.id.userId);
            holder.updateButton = (Button)convertView.findViewById(R.id.updateButton);

            //save the holder the view's tag
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        //get a reference to the data
        StatInfo statInfo = mStatList.get(position);

        // set the text for username and coin total
        holder.userName.setText(statInfo.getUserName());
        holder.coinCount.setText(String.valueOf(statInfo.getCoinTotal()));


        holder.coinImage.setImageResource(R.drawable.gold_coin);
        //holder.userId.setText(String.valueOf(statInfo.getUserId()));
        holder.updateButton.setTag(statInfo.getUserId());

        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get the row from the button by calling getParent()
                LinearLayout row = (LinearLayout)view.getParent();
                EditText coinEditText = (EditText)row.findViewById(R.id.coinTotal);

                //ViewHolder viewHolder = (ViewHolder)view.getTag();
                //TextView idTextView = viewHolder.userId;
                //TextView coinTextView = viewHolder.coinCount;

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Login.SHARED_PREFERENCES_MOVEIT, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String userId = String.valueOf(view.getTag());
                //editor.putInt(UserSummaryFragment.USER_COIN_TOTAL + userId, Integer.parseInt(coinEditText.getText().toString()));
                editor.commit();

                //remove focus from coin edittext box
                coinEditText.clearFocus();

                //display Toast to confirm update
                Toast.makeText(mContext, "Your changes have been saved", Toast.LENGTH_SHORT).show();

            }
        });


        // Return the completed view to render on screen
        return convertView;    }


}
