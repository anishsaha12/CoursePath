package com.rising.dots.coursepath;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anish on 12/7/2017.
 */

public class SearchCCode extends AppCompatActivity {

    ListView list = null;
    HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses_list);
        String CC = getIntent().getStringExtra("CC");

        list = (ListView) findViewById(R.id.lvCourses);

        loadCourses();
        findCourses(CC);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CourseItem contDet = (CourseItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(SearchCCode.this, ViewCourse.class);
                intent.putExtra("CC", contDet.getCOURSE_CODE());;
                startActivity(intent);

            }
        });
    }

    public  void findCourses(String CC){
        int n = map.keySet().toArray().length;
        System.out.println(n +" Courses");

        ArrayList<CourseItem> listCont = new ArrayList<CourseItem>();

        for (int i = 0; i <n; i++) {
            String CCode =map.keySet().toArray()[i].toString();
            if(CCode.contains(CC)) {
                String CTitle = map.get(CCode).toArray()[0].toString();
                String L = map.get(CCode).toArray()[1].toString();
                String T = map.get(CCode).toArray()[2].toString();
                String P = map.get(CCode).toArray()[3].toString();
                String J = map.get(CCode).toArray()[4].toString();
                String C = map.get(CCode).toArray()[5].toString();
                String Prereq = map.get(CCode).toArray()[6].toString();
                String Catego = map.get(CCode).toArray()[7].toString();
                String Stat = map.get(CCode).toArray()[8].toString();

                CourseItem courseItem = new CourseItem(CCode, CTitle, L, T, P, J, C, Prereq, Catego, Stat);
                listCont.add(courseItem);
            }
        }

        CourseListAdapter courseListAdapter = new CourseListAdapter(SearchCCode.this,R.layout.course_item,listCont);
        list.setAdapter(courseListAdapter);
        courseListAdapter.notifyDataSetChanged();

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
}
