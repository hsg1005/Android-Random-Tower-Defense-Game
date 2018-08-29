package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by 성규 on 2017-06-07.
 */

public class MainMenu extends Activity implements View.OnClickListener{
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button playGame = (Button) findViewById(R.id.playGame);
        Button introduceThisGame = (Button) findViewById(R.id.introduceThisGame);
        Button setGame = (Button) findViewById(R.id.setGame);
        Button showScore = (Button) findViewById(R.id.showScore);
        Button finishGame = (Button) findViewById(R.id.finishGame);

        playGame.setOnClickListener(this);
        introduceThisGame.setOnClickListener(this);
        setGame.setOnClickListener(this);
        showScore.setOnClickListener(this);
        finishGame.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.playGame:
                intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                break;
            case R.id.introduceThisGame:
                intent = new Intent(this, IntroduceThisGameActivity.class);
                startActivity(intent);
                break;
            case R.id.showScore:
                intent = new Intent(this, ShowScoreActivity.class);
                startActivity(intent);
                break;
            case R.id.setGame:
                intent = new Intent(this, SetGameActivity.class);
                startActivity(intent);
                break;
            case R.id.finishGame:
                finish();
                break;
        }

    }
}
