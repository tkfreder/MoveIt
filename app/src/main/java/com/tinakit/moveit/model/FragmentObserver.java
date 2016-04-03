package com.tinakit.moveit.model;

import java.util.Observable;

/**
 * Created by Tina on 4/3/2016.
 */
public class FragmentObserver extends Observable {

    @Override
    public void notifyObservers() {
        hasChanged(); // Set the changed flag to true, otherwise observers won't be notified.
        super.notifyObservers();
    }
}
