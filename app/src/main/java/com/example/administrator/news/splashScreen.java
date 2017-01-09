package com.example.administrator.news;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;


public class splashScreen extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

        setContentView(R.layout.activity_splash_screen);

        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo("org.wordpress.android", 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            Log.e("debug", "第一次运行");
            editor.putBoolean("isFirstRun", false);
            editor.commit();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(splashScreen.this, guideActivity.class);
                    splashScreen.this.startActivity(mainIntent);
                    splashScreen.this.finish();
                }
            }, 2900);
        } else {
            Log.e("debug", "不是第一次运行");
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(splashScreen.this, MainActivity.class);
                    splashScreen.this.startActivity(mainIntent);
                    splashScreen.this.finish();
                }
            }, 2900);
        }

    }
}