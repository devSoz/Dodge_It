package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    //private List<Integer> scoreList = new ArrayList<Integer>();
    private List<String> strTime = new ArrayList<String>();
    private List<TextView> txtScore = new ArrayList<TextView>();
    private List<Integer> resCol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        clickListener();
        resCol = new ArrayList<Integer>();

        showScore();
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
        Integer highScore;
        resCol.add(R.id.txtScore1);
        resCol.add(R.id.txtScore2);
        resCol.add(R.id.txtScore3);
        resCol.add(R.id.txtScore4);
        resCol.add(R.id.txtScore5);

        for(int i = 0;i<=4; i++)
        {
            highScore = Integer.parseInt(sharedPreferences.getString("Score" + String.valueOf(i), "0"));
            //scoreList.add(highScore);
            txtScore.add(findViewById(resCol.get(i)));

            int min = (int) (highScore / 60);
            int sec = (int) ((highScore) % 60);
            txtScore.get(i).setText(String.format ("%02d", min) + ":" + String.format ("%02d", sec) +"   " + String.valueOf(highScore) );
        }


    }


    private void clickListener()
    {
        ImageButton b1 = (ImageButton) findViewById(R.id.btn1);
        ImageButton b2 = (ImageButton) findViewById(R.id.btnHelp);
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

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder alertText1 = new StringBuilder(50);
                alertText1.append("Scoring Criteria:\n\n");
                alertText1.append("One Player mode: +2 points per hit \nCom vs Player mode: +2 points per hit, \n\t\t+5 for when computer misses");
                //alertText1.append(Double.toString(lorentzFactor));
                alertShow(alertText1);
            }
        });
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

}