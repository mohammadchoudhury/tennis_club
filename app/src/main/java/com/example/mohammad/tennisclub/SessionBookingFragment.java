package com.example.mohammad.tennisclub;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohammad.tennisclub.model.Session;

import java.util.ArrayList;

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

        ArrayList<Session> sessions = new ArrayList<>();
        sessions.add(new Session("Monday, 19 Mar 2018"));
        sessions.add(new Session("Monday, 19 Mar 2018", "11:00"));
        sessions.add(new Session("Monday, 19 Mar 2018", "12:00"));
        sessions.add(new Session("Tuesday, 20 Mar 2018"));
        sessions.add(new Session("Tuesday, 20 Mar 2018", "09:00"));
        sessions.add(new Session("Tuesday, 20 Mar 2018", "12:00"));
        sessions.add(new Session("Tuesday, 20 Mar 2018", "15:00"));
        sessions.add(new Session("Wednesday, 21 Mar 2018"));
        sessions.add(new Session("Wednesday, 21 Mar 2018", "13:00"));
        sessions.add(new Session("Wednesday, 21 Mar 2018", "14:00"));
        sessions.add(new Session("Wednesday, 21 Mar 2018", "16:00"));
        sessions.add(new Session("Wednesday, 21 Mar 2018", "17:00"));
        ListView listView = rootView.findViewById(R.id.lv_booking_options);
        listView.setAdapter(new SessionAdapter(sessions));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Clicked " + position, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private class SessionAdapter extends BaseAdapter {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_SESSION = 1;
        private ArrayList<Session> sessions;

        public SessionAdapter(ArrayList<Session> sessions) {
            this.sessions = sessions;
        }

        @Override
        public int getItemViewType(int position) {
            return sessions.get(position).getType() == Session.Type.HEADER ? TYPE_HEADER : TYPE_SESSION;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return sessions.size();
        }

        @Override
        public Session getItem(int position) {
            return sessions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return sessions.get(position).hashCode();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                switch (getItemViewType(position)) {
                    case TYPE_HEADER:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_session_header, parent, false);
                        view.setOnClickListener(null);
                        holder.textView = view.findViewById(R.id.tv_header);
                        break;
                    case TYPE_SESSION:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_centred, parent, false);
                        holder.textView = view.findViewById(R.id.tv_item);
                        break;
                }
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            switch (getItemViewType(position)) {
                case TYPE_HEADER:
                    holder.textView.setText(getItem(position).getDate());
                    break;
                case TYPE_SESSION:
                    holder.textView.setText(getItem(position).getTime());
                    break;
            }
            return view;
        }

        private class ViewHolder {
            public TextView textView;
        }
    }
}