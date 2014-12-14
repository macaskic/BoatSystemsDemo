package com.surveyorexpert.BoatControl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.surveyorexpert.math.Vector2D;

public class TouchManager {

	private final int maxNumberOfTouchPoints;

	private final Vector2D[] points;
	private final Vector2D[] previousPoints;

	public TouchManager(final int maxNumberOfTouchPoints) {
		this.maxNumberOfTouchPoints = maxNumberOfTouchPoints;

		points = new Vector2D[maxNumberOfTouchPoints];
		previousPoints = new Vector2D[maxNumberOfTouchPoints];
	}

	public boolean isPressed(int index) {
		return points[index] != null;
	}

	public int getPressCount() {
		int count = 0;
		for(Vector2D point : points) {
			if (point != null)
				++count;
		}
		return count;
	}

	public Vector2D moveDelta(int index) {

		if (isPressed(index)) {
			Vector2D previous = previousPoints[index] != null ? previousPoints[index] : points[index];
			return Vector2D.subtract(points[index], previous);
		}
		else {
			return new Vector2D();
		}
	}

	private static Vector2D getVector(Vector2D a, Vector2D b) {
		if (a == null || b == null)
			throw new RuntimeException("can't do this on nulls");

		return Vector2D.subtract(b, a);
	}

	public Vector2D getPoint(int index) {
		return points[index] != null ? points[index] : new Vector2D();
	}

	public Vector2D getPreviousPoint(int index) {
		return previousPoints[index] != null ? previousPoints[index] : new Vector2D();
	}

	public Vector2D getVector(int indexA, int indexB) {
		return getVector(points[indexA], points[indexB]);
	}

	public Vector2D getPreviousVector(int indexA, int indexB) {
		if (previousPoints[indexA] == null || previousPoints[indexB] == null)
			return getVector(points[indexA], points[indexB]);
		else
			return getVector(previousPoints[indexA], previousPoints[indexB]);
	}

	public void update(MotionEvent event) {
	   int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
    //    tvLoc.setText( event.getX() + " " + event.getY());
	   if (actionCode == MotionEvent.ACTION_POINTER_UP || actionCode == MotionEvent.ACTION_UP) {
		   int index = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;
		   previousPoints[index] = points[index] = null;
	   }
	   else {
			for(int i = 0; i < maxNumberOfTouchPoints; ++i) {
				if (i < event.getPointerCount()) {
					int index = event.getPointerId(i);

					Vector2D newPoint = new Vector2D(event.getX(i), event.getY(i));

					if (points[index] == null)
						points[index] = newPoint;
					else {
						if (previousPoints[index] != null) {
							previousPoints[index].set(points[index]);
						}
						else {
							previousPoints[index] = new Vector2D(newPoint);

						}

						if (Vector2D.subtract(points[index], newPoint).getLength() < 64)
							points[index].set(newPoint);
					}
				}
				else {
				   previousPoints[i] = points[i] = null;
				}
			}
	   }
	}

    public static class SimpleView extends View implements View.OnTouchListener {

        Context myContext = null;
        Canvas myCanvas = null;
        private final Bitmap bitmap;
        private final int width;
        private final int height;
        private Matrix transform = new Matrix();
        private String m_TextCaptured = "Reading";

        private Vector2D position = new Vector2D();
        private float scale = 1;
        private float angle = 0;
        private float touchX = 0;
        private float touchY = 0;



        private TouchManager touchManager = new TouchManager(2);
        private boolean isInitialized = false;

        // Debug helpers to draw lines between the two touch points
        private Vector2D vca = null;
        private Vector2D vcb = null;
        private Vector2D vpa = null;
        private Vector2D vpb = null;

        public SimpleView(Context context, Bitmap bitmap) {
            super(context);
            myContext = context;
            this.bitmap = bitmap;
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();

            setOnTouchListener(this);
        }


        private static float getDegreesFromRadians(float angle) {
            return (float)(angle * 180.0 / Math.PI);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            myCanvas = canvas;
            int w = 0;
            int h = 0;

            if (!isInitialized) {
                w = getWidth();
                h = getHeight();
                position.set(w / 2, h / 2);
                //   position.set(0,0);
                isInitialized = true;
            }

            Paint paint = new Paint();

            transform.reset();
            transform.postTranslate(-width / 2.0f, -height / 2.0f);
            transform.postRotate(getDegreesFromRadians(angle));
            transform.postScale(scale, scale);
            transform.postTranslate(position.getX(), position.getY());

            canvas.drawBitmap(bitmap, transform, paint);

            try {// TODO CGM stop the rotation gesture Circles

                //  float textSize = Math.min(width, height) * 0.12f;
                float textSize = Math.min(width, height) * 0.10f;
                paint.setTextSize(textSize/scale);
                canvas.scale(scale, scale);
                //   String str = "w =" + width + " , ht =" + height + ", scale = " + scale;
                //   String str = "w =" + w + " , ht =" + h + ", scale = " + scale;
                //   String str = "w=" + getWidth() + " , ht=" + getHeight() + ", scale=" + scale
                //           +  ", transform=" + transform.toString();


                String str = "w=" + getWidth() + " , h=" + getHeight() + ", scale=" + scale
                        +  ", pos X=" + position.getX() + ", pos Y= " + position.getY()
                        + ", bitmap h=" +  bitmap.getHeight()*scale + ",bitmap w=" + bitmap.getWidth()*scale;

                String str1 = "";
                String str2 = "";

                str1 = "Constant Touch X=" +  touchX + ", Y=" +  touchY + ", orientation=" + getRotation(myContext);

                if ((touchX > 1100 && touchX < 1250 )  && (touchY> 500 &&  touchY < 650)){
                    str2 = "Selected Touch X=" +  touchX + ", Y=" +  touchY ;
                }

    //            if ((touchX > 1100 && touchX < 1250 )  && (touchY> 500 &&  touchY < 650)){
    //                str2 = "Selected Touch X=" +  touchX + ", Y=" +  touchY ;
    //            }

                paint.setColor(Color.WHITE);

                canvas.drawText(str, 1/scale, 25/scale, paint);
                canvas.drawText(str1,1/scale, 50/scale, paint);
                canvas.drawText(str2,1/scale, 75/scale, paint);


                paint.setColor(0xFF007F00);
                //     canvas.drawCircle(1150/scale, 600/scale, 50/scale, paint);

                canvas.drawCircle((getWidth()*0.75f)/scale, (getHeight()*0.86f)/scale, 50/scale, paint);
                //      canvas.drawCircle(1150, 600, 50, paint);

                //  getWidth()
    //            canvas.drawCircle(vca.getX(), vca.getY(), 64, paint);
    //			paint.setColor(0xFF7F0000);
    //			canvas.drawCircle(vcb.getX(), vcb.getY(), 64, paint);
    //
    //			paint.setColor(0xFFFF0000);
    //			canvas.drawLine(vpa.getX(), vpa.getY(), vpb.getX(), vpb.getY(), paint);
    //			paint.setColor(0xFF00FF00);
    //			canvas.drawLine(vca.getX(), vca.getY(), vcb.getX(), vcb.getY(), paint);
            }
            catch(NullPointerException e) {
                // Just being lazy here...
            }
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            vca = null;
            vcb = null;
            vpa = null;
            vpb = null;
            try {

                touchManager.update(event);

                if (touchManager.getPressCount() == 1) {
                    vca = touchManager.getPoint(0);
                    vpa = touchManager.getPreviousPoint(0);
                    position.add(touchManager.moveDelta(0));

                    //     String str1 = "w=" + getWidth() + " , ht=" + getHeight() + ", scale=" + scale
                    //            +  ", pos X=" + position.getX() + ", pos Y= " + position.getY()
                    //            + ", bitmap h=" +  bitmap.getHeight()*scale + ",bitmap w=" + bitmap.getWidth()*scale;


                    if (myCanvas != null) {

                        touchX = event.getX();
                        touchY =  event.getY();
    //                    Paint paint = new Paint();
    //                    float textSize = Math.min(width, height) * 0.10f;
    //                    paint.setTextSize(textSize / scale);
    //               //     myCanvas.scale(scale, scale);
    //                    // myCanvas.scale(1.0f, 1.0f);
    //
    //                    String str1 = "Hello";
    //                    paint.setColor(Color.WHITE);
    //               //     myCanvas.drawText(str1, 1, 75, paint);
                    }

                    // x = 300 to 320, y= 340 to 360
                    //    if ((event.getX() > 1150/scale && event.getX() < 1200/scale )) {
                    //       &&     (event.getY() > 600/scale &&  event.getY() < 650/scale))

                    //       Toast.makeText(myContext,
                    //               "X Val: " + event.getX() + "Y Val: " + event.getY(),
                    //              Toast.LENGTH_SHORT).show();
                    //        showMyDialog("hello");
                    //   }
                }
                else {
                    if (touchManager.getPressCount() == 2) {
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
                        // TODO CGM stop the rotation gesture
                        //			angle -= Vector2D.getSignedAngleBetween(current, previous);

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
                    return "reverse landscape";
            }
        }


    }
}
