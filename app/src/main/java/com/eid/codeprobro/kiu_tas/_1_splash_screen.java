package com.eid.codeprobro.kiu_tas;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class _1_splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

    Thread th=new Thread(new Runnable() {
        @Override
        public void run() {
            try{
                Thread.sleep(1000);
                Intent in=new Intent(_1_splash_screen.this, _2_Register_page.class);
                startActivity(in);
                finish();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    });
    th.start();
    }
}