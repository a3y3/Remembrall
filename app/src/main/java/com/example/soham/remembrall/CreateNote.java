package com.example.soham.remembrall;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNote extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;
    private String titleText, noteText, isCheckBox;
    private EditText title, note;
    private String databaseName;
    private String cardNoteFromIntent;
    private String cardTitleFromIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
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
            title.setText(cardTitleFromIntent);
            note.setText(cardNoteFromIntent);
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        titleText = title.getText().toString();
        noteText = note.getText().toString();
        //noteText = noteText.replaceAll("'","\'");
        if(noteText.contains("'")){
            noteText = noteText.replaceAll("'","''");
        }
        sqLiteDatabase.execSQL("INSERT INTO CARDS(TITLE, NOTE) VALUES('"+titleText+"','"+noteText+"')");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.home)
        {
            Toast.makeText(this, "Up pressed", Toast.LENGTH_SHORT).show(); //Not working, Please someone // FIXME: 23-Mar-17
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
