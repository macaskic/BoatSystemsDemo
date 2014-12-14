package com.surveyorexpert.BoatControl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.surveyorexpert.math.Vector2D;

/**
 * Created by calum on 08/12/2014.
 */
public class Sensor {

    private int spotDiam = 0;
    private int graphicWidth = 0;
    private int screenWidth = 0;
    private float posX = 0;
    private float posY = 0;
    private float textSize = 0;
    private float scale = 0f;
    private int myColor = 0;
    private int offSet = 0;
  //  private Color myColor = null;
    private Canvas myCanvas = null;
    private Paint myPaint = null;
    private Vector2D myPosition = null;
    private String myName = "";

    Sensor(Canvas canvas, Paint paint, Vector2D position, String name){
        myName = name;
        myCanvas = canvas;
        myPaint = paint;
        myPosition = position;
    }

    void init(int graphWidth, int color, int diam, int offset, float textSize){
        setGraphicWidth(graphWidth);
        setColor(color);
        setDiam(diam);
        distanceFromMidShips(offset);
        setTextSize(textSize);
    }


 //   void setName(String name){
  ///      myName = name;
  //  }

    void distanceFromMidShips(int dist){
        offSet = dist;
    }

    void setGraphicWidth(int width){
        graphicWidth = width;
    }

  //  void setScreenWidth(int width){
  //      screenWidth = width;
  //  }

    void setColor(int color){
        myColor = color;
    }

    void setDiam(int diam){
        spotDiam = diam;
    }

    void draw(Canvas canvas, Paint paint, float scale){

        paint.setColor(myColor);
        float location = (graphicWidth/(spotDiam/scale)/(scale));

        canvas.drawCircle(getPosX(scale), getPosY(scale), location, paint);
        myPaint.setColor(Color.BLACK);
        myPaint.setTextSize(textSize / scale);
        myCanvas.drawText(myName, getPosX(scale)-25, getPosY(scale)-25, myPaint);

        if (scale > 2.5 && scale < 100) {
            myPaint.setColor(Color.BLACK);
            myPaint.setTextSize(textSize / scale);
            myCanvas.drawText("High " + (int)(scale+2), getPosX(scale), getPosY(scale), myPaint);
            myCanvas.drawText("Reading " +(int)(scale+1),  getPosX(scale), getPosY(scale) +(50/scale), myPaint);
            myCanvas.drawText("Low " + (int)(scale), getPosX(scale), getPosY(scale)+(100/scale), myPaint);
        }
        myPaint.setColor(myColor);
    }

    float getPosX(float scale){
        posX = (myPosition.getX()-(offSet*scale))/scale;
        return posX;
    }

    float getPosY(float scale){
        return myPosition.getY()/scale;
    }

    void  setTextSize(float tSize){
        textSize = tSize;
    }
}
