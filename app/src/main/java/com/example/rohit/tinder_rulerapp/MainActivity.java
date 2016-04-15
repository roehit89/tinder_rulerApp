package com.example.rohit.tinder_rulerapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView displayValue;
    private int maxScreenHeight;
    static int halfCount = 0;
    private ImageView rulerImageView;
    private ImageView markerImageView;
    int lineLength;
    int longestLineLength = 160;
    Paint markerPaint;
    int Ycoordinate;
    Paint rulerPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        rulerImageView = (ImageView) this.findViewById(R.id.id_rulerImageView);
        markerImageView = (ImageView) this.findViewById(R.id.id_markerImageView);
        displayValue = (TextView) this.findViewById(R.id.id_rulerValue);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        maxScreenHeight = displayMetrics.heightPixels;
        Ycoordinate = maxScreenHeight;

        double x =  Math.pow(displayMetrics.heightPixels / displayMetrics.ydpi,2);
        double y  =  Math.pow(displayMetrics.widthPixels / displayMetrics.xdpi,2);
        double screenInches   =  Math.sqrt(x + y);

        int divisionsOnScale  = (int)(16 * screenInches);

        Log.i("screen inches =", String.valueOf(screenInches));
        Log.i("total parts =", String.valueOf(divisionsOnScale));


        final Bitmap[] rulerBitmap = {Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight(),
                Bitmap.Config.ARGB_8888)};
        final Bitmap[] markerBitmap = {Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight(),
                Bitmap.Config.ARGB_8888)};

        final Canvas rulerCanvas = new Canvas(rulerBitmap[0]);
        final Canvas markerCanvas = new Canvas(markerBitmap[0]);

        rulerImageView.setImageBitmap(rulerBitmap[0]);
        markerImageView.setImageBitmap(markerBitmap[0]);

        markerPaint = new Paint();
        markerPaint.setStrokeWidth(3);
        markerPaint.setColor(Color.parseColor("#458B00"));
        rulerPaint = new Paint();
        rulerPaint.setColor(Color.BLACK);
        rulerPaint.setStrokeWidth(2);



        for(int i=0; i<= divisionsOnScale; i++){  //loop to print scale line by line
            lineLength = findLineLength(i, rulerCanvas, rulerPaint, Ycoordinate);
            rulerCanvas.drawLine(0, Ycoordinate, lineLength, Ycoordinate, rulerPaint);
            Ycoordinate -= 20;
        }

        rulerImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int y = (int) event.getY();

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    markerBitmap[0].eraseColor(Color.TRANSPARENT);
                    markerCanvas.drawLine(0, y, 180, y, markerPaint);
                    markerImageView.invalidate();
                    showValue(y);
                }
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    markerBitmap[0].eraseColor(Color.TRANSPARENT);
                    markerCanvas.drawLine(0, y, 180, y, markerPaint);
                    markerImageView.invalidate();
                    showValue(y);
                }
                return true;
            }
        });
    }

    private int findLineLength(int i, Canvas rulerCanvas, Paint rulerPaint, int Ycoordinate) { // calculate length of each line
        if (i % 16 == 0){
            rulerPaint.setTextSize(40);
            rulerCanvas.save();
            rulerCanvas.rotate(-90, longestLineLength + 10, Ycoordinate + 10);
            rulerCanvas.drawText(String.valueOf(i / 16), longestLineLength + 12, Ycoordinate + 40, rulerPaint);
            rulerCanvas.restore();
            return longestLineLength;
        }
        else if(i%8 == 0){

            rulerPaint.setTextSize(30);
            rulerCanvas.save();
            rulerCanvas.rotate(-90, longestLineLength + 10, Ycoordinate + 10);
            rulerCanvas.drawText(halfCount+".5", longestLineLength + 12, Ycoordinate, rulerPaint);
            rulerCanvas.restore();
            Log.i("values received %8 ", String.valueOf(i));
            halfCount++;
            return (longestLineLength*3)/4;
        }
        else if(i%4 == 0){
            return 100;
        }
        else if(i%2 == 0){
            return longestLineLength/2;
        }
        else {
            return longestLineLength/4;
        }
    }

    private void showValue(int y) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float rulerValue = (float) (maxScreenHeight-y) / displayMetrics.densityDpi;
        displayValue.setText(String.format("%.2f",rulerValue) + " inch");

        Log.i("y =", String.valueOf(y));
        Log.i("maxScreenHeight =", String.valueOf(maxScreenHeight));
        Log.i("maxScreenHeight - y =", String.valueOf(maxScreenHeight-y));
        Log.i("dpi =", String.valueOf(displayMetrics.densityDpi));
        Log.i("value on ruler(scrnEnd-y/dpi) =", String.valueOf(rulerValue));
    }
}
