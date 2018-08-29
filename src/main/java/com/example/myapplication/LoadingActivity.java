package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by 성규 on 2017-05-25.
 */

public class LoadingActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate( savedInstanceState);
        setContentView(R.layout.activity_loading);

        startLoading();
    }

    private void startLoading(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}
