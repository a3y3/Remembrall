package com.example.soham.remembrall;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class CreateNote extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;
    private String titleText, noteText, isCheckBox;
    private EditText title, note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        note = (EditText)findViewById(R.id.note_textfield);
        note.requestFocus();
        title = (EditText)findViewById(R.id.add_title);


        //All things database
        sqLiteDatabase = openOrCreateDatabase("Cards-Test1.db", getApplicationContext().MODE_PRIVATE,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CARDS(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE VARCHAR, NOTE VARCHAR, IS_CHECKBOX VARCHAR)");

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        titleText = title.getText().toString();
        noteText = note.getText().toString();
        sqLiteDatabase.execSQL("INSERT INTO CARDS(TITLE, NOTE) VALUES('"+titleText+"','"+noteText+"')");
    }
}
