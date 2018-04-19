package com.rising.dots.coursepath;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView list = null;
    HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
    HashMap<String,ArrayList<String>> current_map = new HashMap<String,ArrayList<String>>();

    AlarmManager am = null;
    PendingIntent pendingIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadCourses();
        loadCurrentCourses();
        pendingIntent = PendingIntent.getBroadcast(this, 0,new Intent(MainActivity.this, ReminderReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager)this.getSystemService(this.ALARM_SERVICE);

        final Button mon = findViewById(R.id.btMon);
        final Button tue = findViewById(R.id.btTue);
        final Button wed = findViewById(R.id.btWed);
        final Button thu = findViewById(R.id.btThu);
        final Button fri = findViewById(R.id.btFri);

        list = (ListView) findViewById(R.id.lvTimeSlots);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CourseItem contDet = (CourseItem) parent.getItemAtPosition(position);
                if(contDet.getCOURSE_CODE()!="-") {
                    Intent intent = new Intent(MainActivity.this, ViewCourse.class);
                    intent.putExtra("CC", contDet.getCOURSE_CODE());
                    startActivity(intent);
                }

            }
        });

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


        mon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                // Perform action on click
                int num=2;
                final ArrayList<CourseItem> listCont = new ArrayList<CourseItem>();
                final ArrayList<String> slots = new ArrayList<String>();
                final ArrayList<String> times = new ArrayList<String>();
                final ArrayList<String> venues = new ArrayList<String>();
                mon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimaryDark));
                tue.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                wed.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                thu.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                fri.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                for(int i=1;i<12;i++){
                    CourseItem ciT = CourseInSlot(theorySlots[num][i]);
                    CourseItem ciL = CourseInSlot(labSlots[num][i]);
                    if(ciT!=null){
                        listCont.add(ciT);
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        String venue = current_map.get(ciT.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(0).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(0).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(ciL!=null){
                        listCont.add(ciL);
                        slots.add(labSlots[num][i]);
                        times.add(labSlots[1][i]);
                        String venue = current_map.get(ciL.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(1).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(1).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(theorySlots[0][i]!="-"){
                        //nothing
                        listCont.add(new CourseItem("-","-","-","-","-","-","-","-","-","-"));
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        venues.add("");
                    }
                }
                TimeSlotsAdapter courseListAdapter = new TimeSlotsAdapter(MainActivity.this,R.layout.time_slot_item,listCont,slots,times,venues);
                list.setAdapter(courseListAdapter);
                courseListAdapter.notifyDataSetChanged();
            }
        });

        tue.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                // Perform action on click
                int num=3;
                final ArrayList<CourseItem> listCont = new ArrayList<CourseItem>();
                final ArrayList<String> slots = new ArrayList<String>();
                final ArrayList<String> times = new ArrayList<String>();
                final ArrayList<String> venues = new ArrayList<String>();
                tue.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimaryDark));
                mon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                wed.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                thu.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                fri.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                for(int i=1;i<12;i++){
                    CourseItem ciT = CourseInSlot(theorySlots[num][i]);
                    CourseItem ciL = CourseInSlot(labSlots[num][i]);
                    if(ciT!=null){
                        listCont.add(ciT);
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        String venue = current_map.get(ciT.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(0).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(0).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(ciL!=null){
                        listCont.add(ciL);
                        slots.add(labSlots[num][i]);
                        times.add(labSlots[1][i]);
                        String venue = current_map.get(ciL.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(1).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(1).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(theorySlots[0][i]!="-"){
                        //nothing
                        listCont.add(new CourseItem("-","-","-","-","-","-","-","-","-","-"));
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        venues.add("");
                    }
                }
                TimeSlotsAdapter courseListAdapter = new TimeSlotsAdapter(MainActivity.this,R.layout.time_slot_item,listCont,slots,times,venues);
                list.setAdapter(courseListAdapter);
                courseListAdapter.notifyDataSetChanged();
            }
        });

        wed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                // Perform action on click
                int num=4;
                final ArrayList<CourseItem> listCont = new ArrayList<CourseItem>();
                final ArrayList<String> slots = new ArrayList<String>();
                final ArrayList<String> times = new ArrayList<String>();
                final ArrayList<String> venues = new ArrayList<String>();
                wed.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimaryDark));
                tue.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                mon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                thu.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                fri.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                for(int i=1;i<12;i++){
                    CourseItem ciT = CourseInSlot(theorySlots[num][i]);
                    CourseItem ciL = CourseInSlot(labSlots[num][i]);
                    if(ciT!=null){
                        listCont.add(ciT);
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        String venue = current_map.get(ciT.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(0).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(0).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(ciL!=null){
                        listCont.add(ciL);
                        slots.add(labSlots[num][i]);
                        times.add(labSlots[1][i]);
                        String venue = current_map.get(ciL.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(1).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(1).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(theorySlots[0][i]!="-"){
                        //nothing
                        listCont.add(new CourseItem("-","-","-","-","-","-","-","-","-","-"));
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        venues.add("");
                    }
                }
                TimeSlotsAdapter courseListAdapter = new TimeSlotsAdapter(MainActivity.this,R.layout.time_slot_item,listCont,slots,times,venues);
                list.setAdapter(courseListAdapter);
                courseListAdapter.notifyDataSetChanged();
            }
        });

        thu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                // Perform action on click
                int num=5;
                final ArrayList<CourseItem> listCont = new ArrayList<CourseItem>();
                final ArrayList<String> slots = new ArrayList<String>();
                final ArrayList<String> times = new ArrayList<String>();
                final ArrayList<String> venues = new ArrayList<String>();
                thu.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimaryDark));
                tue.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                wed.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                mon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                fri.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                for(int i=1;i<12;i++){
                    CourseItem ciT = CourseInSlot(theorySlots[num][i]);
                    CourseItem ciL = CourseInSlot(labSlots[num][i]);
                    if(ciT!=null){
                        listCont.add(ciT);
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        String venue = current_map.get(ciT.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(0).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(0).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(ciL!=null){
                        listCont.add(ciL);
                        slots.add(labSlots[num][i]);
                        times.add(labSlots[1][i]);
                        String venue = current_map.get(ciL.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(1).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(1).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(theorySlots[0][i]!="-"){
                        //nothing
                        listCont.add(new CourseItem("-","-","-","-","-","-","-","-","-","-"));
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        venues.add("");
                    }
                }
                TimeSlotsAdapter courseListAdapter = new TimeSlotsAdapter(MainActivity.this,R.layout.time_slot_item,listCont,slots,times,venues);
                list.setAdapter(courseListAdapter);
                courseListAdapter.notifyDataSetChanged();
            }
        });

        fri.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                // Perform action on click
                int num=6;
                final ArrayList<CourseItem> listCont = new ArrayList<CourseItem>();
                final ArrayList<String> slots = new ArrayList<String>();
                final ArrayList<String> times = new ArrayList<String>();
                final ArrayList<String> venues = new ArrayList<String>();
                fri.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimaryDark));
                tue.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                wed.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                thu.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                mon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorPrimary));
                for(int i=1;i<12;i++){
                    CourseItem ciT = CourseInSlot(theorySlots[num][i]);
                    CourseItem ciL = CourseInSlot(labSlots[num][i]);
                    if(ciT!=null){
                        listCont.add(ciT);
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        String venue = current_map.get(ciT.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(0).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(0).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(ciL!=null){
                        listCont.add(ciL);
                        slots.add(labSlots[num][i]);
                        times.add(labSlots[1][i]);
                        String venue = current_map.get(ciL.getCOURSE_CODE()).toArray()[1].toString();
                        if(venue.contains(",")) {
                            List ven = Arrays.asList(venue.split(","));
                            venues.add(ven.get(1).toString());
                        }else if(venue.contains("+")) {
                            List ven = Arrays.asList(venue.split("\\+"));
                            venues.add(ven.get(1).toString());
                        }else{
                            venues.add(venue);
                        }
                    }else if(theorySlots[0][i]!="-"){
                        //nothing
                        listCont.add(new CourseItem("-","-","-","-","-","-","-","-","-","-"));
                        slots.add(theorySlots[num][i]);
                        times.add(theorySlots[0][i]);
                        venues.add("");
                    }
                }
                TimeSlotsAdapter courseListAdapter = new TimeSlotsAdapter(MainActivity.this,R.layout.time_slot_item,listCont,slots,times,venues);
                list.setAdapter(courseListAdapter);
                courseListAdapter.notifyDataSetChanged();
            }
        });

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day){
            case Calendar.MONDAY:
                mon.performClick();
                break;
            case Calendar.TUESDAY:
                tue.performClick();
                break;
            case Calendar.WEDNESDAY:
                wed.performClick();
                break;
            case Calendar.THURSDAY:
                thu.performClick();
                break;
            case Calendar.FRIDAY:
                fri.performClick();
                break;
            case Calendar.SATURDAY:
                final ArrayList<CourseItem> listCont = new ArrayList<CourseItem>();
                final ArrayList<String> slots = new ArrayList<String>();
                final ArrayList<String> times = new ArrayList<String>();
                final ArrayList<String> venues = new ArrayList<String>();
                listCont.add(new CourseItem("Saturday","No Classes","-","-","-","-","-","-","-","-"));
                slots.add("");
                times.add("Full Day");
                venues.add("");
                TimeSlotsAdapter courseListAdapter = new TimeSlotsAdapter(MainActivity.this,R.layout.time_slot_item,listCont,slots,times,venues);
                list.setAdapter(courseListAdapter);
                courseListAdapter.notifyDataSetChanged();
                break;
            case Calendar.SUNDAY:
                final ArrayList<CourseItem> listCont1 = new ArrayList<CourseItem>();
                final ArrayList<String> slots1 = new ArrayList<String>();
                final ArrayList<String> times1 = new ArrayList<String>();
                final ArrayList<String> venues1 = new ArrayList<String>();
                listCont1.add(new CourseItem("Sunday","No Classes","-","-","-","-","-","-","-","-"));
                slots1.add("");
                times1.add("Full Day");
                venues1.add("");
                TimeSlotsAdapter courseListAdapter1 = new TimeSlotsAdapter(MainActivity.this,R.layout.time_slot_item,listCont1,slots1,times1,venues1);
                list.setAdapter(courseListAdapter1);
                courseListAdapter1.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_courses) {
            Intent i = new Intent(MainActivity.this, CurrentCourses.class);
            startActivity(i);
        } else if (id == R.id.nav_timetable) {
            Intent i = new Intent(MainActivity.this, Timetable.class);
            startActivity(i);
        } else if (id == R.id.nav_complet_courses) {
            Intent i = new Intent(MainActivity.this, CompletedCourses.class);
            startActivity(i);
        } else if (id == R.id.nav_find) {
            Intent i = new Intent(MainActivity.this, FindCourses.class);
            startActivity(i);
        } else if (id == R.id.nav_all_courses) {
            Intent i = new Intent(MainActivity.this, AllCourses.class);
            startActivity(i);
        } else if (id == R.id.nav_reload_courses) {
            reloadCourses();
        } else if (id == R.id.nav_start_rem) {
            Calendar calendar = Calendar.getInstance();
            //calendar.set(Calendar.MINUTE,30);
            /*pendingIntent = PendingIntent.getBroadcast(this, 0,new Intent(MainActivity.this, ReminderReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
            am = (AlarmManager)this.getSystemService(this.ALARM_SERVICE);*/
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+1000,  pendingIntent);
        } else if (id == R.id.nav_stop_rem) {
            am.cancel(pendingIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getResources().openRawResource(R.raw.courses);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public void reloadCourses() {
        String CC = new String();
        String CT = new String();
        String L = new String();
        String T = new String();
        String P = new String();
        String J = new String();
        String C = new String();
        String PQ = new String();
        String Category = new String();
        ArrayList<String> CP = null;
        HashMap<String,ArrayList<String>> map1 = new HashMap<String,ArrayList<String>>();
        try{
            JSONArray parentArray = new JSONArray(loadJSONFromAsset());

            for(int i=0; i<parentArray.length(); i++) {
                JSONObject finalObject = parentArray.getJSONObject(i);

                CC = finalObject.getString("COURSE_CODE");
                CT = finalObject.getString("COURSE_TITLE");
                L = finalObject.getString("L");
                T = finalObject.getString("T");
                P = finalObject.getString("P");
                J = finalObject.getString("J");
                C = finalObject.getString("C");
                PQ = finalObject.getString("PREREQUISITES");
                Category = finalObject.getString("CATEGORY");
                CP = new ArrayList<String>();
                CP.add(CT);
                CP.add(L);
                CP.add(T);
                CP.add(P);
                CP.add(J);
                CP.add(C);
                CP.add(PQ);
                CP.add(Category);
                String Stat = map.get(CC).toArray()[8].toString();
                if(Stat.equals("Done"))
                    CP.add("Done"); //status- Done/Not Done/Current
                else if(Stat.equals("Current"))
                    CP.add("Current"); //status- Done/Not Done/Current
                else
                    CP.add("Not Done"); //status- Done/Not Done/Current
                map1.put(CC, CP);

            }

            FileOutputStream fileOutputStream = new FileOutputStream(new File(getFilesDir(), "myCourseMap.txt"));
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(map1);
            objectOutputStream.close();

        }catch (JSONException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int n = map.keySet().toArray().length;
        System.out.println(n +" Courses");
    }

    public void loadCourses(){
        FileInputStream fileInputStream  = null;
        try {
            fileInputStream = new FileInputStream(new File(getFilesDir(), "myCourseMap.txt"));
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
            fileInputStream = new FileInputStream(new File(getFilesDir(), "myCurrentCourseMap.txt"));
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
