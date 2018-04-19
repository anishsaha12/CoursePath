package com.rising.dots.coursepath;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anish on 12/8/2017.
 */

public class Timetable extends AppCompatActivity {
    HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
    HashMap<String,ArrayList<String>> current_map = new HashMap<String,ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable);
        loadCourses();

        String[] theorySlots = {
                "THEORY","8:00-8:50","9:00-9:50","10:00-10:50","11:00-11:50","12:00-12:50","2:00-2:50","3:00-3:50","4:00-4:50","5:00-5:50","6:00-6:50","-",
                "LAB","8:00-8:45","8:46-9:30","10:00-10:45","10:46-11:30","11:31-12:15","2:00-2:45","2:46-3:30","4:00-4:45","4:46-5:30","5:31-6:15","6:16-7:00",
                "MON","A1","F1","D1","TB1","TG1","A2","F2","D2","TB2","TG2","-",
                "TUE","B1","G1","E1","TC1","TAA1","B2","G2","E2","TC2","TAA2","-",
                "WED","C1","A1","F1","V1","V2","C2","A2","F2","TD2","TBB2","-",
                "THU","D1","B1","G1","TE1","TCC1","D2","B2","G2","TE2","TCC2","-",
                "FRI","E1","C1","TA1","TF1","TD1","E2","C2","TA2","TF2","TDD2","-"};
        String[] labSlots = {
                "THEORY","8:00-8:50","9:00-9:50","10:00-10:50","11:00-11:50","12:00-12:50","2:00-2:50","3:00-3:50","4:00-4:50","5:00-5:50","6:00-6:50","-",
                "LAB","8:00-8:45","8:46-9:30","10:00-10:45","10:46-11:30","11:31-12:15","2:00-2:45","2:46-3:30","4:00-4:45","4:46-5:30","5:31-6:15","6:16-7:00",
                "MON","L1","L2","L3","L4","L5","L31","L32","L33","L34","L35","L36",
                "TUE","L7","L8","L9","L10","L11","L37","L38","L39","L40","L41","L42",
                "WED","L13","L14","L15","L16","-","L43","L44","L45","L46","L47","L48",
                "THU","L19","L20","L21","L22","L23","L49","L50","L51","L52","L53","L54",
                "FRI","L25","L26","L27","L28","L29","L55","L56","L57","L58","L59","L60"};
        loadCurrentCourses();
        final TimetableAdapter timetableAdapter = new TimetableAdapter(this,theorySlots,labSlots,current_map);
        final GridView grid=(GridView)findViewById(R.id.gridTimetable);

        grid.setAdapter(timetableAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String ccode = ((TextView) view.findViewById(R.id.tvCode)).getText().toString();
                if(ccode.length()==7){
                    Intent intent = new Intent(Timetable.this, ViewCourse.class);
                    intent.putExtra("CC", ccode);
                    startActivity(intent);
                }
            }
        });
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
            fileInputStream = new FileInputStream(new File(getFilesDir(), "myCurrentCourseMap.txt"));//i mean myCurrentCourseMap
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
}
