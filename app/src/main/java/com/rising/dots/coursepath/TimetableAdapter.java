package com.rising.dots.coursepath;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anish on 12/8/2017.
 */

public class TimetableAdapter extends BaseAdapter{

    private Context mContext;
    private String[] theorySlots;
    private String[] labSlots;
    HashMap<String,ArrayList<String>> current_map = new HashMap<String,ArrayList<String>>();

    public TimetableAdapter(Context c, String[] thSl, String[] laSl, HashMap<String,ArrayList<String>> current_map  ) {
        mContext = c;
        this.theorySlots = thSl;
        this.labSlots = laSl;
        this.current_map = current_map;
    }

    @Override
    public int getCount() {
        return theorySlots.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View grid = null;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view==null){
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_item, null);
            TextView slot = (TextView) grid.findViewById(R.id.tvSlot);
            TextView code = (TextView) grid.findViewById(R.id.tvCode);

            String ccT = CurrentCourses(theorySlots[i]);
            String ccL = CurrentCourses(labSlots[i]);
            if(ccT=="" && ccL=="" && i>23 && i%12!=0){  //no courses empty slots
                grid.findViewById(R.id.lvgridItem).setBackgroundColor(Color.YELLOW);
                slot.setText(theorySlots[i]+"/"+labSlots[i]);
                code.setText("-");
            }else if(i<=23 || i%12==0){                 //headers
                grid.findViewById(R.id.lvgridItem).setBackgroundColor(Color.LTGRAY);
                code.setText(theorySlots[i]);
                if(i>=1&&i<=11 || i>=13&&i<=23)
                    code.setTextSize(9);
            }else {                                     //course found
                if(ccT!="") {
                    String venue = current_map.get(ccT).toArray()[1].toString();
                    String v;
                    if(venue.contains(",")) {
                        List ven = Arrays.asList(venue.split(","));
                        v=ven.get(0).toString();
                    }else if(venue.contains("+")) {
                        List ven = Arrays.asList(venue.split("\\+"));
                        v=ven.get(0).toString();
                    }else{
                        v=venue;
                    }
                    slot.setText(theorySlots[i]+"-"+v);
                    code.setText(ccT);
                }else{
                    String venue = current_map.get(ccL).toArray()[1].toString();
                    String v;
                    if(venue.contains(",")) {
                        List ven = Arrays.asList(venue.split(","));
                        v=ven.get(1).toString();
                    }else if(venue.contains("+")) {
                        List ven = Arrays.asList(venue.split("\\+"));
                        v=ven.get(1).toString();
                    }else{
                        v=venue;
                    }
                    slot.setText(labSlots[i]+"-"+v);
                    code.setText(ccL);
                }
            }
        }else {
            grid = view;
        }
        return grid;
    }

    public String CurrentCourses(String slot ) {
        int n = current_map.keySet().toArray().length;

        for (int i = 0; i < n; i++) {
            String CCode = current_map.keySet().toArray()[i].toString();
            String Cslot = current_map.get(CCode).toArray()[0].toString();;
            if(Cslot.contains("+")){
                List slots = Arrays.asList(Cslot.split("\\+"));
                if(slots.contains(slot)){
                    return CCode;
                }

            }else if (Cslot.equals(slot)) {
                return CCode;
            }
        }
        return "";
    }

}
