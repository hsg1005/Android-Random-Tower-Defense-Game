package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by 성욱 on 2017-06-22.
 */

/*
class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 2;
    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE contacts(_id INTEGER RIMARY KEY"+"AUTOINCREMENT, name TEXT, track TEXT);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }
}*/

public class ShowScoreActivity extends Activity {

    //DBHelper helper;
    //SQLiteDatabase db;
    //TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);

        //뒤로가기 버튼
        Button back = (Button) findViewById(R.id.show_score_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

/*
        try{
            db = helper.getWritableDatabase();
        }catch (SQLiteException ex){
            db = helper.getReadableDatabase();
        }
        //edit_name = (EditText) findViewById(R.id.et_name);
        //edit_track = (EditText) findViewById(R.id.et_track);
    }

    public void insert(View target){
        //String name = edit_name.getText().toString();
        //String track = edit_track.getText().toString();
        //db.execSQL("INSERT INTO contacts VALUES (null, '"+name+"','"+track+"');");

        //edit_name.setText("");
        //edit_track.setText("");
    }

    public void search(View target){
        String s = "";
        Cursor cursor;
        cursor = db.rawQuery("SELECT name, track FROM contacts;",null);
        while(cursor.moveToNext()){
            s += cursor.getString(0)+" ";
            s += cursor.getString(1)+"\n";
        }
        tv.setText(s);

     */

    }
}
