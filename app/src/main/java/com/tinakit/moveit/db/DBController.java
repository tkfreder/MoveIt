package com.tinakit.moveit.db;

import android.app.Activity;
import android.util.Log;

import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Tina on 3/29/2016.
 */
public class DBController {

    private static final String TAG = "DBCONTROLLER";

    @Inject
    FitnessDBHelper mFitnessDBHelper;

    public Observable<List<User>> getUsers(Activity activity) {
        // Dagger 2 injection
        ((CustomApplication)activity.getApplication()).getAppComponent().inject(this);

        return makeObservable(mFitnessDBHelper.getUsers())
                .subscribeOn(Schedulers.computation()); // note: do not use Schedulers.io()
    }

    private static <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(
                new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            subscriber.onNext(func.call());
                        } catch(Exception ex) {
                            Log.e(TAG, "Error reading from the database", ex);
                        }
                    }
                });
    }


}


