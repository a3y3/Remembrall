package com.example.soham.remembrall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class CreateNote extends AppCompatActivity {
    private EditText note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        note = (EditText)findViewById(R.id.note_textfield);
        note.getBackground().clearColorFilter();
        note.clearComposingText();
        note.requestFocus();

    }
}
