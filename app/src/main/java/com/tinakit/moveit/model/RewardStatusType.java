package com.tinakit.moveit.model;

/**
 * Created by Tina on 10/3/2015.
 */
public enum RewardStatusType {

    AVAILABLE, //user can redeem this reward if they have enough points
    PENDING, //user has requested this reward, waiting for admin to fulfill the request
    DENIED, //admin has denied request for this reward
    UNAVAILABLE; //this reward is not available for this user


}
