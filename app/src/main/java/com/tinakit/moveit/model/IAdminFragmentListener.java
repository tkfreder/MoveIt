package com.tinakit.moveit.model;

/**
 * Created by Tina on 2/16/2016.
 */
public interface IAdminFragmentListener {

    public void registerObserver(IAdminFragmentObserver observer);

    public void removeObserver(IAdminFragmentObserver observer);

    public void notifyObservers();
}
