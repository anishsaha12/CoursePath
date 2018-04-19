package com.rising.dots.coursepath;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
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

public class CourseListAdapter extends ArrayAdapter<CourseItem> {

    Activity activity;
    int resource;
    ArrayList<CourseItem> data = new ArrayList<CourseItem>();
    CourseItem contDet;

    public CourseListAdapter(Activity act, int resource, ArrayList<CourseItem> data) {
        super(act, resource, data);
        this.activity = act;
        this.resource = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        contDet = data.get(position);
        LayoutInflater inflater=LayoutInflater.from(activity);
        row=inflater.inflate(resource,parent,false);

        CourseHolder holder = new CourseHolder(row);
        row.setTag(holder);

        holder.CourseTitle.setText(contDet.getCOURSE_TITLE());
        holder.CourseCode.setText(contDet.getCOURSE_CODE());
        holder.Category.setText(contDet.getCATEGORY());
        holder.Credits.setText(contDet.getCredits());
        holder.DoneTxt.setText(contDet.getSTATUS());
        if (contDet.getSTATUS().equals("Done")) {
            holder.DoneImg.setImageResource(R.drawable.done);
        }else if (contDet.getSTATUS().equals("Current")) {
            holder.DoneImg.setImageResource(R.drawable.current);
        }

        return row;
    }

    class CourseHolder{
        TextView CourseCode;
        TextView CourseTitle;
        TextView Category;
        TextView Credits;
        ImageView DoneImg;
        TextView DoneTxt;
        TextView Slots;

        public CourseHolder(View row){
            CourseCode = (TextView) row.findViewById(R.id.tvCCode);
            CourseTitle = (TextView) row.findViewById(R.id.tvCTitle);
            Category = (TextView) row.findViewById(R.id.tvCat);
            Credits = (TextView) row.findViewById(R.id.tvCredits);
            DoneImg = (ImageView) row.findViewById(R.id.ivCDone);
            DoneTxt = (TextView) row.findViewById(R.id.tvCDone);
            Slots = (TextView) row.findViewById(R.id.tvCSlots);
        }
    }
}
