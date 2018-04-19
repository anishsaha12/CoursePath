package com.rising.dots.coursepath;

/**
 * Created by anish on 12/7/2017.
 */

public class CourseItem {
    private String COURSE_CODE;
    private String COURSE_TITLE;
    private String L;
    private String T;
    private String P;
    private String J;
    private String C;
    private String PREREQUISITES;
    private String CATEGORY;
    private String STATUS;

    public CourseItem(String cc, String ct, String l, String t, String p, String j, String c, String pre, String cat, String stat){
        COURSE_CODE=cc;
        COURSE_TITLE=ct;
        L=l;
        T=t;
        P=p;
        J=j;
        C=c;
        PREREQUISITES=pre;
        CATEGORY=cat;
        STATUS=stat;
    }

    public String getCOURSE_CODE(){
        return COURSE_CODE;
    }
    public String getCOURSE_TITLE() {return COURSE_TITLE;}
    public String getL() {return L;}
    public String getT() {return T;}
    public String getP() {return P;}
    public String getJ() {return J;}
    public String getC() {return C;}
    public String getPREREQUISITES() {return PREREQUISITES;}
    public String getCATEGORY() {return CATEGORY;}
    public String getSTATUS() {return STATUS;}
    public String getCredits() {
        String cre = getL() + " "+ getT() + " "+getP() + " "+getJ() + " "+getC();
        return cre;
    }
}
