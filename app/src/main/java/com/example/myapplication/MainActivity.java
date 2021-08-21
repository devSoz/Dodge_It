package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    //private List<Integer> scoreList = new ArrayList<Integer>();
    private List<String> strTime = new ArrayList<String>();
    private List<TextView> txtScore = new ArrayList<TextView>();
    private List<Integer> resCol;
    private PopupWindow popUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        clickListener();
        resCol = new ArrayList<Integer>();

        showScore();
        popup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("test","resume");
        showScore();
    }

    //get score from the shared preferences
    private void showScore()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        Float highScore;
        resCol.add(R.id.txtScore1);
        resCol.add(R.id.txtScore2);
        resCol.add(R.id.txtScore3);
        resCol.add(R.id.txtScore4);
        resCol.add(R.id.txtScore5);

        for(int i = 1;i<=5; i++)
        {
            highScore = Float.parseFloat(sharedPreferences.getString("Score" + String.valueOf(i), "0"));
            //scoreList.add(highScore);
            txtScore.add(findViewById(resCol.get(i-1)));

            int min = (int) (highScore / 6);
            int sec = (int) ((highScore) % 6);
            txtScore.get(i-1).setText(String.format ("%02d", min) + ":" + String.format ("%02d", sec) +"   " + String.valueOf(highScore) );
        }


    }


    private void clickListener()
    {
        Button b1 = (Button) findViewById(R.id.btnNew);
        Button b2 = (Button) findViewById(R.id.btnhelp);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //getLevel();
                Intent intentCanva = new Intent(MainActivity.this, Canva.class);
                //intentCanva.putExtra("level", level);
                //intentCanva.putExtra("mode", mode);
                startActivity(intentCanva);
            }
        });

      /*  b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder alertText1 = new StringBuilder(50);
                alertText1.append("Scoring Criteria:\n\n");
                alertText1.append("One Player mode: +2 points per hit \nCom vs Player mode: +2 points per hit, \n\t\t+5 for when computer misses");
                //alertText1.append(Double.toString(lorentzFactor));
                alertShow(alertText1);
            }
        });*/
    }

    private void alertShow(StringBuilder alertText)
    {
        AlertDialog.Builder alertAnswer = new AlertDialog.Builder(MainActivity.this);

        alertAnswer.setMessage(alertText)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
        AlertDialog createAlert = alertAnswer.create();
        createAlert.show();
    }

    //for help
    public void popup()
    {
        ScrollView rel = (ScrollView) findViewById(R.id.rel12) ;
        Button but = (Button) findViewById(R.id.btnhelp);
        but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Display display = getWindowManager().getDefaultDisplay();

                // Load the resolution into a Point object
                Point size = new Point();

                display.getSize(size);
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View custview = inflater.inflate(R.layout.popup, null);
                popUp = new PopupWindow(custview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView tv = custview.findViewById(R.id.tv);
                tv.setText(Html.fromHtml("<br><br><br><br><u><b>Rules:</b></u><br><br>" +
                        "You will get one point for stepping into every non-mine tile.<br><br>" +
                        "And will be losing the game when stepped into the mine.<br><br>"  +
                        "In level 3, the number of mines in the neighbouring tiles are revealed on the tile you choose, which can be used to track the mines and play the game.<br><br>" +
                        "<u><b>Number of Mines:</b></u><br>" +
                        "<ol>" +
                        "<li>&nbsp;Level 1: 10 mines</li>" +
                        "<li>&nbsp;Level 2: 13 mines</li>" +
                        "<li>&nbsp;Level 3: 3, 6, 9, 12, 15 mines as per difficulty.</li><br>" ));



                ImageButton btnclose =  custview.findViewById(R.id.btnclose);
                btnclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popUp.dismiss();
                    }
                });
                popUp.showAtLocation(rel, Gravity.NO_GRAVITY, 10, 10);
                popUp.update(50, 50, size.x-100, size.y-100);
            }});
    }

}