package com.surveyorexpert.BoatControl;

/**
 * Created by calum on 04/12/2014.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.surveyorexpert.math.Vector2D;

public class SimpleView extends ImageView implements View.OnTouchListener {

    private Sensor sensor1 = null;
    private Sensor sensor2 = null;
    private Sensor sensor3 = null;
    private Sensor sensor4 = null;

    private Context myContext = null;
    private Canvas myCanvas = null;
    private Paint myPaint = null;
    private Vector2D position = new Vector2D();

    private final Bitmap bitmap;
    private final int width;
    private final int height;
    private Matrix transform = new Matrix();
    private String m_TextCaptured = "Reading";
    private float textSize = 0;

    private float scale = 1;
    private float touchX = 0;
    private float touchY = 0;


  //  private float spotDiam = 0;
  //  private float spotScaled = 0f;
    private float greenSpotX = 0;
    private float greenSpotY = 0;
  //  private float greenSpotX1 = 0;
 //   private float greenSpotY1 = 0;
    private float redSpotX = 0;
    private float redSpotY = 0;
    private float yellowSpotX = 0;
    private float yellowSpotY = 0;
    private float magentaSpotX = 0;
    private float magentaSpotY = 0;


    private  String str = null;
    private  String str0 = null;
    private  String str1 = null;
    private  String str2 = null;
    private  String msg = null;

    private TouchManager touchManager = new TouchManager(2);
    private boolean isInitialized = false;


    public SimpleView(Context context, Bitmap bitmap) {
        super(context);
        myContext = context;
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.setBackgroundColor(Color.LTGRAY);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        myCanvas = canvas;
        myPaint = new Paint();

        textSize = Math.min(width, height) * 0.13f;
        Toast.makeText(myContext, Float.toString(textSize), Toast.LENGTH_SHORT).show();



        myPaint.setTextSize(textSize / scale);

        if (!isInitialized) {
            position.set(getWidth()/2, getHeight()/2);
            setUpSensors();
            isInitialized = true;
        }

        transform.reset();
        transform.postTranslate(-width / 2.0f, -height / 2.0f);
        transform.postScale(scale, scale);
        transform.postTranslate(position.getX(), position.getY());

        myCanvas.drawBitmap(bitmap, transform, myPaint);

        try {

            myCanvas.scale(scale, scale);

            reportData();
            reportSelected();

            drawData(myCanvas, myPaint);
            drawSensors(myCanvas, myPaint);

        }
        catch(NullPointerException e) {
            // Just being lazy here...
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Vector2D vca = null;
        Vector2D vcb = null;
        Vector2D vpa = null;
        Vector2D vpb = null;

        try {

            touchManager.update(event);

            if (touchManager.getPressCount() == 1) {
                vca = touchManager.getPoint(0);
                vpa = touchManager.getPreviousPoint(0);
                position.add(touchManager.moveDelta(0));

                if (myCanvas != null) {
                    touchX = event.getX();
                    touchY =  event.getY();
                }
            }
            else {

                if (touchManager.getPressCount() == 2) {
                    // this enables the pinch
                    vca = touchManager.getPoint(0);
                    vpa = touchManager.getPreviousPoint(0);
                    vcb = touchManager.getPoint(1);
                    vpb = touchManager.getPreviousPoint(1);

                    Vector2D current = touchManager.getVector(0, 1);
                    Vector2D previous = touchManager.getPreviousVector(0, 1);
                    float currentDistance = current.getLength();
                    float previousDistance = previous.getLength();

                    if (previousDistance != 0) {
                        scale *= currentDistance / previousDistance;
                    }
                }
            }

            invalidate();
        }
        catch(Throwable t) {
            // So lazy...
        }
        return true;
    }



    private void showMyDialog(String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setTitle("Sensor");
        builder.setMessage("High Value\n" + msg + "\nLow Value");

        // Set up the input
        final EditText input = new EditText(myContext);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        //	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_TextCaptured = input.getText().toString();
		      /*  m_Text = input.getText().toString();*/
                Toast.makeText(myContext,
                        "Sent to server: " + m_TextCaptured,
                        Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void reportSelected(){

        if (scale < 2.5) {
            if ((touchX / scale > redSpotX - 50 && touchX / scale < redSpotX + 50)
                    && (touchY / scale > redSpotY && touchY / scale < redSpotY + 50)) {
                str2 = "Selected red X " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale);
            } else if ((touchX / scale > yellowSpotX - 50 && touchX / scale < yellowSpotX + 50)
                    && (touchY / scale > yellowSpotY && touchY / scale < yellowSpotY + 50)) {
                str2 = "Selected Yellow X " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale);
            } else if ((touchX / scale > greenSpotX - 50 && touchX / scale < greenSpotX + 50)
                    && (touchY / scale > greenSpotY && touchY / scale < greenSpotY + 50)) {
                str2 = "Selected green X " + (int) touchX / scale + ", Y=" + (int) touchY / scale;
            } else if ((touchX / scale > magentaSpotX - 50 && touchX / scale < magentaSpotX + 50)
                    && (touchY / scale > magentaSpotY && touchY / scale < magentaSpotY + 50)) {
                str2 = "Selected magenta X " + (int) touchX / scale + ", Y=" + (int) touchY / scale;
            } else {
                str2 = "No Selection";
            }
        }
        else{
            if ((((touchX / scale) > (redSpotX - 50)) && ((touchX / scale) < (redSpotX + 50)))
                    && (((touchY / scale) > (redSpotY - 50)) && ((touchY / scale) < redSpotY))) {
                str2 = "Selected Red High " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale)  + ", Scale=" + (int) ( scale);
            }
            if ((touchX / scale > redSpotX - 50 && touchX / scale < redSpotX + 50)
                    && (touchY / scale > redSpotY && touchY / scale < redSpotY + 50)) {
                str2 = "Selected Red Low " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale) + ", Scale=" + (int) ( scale);
            }

            if ((((touchX / scale) > (yellowSpotX - 50)) && ((touchX / scale) < (yellowSpotX + 50)))
                    && (((touchY / scale) > (yellowSpotY - 50)) && ((touchY / scale) < yellowSpotY))) {
                str2 = "Selected Yellow High " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale)  + ", Scale=" + (int) ( scale);
            }
            if ((touchX / scale > yellowSpotX - 50 && touchX / scale < yellowSpotX + 50)
                    && (touchY / scale > yellowSpotY && touchY / scale < yellowSpotY + 50)) {
                str2 = "Selected Yellow Low " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale) + ", Scale=" + (int) ( scale);
            }

            if ((((touchX / scale) > (greenSpotX - 50)) && ((touchX / scale) < (greenSpotX + 50)))
                    && (((touchY / scale) > (greenSpotY - 50)) && ((touchY / scale) < greenSpotY))) {
                str2 = "Selected Green High " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale)  + ", Scale=" + (int) ( scale);
            }
            if ((touchX / scale > greenSpotX - 50 && touchX / scale < greenSpotX + 50)
                    && (touchY / scale > greenSpotY && touchY / scale < greenSpotY + 50)) {
                str2 = "Selected Green Low " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale) + ", Scale=" + (int) ( scale);
            }

            if ((((touchX / scale) > (magentaSpotX - 50)) && ((touchX / scale) < (magentaSpotX + 50)))
                    && (((touchY / scale) > (magentaSpotY - 50)) && ((touchY / scale) < magentaSpotY))) {
                str2 = "Selected Magenta High " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale)  + ", Scale=" + (int) ( scale);
            }
            if ((touchX / scale > magentaSpotX - 50 && touchX / scale < magentaSpotX + 50)
                    && (touchY / scale > magentaSpotY && touchY / scale < magentaSpotY + 50)) {
                str2 = "Selected Magenta Low " + (int) (touchX / scale) + ", Y=" + (int) (touchY / scale) + ", Scale=" + (int) ( scale);
            }


        }
    }

    private void reportData(){

     //   str = "pos X=" + (int)position.getX() + ", pos Y= " + (int)position.getY()
     //           + ",    bitmap h=" +  (int)(bitmap.getHeight()*scale)
     //           + ",bitmap w=" + (int)(bitmap.getWidth()*scale);

        str0 = "Constant Touch X=" + (int)touchX +
                ", Y=" +  (int)touchY + ", scale=" + scale;


        str1 = "w=" + (int)getWidth() + " , h=" + (int)getHeight() + ", scale=" + scale;
       // str2 = "greenSpotX = " + greenSpotX + " ,greenSpotY = " + greenSpotY;
       // str3 = "";
    }

    private void drawData(Canvas myCanvas, Paint paint){

        paint.setColor(Color.BLACK);
    //    myCanvas.drawText(str, 1 / scale, 25 / scale, paint);
        myCanvas.drawText(str0,1/scale, 50/scale, paint);
    //    myCanvas.drawText(str1,1/scale, 75/scale, paint);

        if (!TextUtils.isEmpty(str2)) {
            myCanvas.drawText(str2,1/scale, 25/scale, paint);
        }
        else {
            myCanvas.drawText("", 1 / scale, 25 / scale, paint);
        }
    }
    private void drawSensors(Canvas myCanvas, Paint paint){

        sensor1.draw(myCanvas, myPaint, scale);
        magentaSpotX = sensor1.getPosX(scale);
        magentaSpotY = sensor1.getPosY(scale);

        sensor2.draw(myCanvas, myPaint, scale);
        greenSpotX = sensor2.getPosX(scale);
        greenSpotY = sensor2.getPosY(scale);

        sensor3.draw(myCanvas, myPaint, scale);
        yellowSpotX = sensor3.getPosX(scale);
        yellowSpotY = sensor3.getPosY(scale);

        sensor4.draw(myCanvas, myPaint, scale);
        redSpotX = sensor4.getPosX(scale);
        redSpotY = sensor4.getPosY(scale);
    }


    void setUpSensors(){

        sensor1 = new Sensor(myCanvas, myPaint, position, "Sen1");
        sensor1.init((int)getWidth(), Color.MAGENTA, (int)(bitmap.getWidth()/11.0), (int)(bitmap.getWidth()/2.45), textSize );

        sensor2 = new Sensor(myCanvas, myPaint, position, "Sen2");
        sensor2.init((int)getWidth(), Color.GREEN, (int)(bitmap.getWidth()/11.0), (int)(bitmap.getWidth()/3.5), textSize );

        sensor3 = new Sensor(myCanvas, myPaint, position, "Sen3");
        sensor3.init((int)getWidth(), Color.YELLOW, (int)(bitmap.getWidth()/11.0),-(int)(bitmap.getWidth()/4.2), textSize );

        sensor4 = new Sensor(myCanvas, myPaint, position, "Sen4");
        sensor4.init((int)getWidth(), Color.RED, (int)(bitmap.getWidth()/11.0), 0, textSize );

    }
}






/*
  public String getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "portrait";
            case Surface.ROTATION_90:
                return "landscape";
            case Surface.ROTATION_180:
                return "reverse portrait";
            default:
                // TODO what is "reverse landscape";
                return "default landscape";
        }
    }

    //  sensor1.init((int)getWidth(), Color.CYAN, 60, 270, textSize );
      //  sensor1.init((int)getWidth(), Color.CYAN, 60, 200, textSize );
      //  sensor1.init((int)getWidth(), Color.CYAN, 60, (int)(bitmap.getWidth()/2.45), textSize );

    //        Toast.makeText(myContext, str2, Toast.LENGTH_SHORT).show();
 */