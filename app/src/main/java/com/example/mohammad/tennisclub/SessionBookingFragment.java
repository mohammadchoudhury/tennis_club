package com.example.mohammad.tennisclub;


import android.content.Intent;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Nullable;

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
        final View rootView = inflater.inflate(R.layout.fragment_session_booking, container, false);

        mSessionType = (SessionType) getArguments().getSerializable(ARG_SESSION_TYPE);

        ImageView sessionImage = ((ImageView) rootView.findViewById(R.id.iv_session_icon));

        String sessionType = "";
        if (mSessionType == SessionType.PRIVATE) {
            sessionImage.setImageResource(R.drawable.ic_racket);
            sessionType = "Private";
        } else {
            sessionImage.setImageResource(R.drawable.ic_balls);
            sessionType = "Group";
        }

        final HashMap<String, ArrayList<Session>> sessionByDate = new HashMap<>();
        final ExpandableListView listView = rootView.findViewById(R.id.elv_booking_options);
        ExpandableListAdapter sessionAdapter = new ExpandableListAdapter(sessionByDate);
        listView.setAdapter(sessionAdapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String sessionId = ((Session) parent.getExpandableListAdapter().getChild(groupPosition, childPosition)).getID();
                Intent intent = new Intent(getContext(), ViewSessionActivity.class);
                intent.putExtra("sessionId", sessionId);
                startActivity(intent);
                return true;
            }
        });


        FirebaseFirestore fsdb = FirebaseFirestore.getInstance();
        fsdb.collection("sessions")
                .orderBy("date")
                .whereGreaterThan("date", new Date())
                .whereEqualTo("type", sessionType)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (snapshots != null) {
                            sessionByDate.clear();
                            for (QueryDocumentSnapshot document : snapshots) {
                                Session session = document.toObject(Session.class);
                                session.setID(document.getId());
                                String date = session.getDateString();
                                if (sessionByDate.get(date) == null) {
                                    ArrayList<Session> sessionList = new ArrayList<>();
                                    sessionList.add(session);
                                    sessionByDate.put(date, sessionList);
                                } else {
                                    sessionByDate.get(date).add(session);
                                }
                            }

                            ((ExpandableListAdapter) listView.getExpandableListAdapter()).notifyDataSetChanged();
                            listView.expandGroup(0);
                        }
                    }
                });


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
            SessionViewHolder holder = null;
            if (view == null) {
                holder = new SessionViewHolder();
                view = getLayoutInflater().inflate(R.layout.list_item_two_part_horizontal, null);
                holder.time = view.findViewById(R.id.tv_item1);
                holder.price = view.findViewById(R.id.tv_item2);
                view.setTag(holder);
            } else {
                holder = (SessionViewHolder) view.getTag();
            }
            Session session = getChild(groupPosition, childPosition);
            holder.time.setText(session.getTimeString());
            holder.price.setText(String.valueOf(session.getPriceString()));
            return view;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (sessions.isEmpty()) return 0;
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
            if (sessions.isEmpty()) return -1;
            return getGroup(groupPosition).hashCode();
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
            GroupViewHolder holder = null;
            if (view == null) {
                holder = new GroupViewHolder();
                view = getLayoutInflater().inflate(R.layout.list_item_session_header, null);
                holder.header = view.findViewById(R.id.tv_header);
                view.setTag(holder);
            } else {
                holder = (GroupViewHolder) view.getTag();
            }
            String sessionDate = (String) sessions.keySet().toArray()[groupPosition];
            holder.header.setText(sessionDate);
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
            if (sessions.isEmpty()) return null;
            return (String) sessions.keySet().toArray()[groupPosition];
        }

        private class GroupViewHolder {
            public TextView header;
        }


        private class SessionViewHolder {
            public TextView time;
            public TextView price;
        }
    }

}