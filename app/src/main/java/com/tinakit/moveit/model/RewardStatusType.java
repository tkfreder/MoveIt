package com.tinakit.moveit.model;

/**
 * Created by Tina on 10/3/2015.
 */
public enum RewardStatusType {

    AVAILABLE (1), //user can redeem this reward if they have enough points
    PENDING(2), //user has requested this reward, waiting for admin to fulfill the request
    DENIED (3), //admin has denied request for this reward
    UNAVAILABLE (4); //this reward is not available for this user

    private final int mRewardStatusId;

    RewardStatusType(int rewardStatusId){

        mRewardStatusId = rewardStatusId;
    }

}
