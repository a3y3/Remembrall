package com.example.soham.remembrall;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNote extends AppCompatActivity{
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;
    private String titleText, noteText, isCheckBox;
    private EditText title, note;
    private String databaseName;
    private String cardNoteFromIntent;
    private String cardTitleFromIntent;
    private boolean updateNote = false;
    private boolean dontSaveNote = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent getIntentValues = getIntent();
        databaseName = getIntentValues.getStringExtra("databaseName");
        cardNoteFromIntent = getIntentValues.getStringExtra("cardText");
        cardTitleFromIntent = getIntentValues.getStringExtra("cardTitle");

        title = (EditText) findViewById(R.id.add_title);
        title.requestFocus();
        note = (EditText) findViewById(R.id.note_textfield);

        //All things database
        sqLiteDatabase = openOrCreateDatabase(databaseName, this.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CARDS(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE VARCHAR, NOTE VARCHAR, IS_CHECKBOX VARCHAR)");

        if (cardTitleFromIntent != null) {
            updateNote = true;
            title.setText(cardTitleFromIntent);
            note.setText(cardNoteFromIntent);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        titleText = title.getText().toString();
        noteText = note.getText().toString();
        if (noteText.contains("'")) {
            noteText = noteText.replaceAll("'", "''");
        }
        if (updateNote) {
            sqLiteDatabase.execSQL("UPDATE CARDS SET TITLE='" + titleText + "' WHERE NOTE='" + cardNoteFromIntent +"'");
            sqLiteDatabase.execSQL("UPDATE CARDS SET NOTE='" +noteText +"' WHERE NOTE='" + cardNoteFromIntent +"'");
        } else {
            if(!dontSaveNote)
                sqLiteDatabase.execSQL("INSERT INTO CARDS(TITLE, NOTE) VALUES('" + titleText + "','" + noteText + "')");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.add_note_reminder:
                Toast.makeText(this, "Added Reminder successfully!", Toast.LENGTH_SHORT).show();
                break;

            case android.R.id.home:                     
                onBackPressed();
                break;

            case R.id.do_not_save:
                dontSaveNote = true;
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_create_notes, menu);
        return true;
    }
}
