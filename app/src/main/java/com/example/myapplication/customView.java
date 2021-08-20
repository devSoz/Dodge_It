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
    private Float xRunStart, yRunStart, ballSlope, xCanvas, yCanvas, xWall, yWall,yBottom;
    private Float xDeltaWall = Float.valueOf(20), yDeltaWall = Float.valueOf(10);
    private Boolean updateView=true;
    private SoundPool soundPool;
    private RectF endRect;
    private Integer interval = 40, score, flagClick = 0, flagUp=1, velocity=15;
    private Ball ballObj;
    private Integer cnt=0;
    private Wall wallObj, wallObj2;

    private class UpdateViewRunnable implements Runnable {
        public void run()
        {
            //movePong();
            if(updateView) {
                moveWall();
                cnt++;
                if(cnt>=20) cnt=0;
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCanvas = canvas;
        mCanvas.drawColor(getResources().getColor(R.color.backgnd));
        //Paint paint = new Paint();

        if(ballObj==null)
        {
            setDefault();
            ballObj = new Ball(xCanvas, yCanvas);
            wallObj = new Wall(xCanvas, yCanvas);
         //   wallObj2 = new Wall(xCanvas, yCanvas);
        }

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        mCanvas.drawRect(wallObj.rWall, paint);


        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        if(flagClick==1)
        {
            moveBall();
            mCanvas.drawCircle(ballObj.x, ballObj.y, ballObj.radius, paint);
        }
        else {
            mCanvas.drawCircle(ballObj.x, ballObj.y, ballObj.radius, paint);
        }
        paint.setColor(getResources().getColor(R.color.foregnd));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20f);
        mCanvas.drawLine(0,yBottom+15,xCanvas,yBottom+15,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        xWall= event.getX();
        yWall = event.getY();
        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
            {
                flagClick = 1;
                break;
            }
        }

            postInvalidate();

        return true;
    }

    public void moveBall()
    {
        if(wallObj.rWall.contains((ballObj.x+ballObj.radius), (ballObj.y)+ballObj.radius))

        if(ballObj.y<=(yCanvas/8))
        {
            flagUp=0;
        }
        if(flagUp==0) {
            if((ballObj.y+ballObj.radius)>=yBottom) {
                flagClick = 0;
                flagUp = 1;
            }
            else
                ballObj.y += velocity;
        }
        else
            ballObj.y-=velocity;


    }

    private void moveWall()
    {
        if(wallObj.rWall.right<0)
        {
            //wallObj.rWall.top = yCanvas-500;
          //  wallObj.rWall.bottom = wallObj.rWall.top + wallObj.height;
            wallObj.rWall.left = xCanvas;
            wallObj.rWall.right = wallObj.rWall.left + wallObj.width;
           /* wallObj.rWall.top = yCanvas;
            wallObj.rWall.left = 300;//3*(xCanvas/8);
            wallObj.rWall.bottom = yCanvas + wallObj.height;
            wallObj.rWall.right = wallObj.rWall.left + wallObj.width;*/
        }
        else
        {
            wallObj.rWall.left-=xDeltaWall;
            wallObj.rWall.right-=xDeltaWall;
        }
        invalidate();
    }

    private class Ball
    {
        Float x;
        Float y;
        Integer radius;

        public Ball(Float xCanvas, Float yCanvas)
        {
            this.radius = 30;
            this.x = xCanvas/10;
            this.y = yBottom-this.radius;
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.foregnd));
            mCanvas.drawCircle(this.x, this.y, this.radius, paint);
        }

        public void ballHit() {

        }
    }

    private class Wall
    {
        Integer width;
        Integer height;
        RectF rWall;
        Float xWall, yWall;

        public Wall(Float xCanvas, Float yCanvas)
        {
            this.height = 200;
            this.width = 50;

            rWall = new RectF();
            rWall.top = yBottom-this.height;
            rWall.bottom = rWall.top + this.height;
            rWall.left = xCanvas;
            rWall.right = rWall.left + this.width;

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            mCanvas.drawRect(rWall, paint);
        }
    }

    public void setDefault()
    {
        xCanvas = Float.valueOf(mCanvas.getWidth());
        yCanvas = Float.valueOf(mCanvas.getHeight());
        yBottom = yCanvas-(yCanvas/8);
        mCanvas.drawColor(getResources().getColor(R.color.backgnd));

        //xRunStart = xCanvas/2;
        //yRunStart = yCanvas/2;
        Random random = new Random();
        //xRandomStart = random.nextInt((int) (xCanvas*0.9+1)-10);
    }

    Boolean intersects()
    {
        Float cX = Math.abs(ballObj.x - wallObj.rWall.left);
        Float cY = Math.abs(ballObj.y - wallObj.rWall.top);

        if (cX > (wallObj.width/2 + ballObj.radius)) { return false; }
        if (cY > (wallObj.height/2 + ballObj.radius)) { return false; }

        if (cX <= (rect.width/2)) { return true; }
        if (cY <= (rect.height/2)) { return true; }

        cornerDistance_sq = (circleDistance.x - rect.width/2)^2 +
                (circleDistance.y - rect.height/2)^2;

        return (cornerDistance_sq <= (circle.r^2));
    }

    public void debugCanvas()
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
      //  mCanvas.drawText(String.valueOf(yRandom), 200, 200, paint);
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
    private void storeScore()
    {
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
