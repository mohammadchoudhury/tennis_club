package com.example.mohammad.tennisclub;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SessionBookingFragment extends Fragment {

    private static final String ARG_SESSION_TYPE = "type";
    private SessionType mSessionType;

    public enum SessionType {
        PRIVATE, GROUP
    }

    public SessionBookingFragment() {
    }

    public static SessionBookingFragment newInstance(SessionType type) {
        SessionBookingFragment fragment = new SessionBookingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SESSION_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_session_booking, container, false);

        mSessionType = (SessionType) getArguments().getSerializable(ARG_SESSION_TYPE);

        ImageView sessionImage = ((ImageView) rootView.findViewById(R.id.iv_session_icon));
        if (mSessionType == SessionType.PRIVATE) {
            sessionImage.setImageResource(R.drawable.ic_racket);
        } else {
            sessionImage.setImageResource(R.drawable.ic_balls);
        }


        return rootView;
    }

}
