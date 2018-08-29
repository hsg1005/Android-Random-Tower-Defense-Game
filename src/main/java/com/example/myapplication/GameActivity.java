package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by 성규 on 2017-05-24.
 */

// 게임의 시작
public class GameActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ScreenConfig ScreenConfig = new ScreenConfig();

        //휴대폰의 실제 화면 크기
        ScreenConfig.setScreenSize(getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight());
        // 가상 화면 크기
        ScreenConfig.setVirtualSize(1100, 2000);

        //풀스크린
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

}
