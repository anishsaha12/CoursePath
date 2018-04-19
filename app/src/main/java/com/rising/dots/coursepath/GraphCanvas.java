package com.rising.dots.coursepath;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anish on 12/8/2017.
 */

public class GraphCanvas extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;
    private Paint mPaint,cPaint,tPaint,lPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;
    private String cCode;
    HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();

    public GraphCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        // we set a new Path
        mPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
        mPaint.setTextSize(35);

        //text paint object
        tPaint = new Paint();
        tPaint.setAntiAlias(true);
        tPaint.setColor(Color.BLACK);
        tPaint.setStyle(Paint.Style.STROKE);
        tPaint.setStrokeJoin(Paint.Join.ROUND);
        tPaint.setStrokeWidth(4f);
        tPaint.setTextSize(35);

        //line paint object
        lPaint = new Paint();
        lPaint.setAntiAlias(true);
        lPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
        lPaint.setStyle(Paint.Style.STROKE);
        lPaint.setStrokeJoin(Paint.Join.ROUND);
        lPaint.setStrokeWidth(10f);

        //circle paint object
        cPaint = new Paint();
        cPaint.setStyle(Paint.Style.FILL);
        cPaint.setColor(getResources().getColor(R.color.colorAccent));
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        canvas.drawPath(mPath, mPaint);

        int w = getWidth();
        int h = getHeight();
        int radius;
        radius = 100;
        float x=w/2;
        float y=130;
        int[] centresX = {100, 320, 540, 760, 980}; //+220
        int[] centresY = {150, 400, 650, 900};  //+250
        Log.d("no of rows",""+(h/250));

        //Node draw main level 1
        canvas.drawCircle(centresX[2] , centresY[0] , radius, cPaint);
        canvas.drawText(cCode, centresX[2]-radius+20, centresY[0]+10, tPaint);

        //Node draw level 2 onwards
        drawChildren(canvas, cCode,centresX[2],centresY[0], radius);

    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPath.lineTo(mX, mY);
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    public void setCC(String cc){
        cCode = cc;
    }

    public void setCMap(HashMap<String,ArrayList<String>> ma){
        map = ma;
    }

    public ArrayList<String> getPrerequisites(String CCode) {
        ArrayList<String> prequisites= new ArrayList<>();
        String cP = map.get(CCode).toArray()[6].toString();
        if(cP.contains("NONE")) {
            return null;
        }
        else {
            cP = cP.replace(" ", "");
            String s[] = cP.split(",");
            for (int j = 0; j < s.length; j++) {
                String str = s[j];
                if(s[j].contains("/"))
                    s[j] = str.split("/")[0];
                prequisites.add(s[j]);
            }
            return prequisites;
        }
    }

    void drawChildren(Canvas canvas, String parentCC, int parentX, int parentY, int radius){
        ArrayList<String> lvl2 = getPrerequisites(parentCC);
        int[] centresX = {100, 320, 540, 760, 980}; //+220
        int centreY = parentY+250;
        if(centreY+radius <= 1000) {
            if (lvl2 != null) {
                if (lvl2.size() == 1) {
                    //draw line
                    canvas.drawLine(parentX, parentY, parentX, centreY, lPaint);
                    //parent
                    canvas.drawCircle(parentX, parentY, radius, cPaint);
                    canvas.drawText(parentCC, parentX - radius + 20, parentY + 10, tPaint);
                    //child
                    canvas.drawCircle(parentX, centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(0), parentX - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(0), parentX, centreY, radius);
                } else if (lvl2.size() == 2) {
                    //draw line
                    canvas.drawLine(parentX, parentY, centresX[1], centreY, lPaint);
                    canvas.drawLine(parentX, parentY, centresX[3], centreY, lPaint);
                    //parent
                    canvas.drawCircle(parentX, parentY, radius, cPaint);
                    canvas.drawText(parentCC, parentX - radius + 20, parentY + 10, tPaint);
                    //left child
                    canvas.drawCircle(centresX[1], centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(0), centresX[1] - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(0), centresX[1], centreY, radius);
                    //right child
                    canvas.drawCircle(centresX[3], centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(1), centresX[3] - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(1), centresX[3], centreY, radius);
                } else if (lvl2.size() == 3) {
                    //draw line
                    canvas.drawLine(parentX, parentY, centresX[1], centreY, lPaint);
                    canvas.drawLine(parentX, parentY, centresX[2], centreY, lPaint);
                    canvas.drawLine(parentX, parentY, centresX[3], centreY, lPaint);
                    //parent
                    canvas.drawCircle(parentX, parentY, radius, cPaint);
                    canvas.drawText(parentCC, parentX - radius + 20, parentY + 10, tPaint);
                    //left child
                    canvas.drawCircle(centresX[1], centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(0), centresX[1] - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(0), centresX[1], centreY, radius);
                    //middle child
                    canvas.drawCircle(centresX[2], centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(1), centresX[2] - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(1), centresX[2], centreY, radius);
                    //right child
                    canvas.drawCircle(centresX[3], centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(2), centresX[3] - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(2), centresX[3], centreY, radius);
                } else if (lvl2.size() == 4) {
                    //draw line
                    canvas.drawLine(parentX, parentY, centresX[1], centreY, lPaint);
                    canvas.drawLine(parentX, parentY, centresX[2], centreY, lPaint);
                    canvas.drawLine(parentX, parentY, centresX[3], centreY, lPaint);
                    canvas.drawLine(parentX, parentY, centresX[4], centreY, lPaint);
                    //parent
                    canvas.drawCircle(parentX, parentY, radius, cPaint);
                    canvas.drawText(parentCC, parentX - radius + 20, parentY + 10, tPaint);
                    //1st child
                    canvas.drawCircle(centresX[1], centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(0), centresX[1] - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(0), centresX[1], centreY, radius);
                    //2nd child
                    canvas.drawCircle(centresX[2], centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(1), centresX[2] - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(1), centresX[2], centreY, radius);
                    //3rd child
                    canvas.drawCircle(centresX[3], centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(2), centresX[3] - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(2), centresX[3], centreY, radius);
                    //4th child
                    canvas.drawCircle(centresX[4], centreY, radius, cPaint);
                    canvas.drawText(lvl2.get(2), centresX[4] - radius + 20, centreY + 10, tPaint);
                    drawChildren(canvas, lvl2.get(2), centresX[4], centreY, radius);
                } else if (lvl2.size() == 5) {
                }
            } else {
                //draw line
                canvas.drawLine(parentX, parentY, parentX, centreY, lPaint);
                //parent
                canvas.drawCircle(parentX, parentY, radius, cPaint);
                canvas.drawText(parentCC, parentX - radius + 20, parentY + 10, tPaint);
                //child
                canvas.drawCircle(parentX, centreY, radius, cPaint);
                canvas.drawText("NONE", parentX - radius + 50, centreY + 10, tPaint);
            }

        }else if(lvl2!=null){
            //draw line
            canvas.drawLine(parentX, parentY, parentX, parentY+radius+50, lPaint);
            //parent
            canvas.drawCircle(parentX, parentY, radius, cPaint);
            canvas.drawText(parentCC, parentX - radius + 20, parentY + 10, tPaint);
            //child
            canvas.drawText(". . .", parentX - radius + 80, parentY+radius+50 + 10, tPaint);
        }
    }

}
