package com.pointbreak.reelitin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mydatabase = openOrCreateDatabase("REELITIN", MODE_PRIVATE, null);
                    Cursor c = mydatabase.rawQuery("SELECT * FROM REELITIN;", null);
                    c.moveToFirst();
                    Log.e("AC", c.getString(0));
                    if (!c.getString(0).equals("NONE"))
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    else
                        startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                    finish();
                }
                catch (Exception e) {
                    startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                    finish();
                }
            }
        }, 2000);
    }
}
