package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class customView extends View
{
    private Canvas mCanvas;
    public Float xRunStart, yRunStart, ballSlope, xCanvas, yCanvas;
    public Boolean updateView=true;
    private SoundPool soundPool;
    private RectF endRect;
    public Integer interval;

    private class UpdateViewRunnable implements Runnable {
        public void run()
        {
            //movePong();
            if(updateView) {
                postDelayed(this, interval);
            }
        }
    }

    private UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();
    public customView(Context context)
    {
        super(context);
        soundEnable(context);
        init(null);
    }

    public customView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public customView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public customView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateView = true;
        postDelayed(updateViewRunnable, interval);
    }

    @Override
    protected void onDetachedFromWindow() {
        updateView = false;
        super.onDetachedFromWindow();
    }

    private void init(@Nullable AttributeSet set)
    {

    }


    //Enable sound when hit on wall or slider or when game is over
    public void soundEnable(Context context){
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes
                    audioAttributes
                    = new AudioAttributes
                    .Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool= new SoundPool
                    .Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else {
            soundPool= new SoundPool(3, AudioManager.STREAM_MUSIC,0);
        }



        //beep sound for hit and game over
       /* gameOver= soundPool.load(context, R.raw.beep2,1);
        wallHit= soundPool.load(context, R.raw.beep4,1);
        sliderHit= soundPool.load(context, R.raw.beep4,1);*/


    }

    //play different sound based on the action
   /* public void playSound(int i)
    {
        switch (i) {

            case 1:
                soundPool.play(gameOver, 1, 1, 0, 0, 1);
                break;
            case 2:
                soundPool.play(wallHit, 1, 1, 0, 0, 1);
                break;
            case 3:
                soundPool.play(sliderHit, 1, 1, 0, 0, 1);
                break;
        }
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        xSlider= event.getX();
        ySlider = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                flagBall = 1;

                if (sliderObj.slider.contains(xSlider, ySlider)) {
                    flagSliderMove = 1;
                    //Log.d("test","down");
                    sliderObj.setSliderPos(xSlider);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (flagSliderMove == 1) {
                    sliderObj.setSliderPos(xSlider);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                flagSliderMove = 0;
                // Log.d("test","up");
                break;
            }
        }
        if(flagSliderMove==1)
        {
            postInvalidate();
        }
        return true;
    }


    private class ball
    {
        Float x;
        Float y;
        Integer radius;

        public ball()
        {
            this.radius = 20;
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.foregnd));
            mCanvas.drawCircle(300, 100, this.radius, paint);
        }

        public void moveBall()
        {
            
        }
        public void ballHit() {
            xRunStart = xRunStart + xDelta * ballSlope;
            yRunStart = yRunStart + yDelta;
            //Log.d("test","inside hit");
        }
    }

    private class cSlider
    {
        Integer width;
        Integer height;
        RectF cSlider;

        public cSlider(Float canvasHeight)
        {
            this.width = 200;
            this.height = 50;

            cSlider = new RectF();
            cSlider.top = 0;
            cSlider.left = xCanvas/2-width/2;
            cSlider.bottom = height;
            cSlider.right = cSlider.left+width;

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            mcanvas.drawRect(cSlider, paint);
        }

        public void setcSliderPos()
        {
            if((flagFirstTopHit==0)&&(mode==1))
            {
                xcSlider= (float) (xDelta*0.2)*canvasSide;
            }
            else
            {    xcSlider = (float) (xDelta);  }

            if((cSlider.right>=xCanvas)&&(xcSlider>0.0)) {
                Log.d("Right", String.valueOf(cSlider.right) + "," +String.valueOf(xDelta));
            }
            else if((cSlider.left<=0.0)&&(xcSlider<0.0)){
                Log.d("Left", String.valueOf(cSlider.left) + "," +String.valueOf(xDelta));
            }


            else {
                if((mode==2)&&(level==3))
                {
                    cSlider.left = xRunStart-(cSliderObj.width/2);
                    cSlider.right = xRunStart+(cSliderObj.width/2);
                }
                else {
                    Log.d("else", String.valueOf(cSlider.left) + "," + String.valueOf(xcSlider) + "," + String.valueOf((cSlider.left <= 0.0) && (xDelta < 0.0)));
                    cSlider.left += xcSlider;
                    cSlider.right += xcSlider;
                }
            }
        }

    }


    public void setDefault()
    {
        xCanvas = Float.valueOf(mCanvas.getWidth());
        yCanvas = Float.valueOf(mCanvas.getHeight());
        mcanvas.drawColor(getResources().getColor(R.color.backgnd));

        xRunStart = xCanvas/2;
        yRunStart = yCanvas/2;
        Random random = new Random();
        xRandomStart = random.nextInt((int) (xCanvas*0.9+1)-10);
        if(xRandomStart<xRunStart)
        {
            xRandomStart = xRunStart-xRandomStart;
            canvasSide=-1;
        }
        else
        {
            xRandomStart = xRandomStart-xRunStart;
            canvasSide=1;
        }
        ballSlope = (xRandomStart)/yRunStart;
    }

    public void debugCanvas()
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        //mcanvas.drawText(String.valueOf(yRandom), 200, 200, paint);
        //mcanvas.drawText(String.valueOf(yCanvas), 100, 300, paint);
        //Log.d("debug", String.valueOf(xRunStart) + "," +String.valueOf(yRunStart) + " delta" +String.valueOf(xDelta) + "," + String.valueOf(yDelta)  );

        //mcanvas.drawText(String.valueOf(xRunStart), 100, 100, paint);
        //mcanvas.drawText(String.valueOf(yRunStart), 200, 100, paint);
        //mcanvas.drawText(String.valueOf(ballSlope), 200, 200, paint);
        //mcanvas.drawText(String.valueOf(xDelta), 100, 400, paint);
        //mcanvas.drawText(String.valueOf(yDelta), 200, 400, paint);
    }

    //stores the highest score in shared preferences to show in main screen
    //This function is called when the balls misses
    private void storeScore() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Integer highScore;
        highScore = Integer.parseInt(sharedPreferences.getString("highScore", "0"));

        if (score > highScore) {
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("highScore", String.valueOf(score));
            myEdit.commit();
        }
    }
}
