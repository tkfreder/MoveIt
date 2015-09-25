package com.tinakit.moveit.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.adapter.ChooserRecyclerAdapter;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActivityChooserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivityChooserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityChooserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //UI Widgets
    private TextView mUserName;
    private ImageView mAvatar;
    private RecyclerView mRecyclerView;
    private ChooserRecyclerAdapter mChooserRecyclerAdapter;

    //TODO: replace test data with intent bundle from login screen
    //Session variables
    private User mUser = new User("Lucy","password",false,40,"tiger");


    //TODO: replace image of map with current location using MAPV2
    //http://android-er.blogspot.com/2012/12/get-googlemap-from-mapfragmentsupportma.html

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActivityChooser.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityChooserFragment newInstance() {
        ActivityChooserFragment fragment = new ActivityChooserFragment();
        return fragment;
    }

    public ActivityChooserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_chooser, container, false);

        //TODO: get user details from Intent bundle or SharedPreferences

        //wire up UI widgets
        mUserName = (TextView)view.findViewById(R.id.username);
        mUserName.setText(mUser.getUserName());

        mAvatar = (ImageView)view.findViewById(R.id.avatar);
        mAvatar.setImageResource(getResources().getIdentifier(mUser.getAvatarFileName() , "drawable", getActivity().getPackageName()));

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChooserRecyclerAdapter = new ChooserRecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(mChooserRecyclerAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //TODO: more details on how to have activity and fragments talk to each other
        //http://stackoverflow.com/questions/24777985/how-to-implement-onfragmentinteractionlistener

        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
