package com.tinakit.moveit.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.StatListAdapter;
import com.tinakit.moveit.model.StatInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tina on 7/2/2015.
 */
public class UserSummaryFragment extends Fragment {

    private ListView mStatListView;
    private StatListAdapter mStatListAdapter;
    private Button mUpdateButton;
    private TextView mHeadingTextView;

    //constants
    public static final String USER_COIN_TOTAL = "USER_COIN_TOTAL";
    public static final String USER_ID = "USER_ID";

    //TODO: to be replaced by UserId from SQLite
    protected static final int USER1_ID = 1;
    protected static final int USER2_ID = 2;
    protected static final int USER3_ID = 3;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_summary, parent, false);

        //wire up UI widgets
        mUpdateButton = (Button) view.findViewById(R.id.updateButton);
        mHeadingTextView = (TextView)view.findViewById(R.id.heading);

        /*
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //save the coin totals in SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginFragment.SHARED_PREFERENCES_MOVEIT, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                //loop through list view to get value out of edittext and save it to SharedPreferences

                for (int i = 0; i < mStatListView.getCount(); i++) {
                    View view = mStatListView.getAdapter().getView(i, null, null);

                    EditText CoinEditText = (EditText) view.findViewById(R.id.coinTotal);
                    TextView userId = (TextView)view.findViewById(R.id.userId);
                    CoinEditText.getText().toString();
                    editor.putInt("coinTotal" + userId.getText(), Integer.parseInt(CoinEditText.getText().toString()));
                }

                editor.commit();

            }
        });
*/
        //listview
        mStatListView = (ListView) view.findViewById(R.id.statListView);
        mStatListAdapter = new StatListAdapter(getActivity());
        mStatListView.setAdapter(mStatListAdapter);

        //get list data from string resource
        //TODO: to be replaced with SQLite data source
        List<StatInfo> statList = new ArrayList<StatInfo>();

        //get usernames from SharedPreferences
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginFragment.SHARED_PREFERENCES_MOVEIT, Context.MODE_PRIVATE);

        //get coin totals
        String commaDelimitedCoins = sharedPreferences.getString(RegisterUserFragment.SHARED_PREFERENCES_COINS, "");
        List<String> coinList = new ArrayList<String>(Arrays.asList(commaDelimitedCoins.split(",")));


        //the first user is the parent, do not display the first user
        if (commaDelimitedCoins.equals("") || coinList.size() == 1) {
            mHeadingTextView.setVisibility(View.VISIBLE);
        } else {

            String commaDelimitedUsernames = sharedPreferences.getString(RegisterUserFragment.SHARED_PREFERENCES_USERNAMES, "");
            List<String> usernameList = new ArrayList<String>(Arrays.asList(commaDelimitedUsernames.split(",")));

            //the first user is the parent, do not display the first user, start index at 1
            for (int i = 1; i < usernameList.size(); i++) {
                StatInfo statInfo = new StatInfo();
                statInfo.setUserName(usernameList.get(i));
                statInfo.setCoinTotal(Integer.parseInt(coinList.get(i)));

                //add to list
                statList.add(statInfo);
            }

            //set adapter for listview
            mStatListAdapter.setList(statList);
        }



        return view;

    }
}
