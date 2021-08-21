/*
package com.example.myapplication;


        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.view.WindowManager;
        import android.view.animation.Animation;
        import android.view.animation.AnimationUtils;
        import android.widget.ImageView;
        import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    ImageView imageView;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = (ImageView)findViewById(R.id.imageSplash);
        Animation an2= AnimationUtils.loadAnimation(this,R.anim.animation_splash);
        imageView.startAnimation(an2);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            //public void run() {
                // Intent i = new Intent(Splash.this, MainActivity.class);
                //startActivity(i);
                // finish();
                // This method will be executed once the timer is over
                // Start your app main activity
                /*
                if (AppStatus.getInstance(Splash.this).isOnline()) {
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                    finish();

                    //           Toast.makeText(this,"You are online!!!!",Toast.LENGTH_LONG).show();

                } else {

                    ContextThemeWrapper ctw = new ContextThemeWrapper( Splash.this, R.style.Theme_AlertDialog);
                    final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctw);
                    alertDialogBuilder.setTitle("No internet connection");
                    alertDialogBuilder.setMessage("Check your  internet connection or try again");
                    alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    alertDialogBuilder.show();
                }
                startActivity(new Intent(Splash.this, MainActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);


    }
}*/