package com.example.mohammad.tennisclub;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohammad.tennisclub.model.Session;

import java.util.ArrayList;
import java.util.HashMap;

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

//        HashMap<String, ArrayList<Session>> session = new HashMap<>();
//        ArrayList<Session> sessions = new ArrayList<>();
//        sessions.add(new Session("Monday, 19 Mar 2018", "11:00"));
//        sessions.add(new Session("Monday, 19 Mar 2018", "12:00"));
//        session.put("Monday, 19 Mar 2018", sessions);
//        sessions = new ArrayList<>();
//        sessions.add(new Session("Tuesday, 20 Mar 2018", "09:00"));
//        sessions.add(new Session("Tuesday, 20 Mar 2018", "12:00"));
//        sessions.add(new Session("Tuesday, 20 Mar 2018", "15:00"));
//        sessions.add(new Session("Tuesday, 20 Mar 2018", "18:00"));
//        session.put("Tuesday, 20 Mar 2018", sessions);
//        sessions = new ArrayList<>();
//        sessions.add(new Session("Wednesday, 21 Mar 2018", "14:00"));
//        sessions.add(new Session("Wednesday, 21 Mar 2018", "16:00"));
//        sessions.add(new Session("Wednesday, 21 Mar 2018", "17:00"));
//        session.put("Wednesday, 21 Mar 2018", sessions);

//        ExpandableListView listView = rootView.findViewById(R.id.elv_booking_options);
//        listView.setAdapter(new ExpandableListAdapter(session));
//        listView.expandGroup(0);
//        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Toast.makeText(getContext(), "Clicked " + groupPosition + "." + childPosition, Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        return rootView;
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private HashMap<String, ArrayList<Session>> sessions;

        public ExpandableListAdapter(HashMap<String, ArrayList<Session>> sessions) {
            this.sessions = sessions;
        }

        @Override
        public Session getChild(int groupPosition, int childPosititon) {
            return sessions.get(getKey(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return getChild(groupPosition, childPosition).hashCode();
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.list_item_centred, null);
                holder.textView = view.findViewById(R.id.tv_item);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Session session = getChild(groupPosition, childPosition);
//            holder.textView.setText(session.getTime());
            return view;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return sessions.get(getKey(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return sessions.get(getKey(groupPosition));
        }

        @Override
        public int getGroupCount() {
            return sessions.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return getGroup(groupPosition).hashCode();
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.list_item_session_header, null);
                holder.textView = view.findViewById(R.id.tv_header);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            String sessionDate = (String) sessions.keySet().toArray()[groupPosition];
            holder.textView.setText(sessionDate);
            return view;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private String getKey(int groupPosition) {
            return (String) sessions.keySet().toArray()[groupPosition];
        }

        private class ViewHolder {
            public TextView textView;
        }
    }

}