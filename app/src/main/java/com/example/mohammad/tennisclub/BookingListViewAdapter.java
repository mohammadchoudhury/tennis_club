package com.example.mohammad.tennisclub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohammad.tennisclub.model.Booking;
import com.example.mohammad.tennisclub.model.Session;

import java.util.List;

public class BookingListViewAdapter extends BaseAdapter {

    List<Booking> bookings;

    BookingListViewAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public int getCount() {
        return bookings.size();
    }

    @Override
    public Session getItem(int position) {
        return bookings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return bookings.get(position).hashCode();
    }

    @Override
    public View getView(int position, View listItemView, ViewGroup parent) {
        if (listItemView == null) {
            listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_booking, parent, false);
        }

        Session session = bookings.get(position);
        ImageView sessionImage = listItemView.findViewById(R.id.iv_session_icon);
        String sessionType = "";
        if (session.getType().equalsIgnoreCase("private")){
            sessionImage.setImageResource(R.drawable.ic_racket);
            sessionType = "Private Session";
        } else if (session.getType().equalsIgnoreCase("group")){
            sessionImage.setImageResource(R.drawable.ic_balls);
            sessionType = "Group Session";
        } else {
            sessionImage.setImageResource(R.drawable.ic_court);
            sessionType = "Court Booking";
        }
        ((TextView) listItemView.findViewById(R.id.tv_item_session_type)).setText(sessionType);
        ((TextView) listItemView.findViewById(R.id.tv_item_date)).setText(session.getDateString());
        ((TextView) listItemView.findViewById(R.id.tv_item_time)).setText(session.getTimeString());
        if (session instanceof Booking) {
            ((TextView) listItemView.findViewById(R.id.tv_item_court)).setText(((Booking) session).getCourt());
        } else {
            ((TextView) listItemView.findViewById(R.id.tv_item_court)).setText("Court 5");
        }

        return listItemView;
    }

}