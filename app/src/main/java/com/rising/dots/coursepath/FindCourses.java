package com.rising.dots.coursepath;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FindCourses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_courses);

        Button goCC = (Button) findViewById(R.id.btCourseCode);
        Button findCT = (Button) findViewById(R.id.btCourseTitle);
        final EditText CC = (EditText) findViewById(R.id.etCourseCode);
        final EditText CT = (EditText) findViewById(R.id.etCourseTitle);

        goCC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(v.getContext(), SearchCCode.class);
                intent.putExtra("CC", CC.getText().toString());
                startActivity(intent);
            }
        });

        findCT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(v.getContext(), SearchCTitle.class);
                intent.putExtra("CT", CT.getText().toString());
                startActivity(intent);
            }
        });
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
        HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
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
                CP.add("Not Done"); //status- Done/Not Done/Current
                map.put(CC, CP);

            }

            FileOutputStream fileOutputStream = new FileOutputStream(new File(getFilesDir(), "myCourseMap.txt"));
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(map);
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


}
