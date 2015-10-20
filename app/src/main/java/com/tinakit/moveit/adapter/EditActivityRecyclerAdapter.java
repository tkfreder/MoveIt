package com.tinakit.moveit.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.EditActivity;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.UnitSplitCalorie;

import java.util.List;

/**
 * Created by Tina on 10/20/2015.
 */
public class EditActivityRecyclerAdapter extends RecyclerView.Adapter<EditActivityRecyclerAdapter.CustomViewHolder> {

    private List<UnitSplitCalorie> mUnitSplitList;
    private Context mContext;
    private EditActivity mEditActivity;

    public EditActivityRecyclerAdapter(Context context, List<UnitSplitCalorie> unitList, EditActivity editActivity) {

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

        UnitSplitCalorie unitSplit = mUnitSplitList.get(i);

        LatLng location = new LatLng(unitSplit.getLocation().getLatitude(), unitSplit.getLocation().getLongitude());

        final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);


            customViewHolder.streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                    new OnStreetViewPanoramaReadyCallback(){
                        @Override
                        public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

                            streetViewPanorama.setPosition(SYDNEY);
                        }
                    });

        /*
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        mEditActivity.getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback2(location));
                */


    }

    public class OnStreetViewPanoramaReadyCallback2 implements OnStreetViewPanoramaReadyCallback{

        private final LatLng mLocation;

        public OnStreetViewPanoramaReadyCallback2(LatLng location){
            mLocation = location;
        }
        @Override
        public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

            streetViewPanorama.setPosition(mLocation);
        }
    }

    @Override
    public int getItemCount() {
        return (null != mUnitSplitList ? mUnitSplitList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected SupportStreetViewPanoramaFragment streetViewPanoramaFragment;

    public CustomViewHolder(View view) {
            super(view);

        this.streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        mEditActivity.getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        //do nothing for now.

        }
    }
}