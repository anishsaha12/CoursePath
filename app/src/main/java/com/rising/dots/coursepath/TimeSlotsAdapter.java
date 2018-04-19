package com.rising.dots.coursepath;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anish on 12/7/2017.
 */

public class TimeSlotsAdapter extends ArrayAdapter<CourseItem> {

    Activity activity;
    int resource;
    ArrayList<CourseItem> data = new ArrayList<CourseItem>();
    ArrayList<String> slots = new ArrayList<String>();
    ArrayList<String> times = new ArrayList<String>();
    ArrayList<String> venues = new ArrayList<String>();
    CourseItem contDet;
    String slot,time,venue;

    public TimeSlotsAdapter(Activity act, int resource, ArrayList<CourseItem> data, ArrayList<String> slots, ArrayList<String> times, ArrayList<String> venues) {
        super(act, resource, data);
        this.activity = act;
        this.resource = resource;
        this.data = data;
        this.slots = slots;
        this.times = times;
        this.venues = venues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        contDet = data.get(position);
        slot = slots.get(position);
        time = times.get(position);
        venue = venues.get(position);
        LayoutInflater inflater=LayoutInflater.from(activity);
        row=inflater.inflate(resource,parent,false);

        CourseHolder holder = new CourseHolder(row);
        row.setTag(holder);

        holder.CourseTitle.setText(contDet.getCOURSE_TITLE());
        holder.CourseCode.setText(contDet.getCOURSE_CODE());
        holder.Category.setText(contDet.getCATEGORY());
        holder.Credits.setText(contDet.getCredits());
        holder.Slots.setText("- "+slot);
        holder.TimeSlot.setText(time);
        holder.Venue.setText("- "+venue);

        return row;
    }

    class CourseHolder{
        TextView TimeSlot;
        TextView CourseCode;
        TextView CourseTitle;
        TextView Category;
        TextView Credits;
        TextView Slots, Venue;

        public CourseHolder(View row){
            TimeSlot = (TextView) row.findViewById(R.id.tvCTime);
            CourseCode = (TextView) row.findViewById(R.id.tvCCode);
            CourseTitle = (TextView) row.findViewById(R.id.tvCTitle);
            Category = (TextView) row.findViewById(R.id.tvCat);
            Credits = (TextView) row.findViewById(R.id.tvCredits);
            Slots = (TextView) row.findViewById(R.id.tvCSlots);
            Venue = (TextView) row.findViewById(R.id.tvCVenue);
        }
    }
}
