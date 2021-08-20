package com.example.myapplication;

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

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class customView extends View
{
    private Canvas mCanvas;
    private Float xRunStart, yRunStart, ballSlope, xCanvas, yCanvas, xWall, yWall,yBottom;
    private Float xDeltaWall = Float.valueOf(20), yDeltaWall = Float.valueOf(10), vX, vX2, vMid;
    private Boolean updateView=false;
    private SoundPool soundPool;
    private RectF endRect;
    private Integer interval = 40, score, flagClick = 0, flagUp=1, flagStart=0, velocity=25, secondsPassed=0;
    private Ball ballObj;
    private Integer cnt=0, flagGameOver = 0;
    private Wall wallObj, wallObj2;
    private Drawable mCustomImage;
    private Paint paint;


    private class UpdateViewRunnable implements Runnable {
        public void run()
        {
            //movePong();
            if(updateView) {

                if(flagStart==1) {
                    secondsPassed++;
                    moveWall();
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

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        if(ballObj==null)
        {
            setDefault();
            ballObj = new Ball(xCanvas, yCanvas);
            paint.setTextSize(50);
            mCanvas.drawText(String.valueOf("Click to start"), xCanvas/3, yCanvas/2, paint);
        }
        if(flagStart==1) {
            if (wallObj == null ) {
                wallObj = new Wall(vX, vX + vMid);
                wallObj2 = new Wall(vX2, vX2 + vMid);
                //   wallObj2 = new Wall(xCanvas, yCanvas);
            }
            mCanvas.drawRect(wallObj.rWall, paint);
            mCanvas.drawRect(wallObj2.rWall, paint);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            if(flagClick==1)
            {
                moveBall();
                mCanvas.drawCircle(ballObj.x, ballObj.y, ballObj.radius, paint);
            }
            else {
                if(wallObj.rWall.contains(ballObj.x+ballObj.radius, ballObj.y))
                {
                    gameEnd();
                }
                mCanvas.drawCircle(ballObj.x, ballObj.y, ballObj.radius, paint);
            }
        }

        paint.setColor(getResources().getColor(R.color.foregnd));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20f);
        mCanvas.drawLine(0,yBottom+15,xCanvas,yBottom+15,paint);

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
                if(flagStart==1)
                    flagClick = 1;
                flagStart = 1;
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
            gameEnd();
            flagClick=0;
        }
        if(ballObj.y<=3*(yCanvas/8))
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
            //wallObj.rWall.left = xCanvas;
            //wallObj.rWall.right = wallObj.rWall.left + wallObj.width;
            wallObj.resetWall();
        }

        else
        {
            wallObj.rWall.left-=xDeltaWall;
            wallObj.rWall.right-=xDeltaWall;
        }

        if(wallObj2.rWall.right<0)
        {
            //wallObj2.rWall.left = xCanvas;
            //wallObj2.rWall.right = wallObj.rWall.left + wallObj.width;
            wallObj2.resetWall();
        }
        else
        {
            wallObj2.rWall.left-=xDeltaWall;
            wallObj2.rWall.right-=xDeltaWall;
        }
        invalidate();
    }

    public void setTimer()
    {
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create("Odyssey", Typeface.NORMAL));
        int min = (int) (secondsPassed / 60);
        int sec = (int) ((secondsPassed) % 60);
        paint.setTextSize(60);
     //   mCanvas.drawText(String.format ("%02d", min) + ":" + String.format ("%02d", sec), (xCanvas - 2 * board.tileSize+10), board.boardRect.top-25, paint2);
    }

    //SHow respective image based on if user lost or won, and explode the image
    private void endGame()
    {
      /*  Rect rect = new Rect(Integer.valueOf((int) (board.boardRect.left+50)), Integer.valueOf((int) (board.boardRect.bottom+50)), Integer.valueOf((int) (xCanvas-50)), Integer.valueOf((int) (yCanvas-100)));
        if((flagClickMine==1)) {
            mCustomImage = getResources().getDrawable(R.drawable.oops);
        }
        else{
            mCustomImage = getResources().getDrawable(R.drawable.congrats);
        }

        mCustomImage.setBounds(rect);
        mCustomImage.draw(mcanvas);

        if(flagEnd>=1){
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.oops);
            RectF rectF = board.boardRect;
            rectF.round(rect);
            final ExplosionField explosionField = ExplosionField.attach2Window((Activity) getContext());
            explosionField.explode(bm, rect, 200, 7000);
        }
        if(flagEnd==7) {
            Intent intentCanva = new Intent((Activity) getContext(), MainActivity.class);
            intentCanva.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getContext().startActivity(intentCanva);
        }*/
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
        Integer height;
        RectF rWall;
        Float x1, x2;

        public Wall(Float x1, Float x2)
        {
            this.height = 200;
            this.width = 50;
            this.x1 = x1;
            this.x2 = x2;
            rWall = new RectF();
            resetWall();
            /*
            rWall.top = yBottom-this.height;
            rWall.bottom = rWall.top + this.height;
            rWall.left = xCanvas;
            rWall.right = rWall.left + this.width;*/

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            mCanvas.drawRect(rWall, paint);
        }

        public void resetWall()
        {
            Random random = new Random();
            rWall.left = random.nextInt((int)(this.x2 - this.x1))+this.x1;
            this.width = random.nextInt (150 - 50)+50;
            Log.d("debug random", String.valueOf(rWall.left)+ ", "+this.x1+", "+this.x2);
            rWall.right = rWall.left + this.width;
            rWall.top = yBottom-this.height;
            rWall.bottom = rWall.top + this.height;
        }
    }

    public void setDefault()
    {
        xCanvas = Float.valueOf(mCanvas.getWidth());
        yCanvas = Float.valueOf(mCanvas.getHeight());
        vX = xCanvas;
        vX2 = 3*(xCanvas/2);//7*(xCanvas/4);
        vMid = Float.valueOf(400);
        yBottom = yCanvas-(yCanvas/8);
        mCanvas.drawColor(getResources().getColor(R.color.backgnd));

        //xRunStart = xCanvas/2;
        //yRunStart = yCanvas/2;
        Random random = new Random();
        //xRandomStart = random.nextInt((int) (xCanvas*0.9+1)-10);
    }

    private Boolean ballHit(Wall wall)
    {
        Float cX = Math.abs(ballObj.x - wallObj.rWall.left);
        Float cY = Math.abs(ballObj.y - wallObj.rWall.top);

        if (cX > (wallObj.width/2 + ballObj.radius)) { return false; }
        if (cY > (wallObj.height/2 + ballObj.radius)) { return false; }

        if (cX <= (wallObj.width/2)) { return true; }
        if (cY <= (wallObj.height/2)) { return true; }

        double distance = Math.pow((cX-wallObj.width/2),2) + Math.pow((cY-wallObj.height/2), 2);
        if(wallObj.rWall.contains(ballObj.x+ballObj.radius, ballObj.y))
            return true;
        return (distance <= (Math.pow(ballObj.radius, 2)));
    }

    public void gameEnd()
    {
        Toast toast;
        toast = Toast.makeText(getContext(), "Chumo ball hit:", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void debugCanvas()
    {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        mCanvas.drawText(String.valueOf(wallObj.rWall.left), 200, 200, paint);
        mCanvas.drawText(String.valueOf(wallObj2.rWall.left), 100, 300, paint);

        Log.d("debug", String.valueOf(wallObj.rWall.left) + " ," +String.valueOf(wallObj2.rWall.left));

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
