package com.tinakit.moveit.adapter.view_holder;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.tinakit.moveit.R;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.RewardStatusType;
import com.tinakit.moveit.model.User;

/**
 * Created by Tina on 12/23/2015.
 */
public class RewardParentViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180.0f;
    private static final float PIVOT_VALUE = 0.5f;
    private static final long DEFAULT_ROTATE_DURATION_MS = 200;
    private static final boolean HONEYCOMB_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public ImageView avatar;
    public TextView points;
    public ImageButton expandArrow;

    public RewardParentViewHolder(View view) {

        super(view);
        this.avatar = (ImageView)view.findViewById(R.id.avatar);
        this.points = (TextView)view.findViewById(R.id.points);
        this.expandArrow = (ImageButton)view.findViewById(R.id.expandArrow);
    }

    public void bind(Context context, Activity activity, User user){

        avatar.setImageResource(context.getResources().getIdentifier(user.getAvatarFileName(), "drawable", activity.getPackageName()));
        points.setText(String.valueOf(user.getPoints()));
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if(!HONEYCOMB_AND_ABOVE){
            return;
        }

        if(expanded){
            expandArrow.setRotation(ROTATED_POSITION);
        }else{
            expandArrow.setRotation(INITIAL_POSITION);
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (!HONEYCOMB_AND_ABOVE) {
            return;
        }

        RotateAnimation rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                INITIAL_POSITION,
                RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE,
                RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE);
        rotateAnimation.setDuration(DEFAULT_ROTATE_DURATION_MS);
        rotateAnimation.setFillAfter(true);
        expandArrow.startAnimation(rotateAnimation);
    }
}