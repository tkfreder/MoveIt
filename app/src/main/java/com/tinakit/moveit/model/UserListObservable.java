package com.tinakit.moveit.model;

import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.module.CustomApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

/**
 * Created by Tina on 3/30/2016.
 */
public class UserListObservable extends Observable {

    private List<User> mUserList;

    public UserListObservable(){
        mUserList = new ArrayList<>();
    }

    public void setValue(List<User> userList)
    {
        mUserList = userList;
        setChanged();
        notifyObservers();
    }
    public List<User> getValue()
    {
        return mUserList;
    }
}
