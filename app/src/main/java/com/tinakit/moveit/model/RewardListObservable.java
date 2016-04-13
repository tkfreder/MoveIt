package com.tinakit.moveit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Tina on 4/12/2016.
 */
public class RewardListObservable extends Observable {

    private List<Reward> mRewardList;

    public RewardListObservable(){
        mRewardList = new ArrayList<>();
    }

    public void setValue(List<Reward> rewardList)
    {
        mRewardList = rewardList;
        setChanged();
        notifyObservers(mRewardList);
    }

    public List<Reward> getValue()
    {
        return mRewardList;
    }
}
