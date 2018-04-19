package com.rising.dots.coursepath;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by anish on 12/7/2017.
 */

public class ViewCourse extends AppCompatActivity {
    HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
    HashMap<String,ArrayList<String>> current_map = new HashMap<String,ArrayList<String>>();
    TextView CourseCode;
    TextView Slots, Venue;
    TextView CourseTitle;
    TextView Category;
    TextView Credits;
    ImageView DoneImg;
    TextView DoneTxt;
    Button DoneBtn;
    Button CurrentBtn;
    Button ClearCanvas;
    GraphCanvas graphCanvas;
    private String s_Text = "";
    private String v_Text = "";
    private ArrayList<String> preq=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_view);
        final String CC = getIntent().getStringExtra("CC");
        preq = new ArrayList<String>();

        graphCanvas = (GraphCanvas) findViewById(R.id.gc_graph);

        CourseCode = (TextView) findViewById(R.id.tvCCode);
        CourseTitle = (TextView) findViewById(R.id.tvCTitle);
        Slots = (TextView) findViewById(R.id.tvCSlots);
        Venue = (TextView) findViewById(R.id.tvCVenue);
        Category = (TextView) findViewById(R.id.tvCat);
        Credits = (TextView) findViewById(R.id.tvCredits);
        DoneImg = (ImageView) findViewById(R.id.ivCDone);
        DoneTxt = (TextView) findViewById(R.id.tvCDone);
        DoneBtn = (Button) findViewById(R.id.bt_completed);
        CurrentBtn = (Button) findViewById(R.id.bt_current);
        ClearCanvas = (Button) findViewById(R.id.btClearCanvas);


        loadCourses();
        loadCurrentCourses();

        final CourseItem mainCourse = findCourses(CC);
        CourseTitle.setText(mainCourse.getCOURSE_TITLE());
        CourseCode.setText(mainCourse.getCOURSE_CODE());
        graphCanvas.setCC(mainCourse.getCOURSE_CODE());
        graphCanvas.setCMap(map);
        Category.setText(mainCourse.getCATEGORY());
        Credits.setText(mainCourse.getCredits());
        DoneTxt.setText(mainCourse.getSTATUS());

        if(mainCourse.getSTATUS().equals("Done")){
            DoneImg.setImageResource(R.drawable.done);
            CurrentBtn.setVisibility(View.INVISIBLE);
            DoneBtn.setText("Not Done");
        }else if (mainCourse.getSTATUS().equals("Current")) {
            DoneImg.setImageResource(R.drawable.current);
            CurrentBtn.setText("Completed");
            DoneBtn.setText("Not Done");
            Slots.setText("- Slots: "+current_map.get(CC).toArray()[0].toString());
            Venue.setText("- Venue: "+current_map.get(CC).toArray()[1].toString());
        }

        ClearCanvas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                graphCanvas.clearCanvas();
            }
        });

        DoneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (DoneBtn.getText().toString().equals("Course Done")){
                    DoneImg.setImageResource(R.drawable.done);
                    DoneTxt.setText("Done");
                    CurrentBtn.setVisibility(View.INVISIBLE);
                    DoneBtn.setText("Not Done");
                    Slots.setText("");

                    //update map
                    ArrayList<String> CP = new ArrayList<String>();
                    CP.add(mainCourse.getCOURSE_TITLE());
                    CP.add(mainCourse.getL());
                    CP.add(mainCourse.getT());
                    CP.add(mainCourse.getP());
                    CP.add(mainCourse.getJ());
                    CP.add(mainCourse.getC());
                    CP.add(mainCourse.getPREREQUISITES());
                    CP.add(mainCourse.getCATEGORY());
                    CP.add("Done");
                    map.put(CC, CP);

                    updateCourses();
                    //

                }else{  //Not Done
                    DoneImg.setImageResource(R.drawable.notdone);
                    DoneTxt.setText("Not Done");
                    CurrentBtn.setVisibility(View.VISIBLE);
                    CurrentBtn.setText("Current Course");
                    DoneBtn.setText("Course Done");
                    Slots.setText("");

                    //update map
                    ArrayList<String> CP = new ArrayList<String>();
                    CP.add(mainCourse.getCOURSE_TITLE());
                    CP.add(mainCourse.getL());
                    CP.add(mainCourse.getT());
                    CP.add(mainCourse.getP());
                    CP.add(mainCourse.getJ());
                    CP.add(mainCourse.getC());
                    CP.add(mainCourse.getPREREQUISITES());
                    CP.add(mainCourse.getCATEGORY());
                    CP.add("Not Done");
                    map.put(CC, CP);

                    updateCourses();
                    //

                    //update completed courses map
                    current_map.remove(CC);
                    updateCurrentCourses();
                    //
                }
            }
        });

        CurrentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (CurrentBtn.getText().toString().equals("Current Course")) {
                    //
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewCourse.this);
                    builder.setTitle("Slots-Venue");

                    // Set up the input
                    LinearLayout ll=new LinearLayout(ViewCourse.this);
                    ll.setOrientation(LinearLayout.VERTICAL);

                    final EditText slots = new EditText(ViewCourse.this);
                    final EditText venue = new EditText(ViewCourse.this);
                    // Specify the type of input expected;
                    slots.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    venue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

                    ll.addView(slots);
                    ll.addView(venue);

                    builder.setView(ll);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            s_Text = slots.getText().toString();
                            v_Text = venue.getText().toString();
                            DoneImg.setImageResource(R.drawable.current);
                            DoneTxt.setText("Current");
                            CurrentBtn.setText("Completed");
                            DoneBtn.setText("Not Done");
                            Slots.setText("- Slots: "+s_Text);
                            Venue.setText("- Venue: "+v_Text);

                            //update map
                            ArrayList<String> CP = new ArrayList<String>();
                            CP.add(mainCourse.getCOURSE_TITLE());
                            CP.add(mainCourse.getL());
                            CP.add(mainCourse.getT());
                            CP.add(mainCourse.getP());
                            CP.add(mainCourse.getJ());
                            CP.add(mainCourse.getC());
                            CP.add(mainCourse.getPREREQUISITES());
                            CP.add(mainCourse.getCATEGORY());
                            CP.add("Current");
                            map.put(CC, CP);

                            updateCourses();
                            //

                            CP = new ArrayList<String>();
                            CP.add(s_Text);
                            CP.add(v_Text);
                            //update completed courses map
                            current_map.put(CC, CP);
                            updateCurrentCourses();
                            //
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            s_Text="";
                            dialog.cancel();
                        }
                    });

                    builder.show();
                    //

                }else{  //Completed
                    DoneImg.setImageResource(R.drawable.done);
                    DoneTxt.setText("Done");
                    CurrentBtn.setVisibility(View.INVISIBLE);
                    DoneBtn.setText("Not Done");
                    Slots.setText("");

                    //update map
                    ArrayList<String> CP = new ArrayList<String>();
                    CP.add(mainCourse.getCOURSE_TITLE());
                    CP.add(mainCourse.getL());
                    CP.add(mainCourse.getT());
                    CP.add(mainCourse.getP());
                    CP.add(mainCourse.getJ());
                    CP.add(mainCourse.getC());
                    CP.add(mainCourse.getPREREQUISITES());
                    CP.add(mainCourse.getCATEGORY());
                    CP.add("Done");
                    map.put(CC, CP);

                    updateCourses();
                    //

                    //update completed courses map
                    current_map.remove(CC);
                    updateCurrentCourses();
                    //
                }
            }
        });
    }

    public  CourseItem findCourses(String CC){
        int n = map.keySet().toArray().length;
        String CCode = CC;

        String CTitle = map.get(CCode).toArray()[0].toString();
        String L = map.get(CCode).toArray()[1].toString();
        String T = map.get(CCode).toArray()[2].toString();
        String P = map.get(CCode).toArray()[3].toString();
        String J = map.get(CCode).toArray()[4].toString();
        String C = map.get(CCode).toArray()[5].toString();
        String Prereq = map.get(CCode).toArray()[6].toString();
        String Catego = map.get(CCode).toArray()[7].toString();
        String Stat = map.get(CCode).toArray()[8].toString();

        CourseItem courseItem = new CourseItem(CCode,CTitle,L,T,P,J,C,Prereq,Catego,Stat);
        return courseItem;
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

    public void updateCourses() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getFilesDir(), "myCourseMap.txt"));
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(map);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public void updateCurrentCourses() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getFilesDir(), "myCurrentCourseMap.txt"));//i mean myCurrentCourseMap
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(current_map);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
