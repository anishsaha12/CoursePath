package com.rising.dots.coursepath;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anish on 12/11/2017.
 */

public class ReminderReceiver extends BroadcastReceiver {

    AlarmManager am = null;
    PendingIntent pendingIntent = null;
    HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
    HashMap<String,ArrayList<String>> current_map = new HashMap<String,ArrayList<String>>();
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        loadCourses();
        loadCurrentCourses();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //next reminder
        Calendar calendar = Calendar.getInstance();

        //set at half past next hour
        calendar.set(Calendar.MINUTE,30);
        pendingIntent = PendingIntent.getBroadcast(context, 0,new Intent(context, ReminderReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000*60*60,  pendingIntent); //every 1 hr

        //
        String ctitle=null;
        String venue=null;
        final String[][] theorySlots = new String[][]{
                {"THEORY", "8:00-8:50", "9:00-9:50", "10:00-10:50", "11:00-11:50", "12:00-12:50", "2:00-2:50", "3:00-3:50", "4:00-4:50", "5:00-5:50", "6:00-6:50", "-"},
                {"LAB", "8:00-8:45", "8:46-9:30", "10:00-10:45", "10:46-11:30", "11:31-12:15", "2:00-2:45", "2:46-3:30", "4:00-4:45", "4:46-5:30", "5:31-6:15", "6:16-7:00"},
                {"MON", "A1", "F1", "D1", "TB1", "TG1", "A2", "F2", "D2", "TB2", "TG2", "-"},
                {"TUE", "B1", "G1", "E1", "TC1", "TAA1", "B2", "G2", "E2", "TC2", "TAA2", "-"},
                {"WED", "C1", "A1", "F1", "V1", "V2", "C2", "A2", "F2", "TD2", "TBB2", "-"},
                {"THU", "D1", "B1", "G1", "TE1", "TCC1", "D2", "B2", "G2", "TE2", "TCC2", "-"},
                {"FRI", "E1", "C1", "TA1", "TF1", "TD1", "E2", "C2", "TA2", "TF2", "TDD2", "-"}};
        final String[][] labSlots = new String[][]{
                {"THEORY", "8:00-8:50", "9:00-9:50", "10:00-10:50", "11:00-11:50", "12:00-12:50", "2:00-2:50", "3:00-3:50", "4:00-4:50", "5:00-5:50", "6:00-6:50", "-"},
                {"LAB", "8:00-8:45", "8:46-9:30", "10:00-10:45", "10:46-11:30", "11:31-12:15", "2:00-2:45", "2:46-3:30", "4:00-4:45", "4:46-5:30", "5:31-6:15", "6:16-7:00"},
                {"MON", "L1", "L2", "L3", "L4", "L5", "L31", "L32", "L33", "L34", "L35", "L36"},
                {"TUE", "L7", "L8", "L9", "L10", "L11", "L37", "L38", "L39", "L40", "L41", "L42"},
                {"WED", "L13", "L14", "L15", "L16", "-", "L43", "L44", "L45", "L46", "L47", "L48"},
                {"THU", "L19", "L20", "L21", "L22", "L23", "L49", "L50", "L51", "L52", "L53", "L54"},
                {"FRI", "L25", "L26", "L27", "L28", "L29", "L55", "L56", "L57", "L58", "L59", "L60"}};

        int hour = (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+1);   //one hour after current hour
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int row=0,columnT = 0,columnL = 0;

        int hr=hour;
        if(hr>12)
            hr=hr-12;

        switch (day){
            case Calendar.MONDAY:
                row=2;
                break;
            case Calendar.TUESDAY:
                row=3;
                break;
            case Calendar.WEDNESDAY:
                row=4;
                break;
            case Calendar.THURSDAY:
                row=5;
                break;
            case Calendar.FRIDAY:
                row=6;
                break;
        }

        Log.d("Rem","row:"+row+" hour:"+hour+"");

        if(row!=0 && hour>=7 && hour<=19) {
            for(int i=1;i<11;i++){
                List slotTList = Arrays.asList(theorySlots[0][i].split("-"));
                List start = Arrays.asList(slotTList.get(0).toString().split(":"));
                if((hr)==Integer.parseInt(start.get(0).toString())){
                    columnT=i;
                    break;
                }
            }
            CourseItem ciT = CourseInSlot(theorySlots[row][columnT]);
            if(ciT!=null) {
                ctitle = ciT.getCOURSE_TITLE();
                String ven = current_map.get(ciT.getCOURSE_CODE()).toArray()[1].toString();
                if (ven.contains(",")) {
                    List venL = Arrays.asList(ven.split(","));
                    venue = (venL.get(0).toString());
                } else if (ven.contains("+")) {
                    List venL = Arrays.asList(ven.split("\\+"));
                    venue = (venL.get(0).toString());
                } else {
                    venue = ven;
                }
            }

            for(int i=1;i<12;i++) {
                Time time = new Time(hr, 0, 0);
                String slotTime = labSlots[1][i];
                List slotTList = Arrays.asList(slotTime.split("-"));
                List start = Arrays.asList(slotTList.get(0).toString().split(":"));
                List end = Arrays.asList(slotTList.get(1).toString().split(":"));
                Time stT = new Time(Integer.parseInt(start.get(0).toString()),Integer.parseInt(start.get(1).toString()),0);
                Time enT = new Time(Integer.parseInt(end.get(0).toString()),Integer.parseInt(end.get(1).toString()),0);

                if((stT.before(time) && enT.after(time)) || stT.equals(time)){
                    columnL=i;
                    break;
                }
            }
            CourseItem ciL = CourseInSlot(labSlots[row][columnL]);
            if(ciL!=null) {
                ctitle = ciL.getCOURSE_TITLE();
                String ven = current_map.get(ciL.getCOURSE_CODE()).toArray()[1].toString();
                if (ven.contains(",")) {
                    List venL = Arrays.asList(ven.split(","));
                    venue = (venL.get(1).toString());
                } else if (ven.contains("+")) {
                    List venL = Arrays.asList(ven.split("\\+"));
                    venue = (venL.get(1).toString());
                } else {
                    venue = ven;
                }
            }
        //

            if(ctitle!=null) {  //show notification
                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setContentTitle(ctitle)
                        .setContentText(venue)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent);
                notificationManager.notify(30333, mNotifyBuilder.build());
            }
        }

    }

    public void loadCourses(){
        FileInputStream fileInputStream  = null;
        try {
            fileInputStream = new FileInputStream(new File(context.getFilesDir(), "myCourseMap.txt"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            map = (HashMap) objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void loadCurrentCourses(){
        FileInputStream fileInputStream  = null;
        try {
            fileInputStream = new FileInputStream(new File(context.getFilesDir(), "myCurrentCourseMap.txt"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            current_map = (HashMap) objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public CourseItem CourseInSlot(String slot ) {
        int n = current_map.keySet().toArray().length;
        CourseItem courseItem = null;

        for (int i = 0; i < n; i++) {
            String CCode = current_map.keySet().toArray()[i].toString();
            String Cslot = current_map.get(CCode).toArray()[0].toString();;
            if(Cslot.contains("+")){
                List slots = Arrays.asList(Cslot.split("\\+"));
                if(slots.contains(slot)){
                    String CTitle = map.get(CCode).toArray()[0].toString();
                    String L = map.get(CCode).toArray()[1].toString();
                    String T = map.get(CCode).toArray()[2].toString();
                    String P = map.get(CCode).toArray()[3].toString();
                    String J = map.get(CCode).toArray()[4].toString();
                    String C = map.get(CCode).toArray()[5].toString();
                    String Prereq = map.get(CCode).toArray()[6].toString();
                    String Catego = map.get(CCode).toArray()[7].toString();
                    String Stat = map.get(CCode).toArray()[8].toString();

                    courseItem = new CourseItem(CCode,CTitle,L,T,P,J,C,Prereq,Catego,Stat);
                    return courseItem;
                }

            }else if (Cslot.equals(slot)) {
                String CTitle = map.get(CCode).toArray()[0].toString();
                String L = map.get(CCode).toArray()[1].toString();
                String T = map.get(CCode).toArray()[2].toString();
                String P = map.get(CCode).toArray()[3].toString();
                String J = map.get(CCode).toArray()[4].toString();
                String C = map.get(CCode).toArray()[5].toString();
                String Prereq = map.get(CCode).toArray()[6].toString();
                String Catego = map.get(CCode).toArray()[7].toString();
                String Stat = map.get(CCode).toArray()[8].toString();

                courseItem = new CourseItem(CCode,CTitle,L,T,P,J,C,Prereq,Catego,Stat);
                return courseItem;
            }
        }
        return courseItem;
    }
}
