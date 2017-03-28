package com.example.soham.remembrall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DisplayNotes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LinearLayout navBar;
    private ImageView navProfilePicture;
    private TextView navUser;
    private TextView navEmail;
    private NavigationView navigationView;
    private GoogleApiClient googleApiClient;
    private boolean doubleTapBackToExit = false;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;
    private String databaseName;

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private int cardsNumber;
    List<NoteHolder> noteHolderList = new ArrayList<NoteHolder>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope(Scopes.PLUS_LOGIN)).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).addApi(Plus.API).build();
        /*Check if the user is signed in already. If he is, continue with fetching card data from server. Otherwise, redirect to login*/

        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(!optionalPendingResult.isDone())
        {
            Intent goToLoginActivity = new Intent(this, MainActivity.class);
            startActivity(goToLoginActivity);
            finish();
        }
        else {
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            View view = navigationView.getHeaderView(0);
            navUser = (TextView) view.findViewById(R.id.nav_header_user);
            navEmail = (TextView) view.findViewById(R.id.nav_header_email);
            navProfilePicture = (ImageView) view.findViewById(R.id.nav_header_profile_picture);
            navBar = (LinearLayout) view.findViewById(R.id.nav_bar_background);
            recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

            //Set CAB listener
             final ActionMode.Callback callback = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater menuInflater = mode.getMenuInflater();
                    menuInflater.inflate(R.menu.display_notes_cab, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            };

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.OnItemClickListener(){
                @Override
                public void onItemClick(View view, int position){
                    Intent intent = new Intent(DisplayNotes.this, CreateNote.class);
                    NoteHolder noteHolder = noteHolderList.get(position);

                    intent.putExtra("cardTitle",noteHolder.get_title());
                    intent.putExtra("cardText", noteHolder.get_note());
                    intent.putExtra("databaseName", databaseName);
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }


            }));


            GoogleSignInResult googleSignInResult = optionalPendingResult.get();
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            String personName = googleSignInAccount.getDisplayName();
            databaseName = "Cards-"+personName;
            String photoURL = googleSignInAccount.getPhotoUrl().toString();
            String personEmail = googleSignInAccount.getEmail();
            String coverPhotoURL = "";
            try {
                FileInputStream fileInputStream = getApplicationContext().openFileInput("coverPhoto");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                coverPhotoURL = bufferedReader.readLine();
                fileInputStream.close();
            }
            catch (Exception exception) {
                Log.e(TAG, "File Not Found!!!" + exception.toString());
            }
            navUser.setText(personName);
            navEmail.setText(personEmail);
            if(photoURL != null || !photoURL.equals("")) {
                Glide.with(getApplicationContext()).load(photoURL).asBitmap().into(new BitmapImageViewTarget(navProfilePicture) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        roundedBitmapDrawable.setCircular(true);
                        navProfilePicture.setImageDrawable(roundedBitmapDrawable);
                    }

                });
            }
            if(coverPhotoURL != null || !coverPhotoURL.equals("")) {
                Glide.with(getApplicationContext()).load(coverPhotoURL).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(resource);
                        navBar.setBackground(drawable);

                    }
                });
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToCreateNote = new Intent(getApplicationContext(), CreateNote.class);
                goToCreateNote.putExtra("databaseName",databaseName);
                startActivity(goToCreateNote);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    public void displayCards() {
        sqLiteDatabase = openOrCreateDatabase(databaseName, getApplicationContext().MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CARDS(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE VARCHAR, NOTE VARCHAR, IS_CHECKBOX VARCHAR)");
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM CARDS", null);
        int countCards = cursor.getCount();
        noteHolderList.clear();
        if (cursor.moveToFirst()) {
            do {
                int _id = cursor.getInt(0);
                String _title = cursor.getString(1);
                String _note = cursor.getString(2);
                NoteHolder noteHolder = new NoteHolder(_id, _title, _note);
                noteHolderList.add(noteHolder);
            }
            while (cursor.moveToNext());
        }

        cardsNumber = countCards;
        cardAdapter = new CardAdapter(cardsNumber, noteHolderList);
        cardAdapter.notifyDataSetChanged();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d(TAG,"Connection failed because of"+connectionResult);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        displayCards();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleTapBackToExit) {
                super.onBackPressed();
            }
            doubleTapBackToExit = true;
            Toast.makeText(this, R.string.click_back_to_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTapBackToExit = false;
                }
            }, 2000);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if(id == R.id.nav_sign_out){
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
