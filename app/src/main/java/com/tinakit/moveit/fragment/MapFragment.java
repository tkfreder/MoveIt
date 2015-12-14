package com.tinakit.moveit.fragment;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tinakit.moveit.R;
import com.tinakit.moveit.model.UnitSplit;
import com.tinakit.moveit.service.GoogleApi;
import com.tinakit.moveit.utility.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 12/12/2015.
 */
public class MapFragment implements OnMapReadyCallback {

    // CONSTANTS
    private static final float ZOOM_STREET_ROUTE = 15.0f;

    // INSTANCE FIELDS
    private FragmentManager mFragmentManager;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private GoogleApi mGoogleApi;

    public MapFragment(FragmentManager fragmentManager, GoogleApi googleApi){

        mFragmentManager = fragmentManager;
        mGoogleApi = googleApi;
    }

    public void addMap(ViewGroup container){

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.map_container, mMapFragment).commit();
        mMapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        displayStartMap();
    }


    public void displayStartMap(){

        if (isMapReady() && mGoogleApi.isConnectedToGoogle()){

            mGoogleMap.setContentDescription("Starting point");
            Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApi.client());
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_STREET_ROUTE));

            //start marker
            mGoogleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .position(latLng)
                            .title("start")
            );
        }
    }

    public void displayMap(List<UnitSplit> unitSplitList, float distance){

        if (mGoogleMap != null){

            //ensure map is visible
            mMapFragment.getView().setVisibility(View.VISIBLE);

            //clear out existing markers if any
            mGoogleMap.clear();

            // Override the default content description on the view, for accessibility mode.
            // Ideally this string would be localised.
            mGoogleMap.setContentDescription("Google Map with polylines.");

            ArrayList<LatLng> locationList = new ArrayList<>();
            for ( UnitSplit unitSplit : unitSplitList) {
                locationList.add(new LatLng(unitSplit.getLocation().getLatitude(), unitSplit.getLocation().getLongitude()));
            }

            mGoogleMap.addPolyline((new PolylineOptions().addAll(locationList).color(Color.BLUE)));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(locationList.get(0).latitude, locationList.get(0).longitude), Map.getZoomByDistance(distance)));

            //render markers
            addMarkersToMap(locationList.get(0), locationList.get(locationList.size() - 1));
        }

    }

    public void addMarkersToMap(LatLng start, LatLng end) {

        //start marker
        mGoogleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(start)
                        .title("start")
        );

        //start marker
        mGoogleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(end)
                .title("end"));
    }

    public boolean isMapReady(){

        return mGoogleMap != null;
    }

    public void makeMap(){

        mMapFragment.getView().setVisibility(View.VISIBLE);
        mMapFragment.getMapAsync(this);
    }

    public void setVisibility(int visibility){

        mMapFragment.getView().setVisibility(visibility);
    }
}
