package com.tinakit.moveit.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.model.User;

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
    private ImageButton walkButton;
    private ImageButton scooterButton;
    private ImageButton bikeButton;
    private ImageButton hikeButton;
    private Button goButton;

    //TODO: replace test data with intent bundle from login screen
    //UserInfo
    User mUser = new User("Lucy","password",false,40,"bunny");


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

        walkButton = (ImageButton)view.findViewById(R.id.walkButton);
        walkButton.setOnClickListener(ActivityTypeClicked);

        scooterButton = (ImageButton)view.findViewById(R.id.scooterButton);
        scooterButton.setOnClickListener(ActivityTypeClicked);

        bikeButton = (ImageButton)view.findViewById(R.id.bikeButton);
        bikeButton.setOnClickListener(ActivityTypeClicked);

        hikeButton = (ImageButton)view.findViewById(R.id.hikeButton);
        hikeButton.setOnClickListener(ActivityTypeClicked);

        goButton = (Button)view.findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: pass activity details in intent bundle
                Intent intent = new Intent(getActivity(), TrackerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("activity_type", (String)v.getTag());
                bundle.putString("username", "Lucy");
                intent.putExtras(bundle);
                startActivity(intent);
                //startActivity(new Intent(getActivity(), TrackerActivity.class));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private View.OnClickListener ActivityTypeClicked = new View.OnClickListener(){
        public void onClick(View view){
            goButton.setEnabled(true);

            //set tag of button to the tag of the activity_type_button clicked
            goButton.setTag((String)view.getTag());
        }
    };

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
