package com.example.soham.remembrall;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class CreateNote extends AppCompatActivity{
    private SQLiteDatabase sqLiteDatabase;
    private String titleText, noteText, isCheckBox;
    private EditText title, note;
    private String databaseName;
    private String cardNoteFromIntent;
    private String cardTitleFromIntent;
    private boolean updateNote = false;
    private boolean dontSaveNote = false;
    boolean doNotFocus;


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
        doNotFocus = getIntentValues.getBooleanExtra("doNotFocus", false);
        Log.d("110",doNotFocus+"");

        title = (EditText) findViewById(R.id.add_title);
        if(!doNotFocus){
            title.requestFocus();
        }                                                   //TODO this does not work as line android:windowSoftInputMode="stateVisible" in AndroidManifest still requests focus
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
                Calendar calendar = Calendar.getInstance();
                final Dialog dateDialog = new Dialog(CreateNote.this);
                dateDialog.setContentView(R.layout.custom_picker);
                dateDialog.setTitle("Pick the date");
                final Dialog timeDialog = new Dialog(CreateNote.this);
                timeDialog.setContentView(R.layout.time_picker);
                timeDialog.setTitle("Pick the time");
                dateDialog.show();
                final DatePicker datePicker = (DatePicker)dateDialog.findViewById(R.id.date_picker);
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateDialog.cancel();
                        timeDialog.show();
                    }
                });
                TimePicker timePicker = (TimePicker)timeDialog.findViewById(R.id.time_picker);
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    }
                });


                break;

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.do_not_save:
                dontSaveNote = true;
                onBackPressed();
                break;

            case R.id.action_5:
                titleText = title.getText().toString();
                noteText = note.getText().toString();
                scheduleNotification(getNotification(titleText, noteText), 5000);
                break;

            case R.id.action_10:
                scheduleNotification(getNotification(titleText, noteText), 10000);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scheduleNotification(Notification notification, int delay){
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String title, String content){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(content);
        if(title.equals("")){
            builder.setContentTitle(content);
            builder.setContentText("");
        }
        builder.setSmallIcon(R.mipmap.remembrall);
        return builder.build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_create_notes, menu);
        return true;
    }
}
