package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.tinakit.moveit.R;

/**
 * Created by Tina on 10/18/2015.
 */
public class Pictures extends FragmentActivity {

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    //TODO: DEBUG
    private static final LatLng HOME1 = new LatLng(34.143000, -118.077089);
    private static final LatLng HOME2 = new LatLng(34.143274, -118.077125);
    private static final LatLng HOME3 = new LatLng(34.143273, -118.076434);
    private static final LatLng HOME4 = new LatLng(34.142364, -118.076501);
    private static final LatLng HOME5 = new LatLng(34.142342, -118.078899);
    private static final LatLng HOME6 = new LatLng(34.143240, -118.078939);
    private static final LatLng HOME7 = new LatLng(34.143255, -118.077145);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictures);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        // Only set the panorama to SYDNEY on startup (when no panoramas have been
                        // loaded which is when the savedInstanceState is null).
                        if (savedInstanceState == null) {
                            panorama.setPosition(HOME1);
                        }
                    }
                });
    }
}
