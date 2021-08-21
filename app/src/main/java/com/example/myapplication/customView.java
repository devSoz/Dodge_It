package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import tyrantgit.explosionfield.ExplosionField;

import static android.content.Context.MODE_PRIVATE;

public class customView extends View
{
    private Canvas mCanvas;
    private Float xCanvas, yCanvas, xWall, yWall,yBottom, speedWall, score;
    private Float xDeltaWall = Float.valueOf(20), yDeltaWall = Float.valueOf(10), vX, vX2, vMid;
    private Boolean updateView=false;
    private SoundPool soundPool;
    private RectF endRect;
    private Integer interval = 25, flagClick = 0, flagUp=1, flagStart=0, velocity=35, secondsPassed=0;
    private Ball ballObj;
    private Integer cnt=0,endSound, flagGameOver = 0, flagSpeedReset=0, timerCount=0, flagEnd=0;
    private int min, sec;
    private Wall wallObj, wallObj2;
    private Drawable mCustomImage;
    private Paint paintWall, paintBall, paintLine;


    private class UpdateViewRunnable implements Runnable {
        public void run()
        {
            //
            //if(flagEnd>0) {
                flagEnd++;
               // explodeGame();

            //}
            if(updateView) {
                if(flagStart==1) {
                    //secondsPassed++;
                    //if(flagEnd==0) {
                        timerCount++;
                        if(ballObj != null)
                            moveWall();
                    //}

                }
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
        endSound = soundPool.load(context, R.raw.beep2,1);
    }

    //play different sound based on the action
    public void playSound(int i)
    {
        soundPool.play(endSound, 1, 1, 0, 0, 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCanvas = canvas;
        mCanvas.drawColor(getResources().getColor(R.color.backgnd));
        //Paint paint = new Paint();

        //paintWall.setColor(Color.WHITE);
        //paintWall.setAntiAlias(true);
        if(ballObj==null)
        {
            paintWall = new Paint();
            paintBall = new Paint();
            paintLine = new Paint();
            setDefault();
            ballObj = new Ball(xCanvas, yCanvas);
            paintLine.setColor(getResources().getColor(R.color.clrGameLine));
            paintLine.setTextSize(70);
            paintLine.setTypeface(Typeface.create("Odyssey", Typeface.NORMAL));
            mCanvas.drawText(String.valueOf("Click any key to start!"), xCanvas/3, yCanvas/3, paintLine);
        }

        if(secondsPassed%5==0)
        {
            speedReset();
        }
        if(flagStart==1) {
            if (wallObj == null ) {
                wallObj = new Wall(vX, vX + vMid, 1);
                wallObj2 = new Wall(vX2, vX2 + vMid, 2);
                //   wallObj2 = new Wall(xCanvas, yCanvas);
            }
            mCanvas.drawRect(wallObj.rWall, paintWall);
            mCanvas.drawRect(wallObj2.rWall, paintWall);

            paintBall.setStyle(Paint.Style.FILL_AND_STROKE);
            paintBall.setColor(getResources().getColor(R.color.ball));
            if(flagClick==1)
            {
                moveBall();
                mCanvas.drawCircle(ballObj.x, ballObj.y, ballObj.radius, paintBall);
            }
            else {
                if(wallObj.rWall.contains(ballObj.x+ballObj.radius, ballObj.y)||(wallObj2.rWall.contains(ballObj.x+ballObj.radius, ballObj.y)))
                {
                    gameEnd();
                }
                mCanvas.drawCircle(ballObj.x, ballObj.y, ballObj.radius, paintBall);
            }
            paintLine.setColor(getResources().getColor(R.color.clrGameLine));
            paintLine.setStyle(Paint.Style.STROKE);
            paintLine.setStrokeWidth(5f);
            setTimer();
            mCanvas.drawText(String.format ("%02d", min) + ":" + String.format ("%02d", sec) + "      Score : " +String.format("%.1f", secondsPassed*0.1) , (xCanvas - xCanvas/3.0f), 50, paintLine);
        }

        paintLine.setColor(getResources().getColor(R.color.clrGameLine));
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(20f);
        mCanvas.drawLine(0,yBottom+15, xCanvas,yBottom+15, paintLine);

        //debugCanvas();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        xWall= event.getX();
        yWall = event.getY();
        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
            {
                //if(flagEnd==0) {
                    if (flagStart == 1)
                        flagClick = 1;
                    flagStart = 1;
                //}
                break;
            }
        }

        postInvalidate();
        return true;
    }

    public void moveBall()
    {
        //if(wallObj.rWall.contains((ballObj.x+ballObj.radius), (ballObj.y)+ballObj.radius))
        if(ballHit(wallObj) || ballHit(wallObj2))
        {
            flagGameOver = 1;
            //flagEnd=1;
            gameEnd();
        }
        if(ballObj.y<=(yCanvas/5))
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

            if (wallObj.rWall.right < 0) {
                //wallObj.rWall.left = xCanvas;
                //wallObj.rWall.right = wallObj.rWall.left + wallObj.width;
                wallObj.resetWall(wallObj2.rWall.left, 1);
            } else {
                wallObj.rWall.left -= xDeltaWall;
                wallObj.rWall.right -= xDeltaWall;
            }

            if (wallObj2.rWall.right < 0) {
                //wallObj2.rWall.left = xCanvas;
                //wallObj2.rWall.right = wallObj.rWall.left + wallObj.width;
                wallObj2.resetWall(wallObj.rWall.left, 2);
            } else {
                wallObj2.rWall.left -= xDeltaWall;
                wallObj2.rWall.right -= xDeltaWall;
            }

        invalidate();
    }

    public void setTimer()
    {
        paintLine.setColor(Color.WHITE);
        paintLine.setTypeface(Typeface.create("Odyssey", Typeface.NORMAL));
        if(timerCount%40==0)
            secondsPassed++;
        min = (int) (secondsPassed / 60);
        sec = (int) ((secondsPassed) % 60);
        paintLine.setTextSize(45);
        //mCanvas.drawText(String.format ("%02d", min) + ":" + String.format ("%02d", sec) + "      Score : " + secondsPassed , (xCanvas - xCanvas/2), 50, paintLine);
    }

    //SHow respective image based on if user lost or won, and explode the image
    private void explodeGame()
    {
        int xc=Math.round(xCanvas)/50;
        int yc=Math.round(yCanvas)/50;
        Rect rect = new Rect(xc*20,yc*20, xc*30,yc*30);

        //Integer.parseInt(xCanvas.toString()) ,Integer.parseInt(xCanvas.toString()));

        mCustomImage = getResources().getDrawable(R.drawable.congrats);



        mCustomImage.setBounds(rect);
        mCustomImage.draw(mCanvas);

        if(flagEnd>=1){
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.congrats);

            RectF rectF = new RectF(xc*5,yc*5,xc*50,yc*50);

            rectF.round(rect);
            final ExplosionField explosionField = ExplosionField.attach2Window((Activity) getContext());
            explosionField.explode(bm, rect, 200, 7000);
        }
        if(flagEnd>=100) {
            //updateView=0;
            Intent intentCanva = new Intent((Activity) getContext(), MainActivity.class);
            intentCanva.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            getContext().startActivity(intentCanva);
            ((Activity) getContext()).finish();
        }
    }

    private void drawPic(Rect rect)
    {
       // mCustomImage = getResources().getDrawable(R.drawable.mine);
      //  mCustomImage.setBounds(rect);
       // mCustomImage.draw(mcanvas);
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
          //  Paint paint = new Paint();
           // paint.setColor(getResources().getColor(R.color.foregnd));
          //  mCanvas.drawCircle(this.x, this.y, this.radius, paint);
        }


    }

    private class Wall
    {
        Integer width;
        Integer height, speed;
        RectF rWall;
        Float x1;
        Float x2;

        public Wall(Float x1, Float x2, Integer t)
        {
            this.height = 200;
            this.width = 50;
            this.x1 = x1;
            this.x2 = x2;
            rWall = new RectF();

            /*
            rWall.top = yBottom-this.height;
            rWall.bottom = rWall.top + this.height;
            rWall.left = xCanvas;
            rWall.right = rWall.left + this.width;*/
            Random random = new Random();
            if(t==1) {
                this.x1 = xCanvas;
                this.x2 = xCanvas+500;
            }
            if(t==2) {
                this.x1 = xCanvas+1000;
                this.x2 = xCanvas+1500;
            }
            rWall.left = random.nextInt((int)(this.x2 - this.x1))+this.x1;
            //this.speed = random.nextInt((int)(25 - 15))+15;
            this.width = random.nextInt (130 - 50)+50;
            this.height = random.nextInt (200 - 100)+100;
            Integer temp = random.nextInt(2-0)+0;
            if(temp==0) {
                paintWall.setColor(getResources().getColor(R.color.clrWall1));
                paintWall.setAntiAlias(true);
            }
            else
            {
                paintWall.setColor(getResources().getColor(R.color.clrWall2));
                paintWall.setAntiAlias(true);
            }
            Log.d("debug random", String.valueOf(rWall.left)+ ", "+this.x1+", "+ this.x2 +"   "+ t);
            rWall.right = rWall.left + this.width;
            rWall.top = yBottom-this.height;
            rWall.bottom = rWall.top + this.height;


            mCanvas.drawRect(rWall, paintWall);
        }

        public void resetWall(Float xtemp, Integer t)
        {
            Random random = new Random();
            this.x1 = xCanvas;
            this.x2 = (Float.parseFloat("1.8")*xCanvas);
         //   if(xtemp-this.rWall.left<1000)
            Float xy=xCanvas+(xtemp *Float.parseFloat( "0.5"));
            this.width = random.nextInt (130 - 50)+50;
            this.height = random.nextInt (200 - 100)+100;
            if(xy+this.width>(x2))
                xy=x2-this.width;
            this.x1=xy;
            rWall.left = random.nextInt((int)(this.x2 - this.x1))+this.x1;

            Integer temp = random.nextInt(3-0)+0;
            if(temp==2) {
                paintWall.setColor(getResources().getColor(R.color.clrWall1));
                paintWall.setAntiAlias(true);
            }
            else if(temp==1)
            {
                paintWall.setColor(getResources().getColor(R.color.clrWall2));
                paintWall.setAntiAlias(true);
            }
            else
            {
                paintWall.setColor(getResources().getColor(R.color.clrWall3));
                paintWall.setAntiAlias(true);
            }
            Log.d("debug random", String.valueOf(rWall.left)+ ", "+this.x1+", "+ this.x2 +"   "+ xtemp + "   t="+t);
            rWall.right = rWall.left + this.width;
            rWall.top = yBottom-this.height;
            rWall.bottom = rWall.top + this.height;
        }
    }

    public void setDefault()
    {
        xCanvas = Float.valueOf(mCanvas.getWidth());
        yCanvas = Float.valueOf(mCanvas.getHeight());
        vX = xCanvas+600;//6*(xCanvas/5);
        vX2 = xCanvas+1800;//8*(xCanvas/5);//7*(xCanvas/4);
        vMid = Float.valueOf(600); //Float.valueOf(xCanvas/5);
        yBottom = yCanvas-(yCanvas/4);
        mCanvas.drawColor(getResources().getColor(R.color.backgnd));


        //xRunStart = xCanvas/2;
        //yRunStart = yCanvas/2;
        Random random = new Random();
        //xRandomStart = random.nextInt((int) (xCanvas*0.9+1)-10);
    }

    private Boolean ballHit(Wall wall)
    {
        Float cX = Math.abs(ballObj.x - wall.rWall.left);
        Float cY = Math.abs(ballObj.y - wall.rWall.top);

        if (cX > (wall.width/2 + ballObj.radius)) { return false; }
        if (cY > (wall.height/2 + ballObj.radius)) { return false; }

        if (cX <= (wall.width/2)) { return true; }
        if (cY <= (wall.height/2)) { return true; }

        double distance = Math.pow((cX-wall.width/2),2) + Math.pow((cY-wall.height/2), 2);
        if((wallObj.rWall.contains(ballObj.x-ballObj.radius, ballObj.y+ballObj.radius))||(wallObj2.rWall.contains(ballObj.x-ballObj.radius, ballObj.y+ballObj.radius)))
            return true;
        //if((wallObj2.rWall.contains(ballObj.x+ballObj.radius, ballObj.y))||(wallObj2.rWall.contains(ballObj.x, ballObj.y+ballObj.radius)))
           // return true;

        return (distance <= (Math.pow(ballObj.radius, 2)));
    }

    public void gameEnd()
    {
        playSound(1);
        flagEnd=1;
        storeScore();
       // explode Game();
        Intent intentCanva = new Intent(getContext(), Canva.class);
        //intentCanva.putExtra("level", level);
        //intentCanva.putExtra("mode", mode);
        getContext().startActivity(intentCanva);

    }

    public void debugCanvas()
    {
       /* Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        mCanvas.drawText(String.valueOf(wallObj.rWall.left), 200, 200, paint);
        mCanvas.drawText(String.valueOf(wallObj2.rWall.left), 100, 300, paint);*/

        Log.d("debug", String.valueOf(flagEnd) + " ," +String.valueOf(flagGameOver));

        //mcanvas.drawText(String.valueOf(xRunStart), 100, 100, paint);
        //mcanvas.drawText(String.valueOf(yRunStart), 200, 100, paint);
        //mcanvas.drawText(String.valueOf(ballSlope), 200, 200, paint);
        //mcanvas.drawText(String.valueOf(xDelta), 100, 400, paint);
        //mcanvas.drawText(String.valueOf(yDelta), 200, 400, paint);
    }

    //stores top 5 highest scores in shared preferences to show in leaderboard
    //This function is called when the balls hits the wall
    private void storeScore()
    {
        if(flagEnd!=3) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
            Float highScore;

            List<Float> scoreList = new ArrayList<Float>();
            for (int i = 1; i <= 5; i++) {
                highScore = Float.parseFloat(sharedPreferences.getString("Score" + String.valueOf(i), "0"));
                scoreList.add(highScore);

            }
            score = secondsPassed / 10f;
            scoreList.add(score);
            Collections.sort(scoreList);

            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            for (int i = 5; i >= 1; i--) {
                myEdit.putString("Score" + String.valueOf(i), String.valueOf(scoreList.get(i)));
                Log.d("Canvas", "from canvas " + scoreList.get(i));
            }
            myEdit.commit();
            flagEnd=3;
        }
    }

    public void speedReset()
    {
        Random random = new Random();
        xDeltaWall = Float.valueOf(random.nextInt((int)(25 - 15))+15);
    }
}
