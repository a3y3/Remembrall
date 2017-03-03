    package com.example.soham.remembrall;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.AutoScrollHelper;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.vision.text.Text;

import java.io.InputStream;
import java.net.Authenticator;

    public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = MainActivity.class.getSimpleName();
        private static final int RC_SIGN_IN = 007;
        private GoogleApiClient googleApiClient;
        private ProgressDialog progressDialog;

        private SignInButton signInButton;
        private Button signOutButton;
        private LinearLayout llProfile;
        private ImageView profilePicture;
        private TextView name;
        private TextView email;
        private LinearLayout navBar;
        private ImageView navProfilePicture;
        private TextView navUser;
        private TextView navEmail;
        private NavigationView navigationView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //All things nav

            navigationView = (NavigationView)findViewById(R.id.nav_view);
            View view = navigationView.getHeaderView(0);
            navUser = (TextView)view.findViewById(R.id.nav_header_user);
            navEmail = (TextView)view.findViewById(R.id.nav_header_email);
            navProfilePicture = (ImageView)view.findViewById(R.id.nav_header_profile_picture);
            navBar = (LinearLayout)view.findViewById(R.id.nav_bar_background);



            //Starting Sign-in <code></code>
            signInButton = (SignInButton)findViewById(R.id.signInButton);
            signOutButton = (Button)findViewById(R.id.signOutButton);
            llProfile = (LinearLayout)findViewById(R.id.llProfile);
            profilePicture = (ImageView)findViewById(R.id.profilePicture) ;
            name = (TextView)findViewById(R.id.name);
            email = (TextView)findViewById(R.id.email);

            signInButton.setOnClickListener(this);
            signInButton.performClick();
            signOutButton.setOnClickListener(this);

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope(Scopes.PLUS_LOGIN)).requestEmail().build();
            googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).addApi(Plus.API).build();

            signInButton.setSize(SignInButton.SIZE_STANDARD);



            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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

        @Override
        public void onClick(View view)
        {
            switch(view.getId())
            {
                case R.id.signInButton:
                    signIn();
                    break;
                case R.id.signOutButton:
                    signOut();
                    break;
            }
        }

        private void signIn()
        {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent,007);
        }

        private void signOut()
        {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status)
                {
                    updateUI(false);
                }
            });
        }

        private void updateUI(boolean isSignedIn)
        {
            if(isSignedIn)
            {
                signInButton.setVisibility(View.GONE);
                signOutButton.setVisibility(View.VISIBLE);
                llProfile.setVisibility(View.VISIBLE);
            }
            else
            {
                signInButton.setVisibility(View.VISIBLE);
                signOutButton.setVisibility(View.GONE);
                llProfile.setVisibility(View.GONE);
            }
        }

        private void handleSignInResult(GoogleSignInResult googleSignInResult)
        {
            String coverPhotoURL="";
            Log.d(TAG,"handleSignInResult"+googleSignInResult.isSuccess());
            if(googleApiClient.hasConnectedApi(Plus.API))
            {
                Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
                if(person!=null)
                {
                    Person.Cover.CoverPhoto coverPhoto = person.getCover().getCoverPhoto();
                    coverPhotoURL = coverPhoto.getUrl();
                }
            }
            if(googleSignInResult.isSuccess())
            {
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                String personName = googleSignInAccount.getDisplayName();
                String photoURL = googleSignInAccount.getPhotoUrl().toString();

                String personEmail = googleSignInAccount.getEmail();
                name.setText(personName);
                email.setText(personEmail);
                Glide.with(getApplicationContext()).load(photoURL).placeholder(R.mipmap.ic_launcher).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(profilePicture);

                navUser.setText(personName);
                navEmail.setText(personEmail);
                //Glide.with(getApplicationContext()).load(photoURL).placeholder(R.mipmap.ic_launcher).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(navProfilePicture);
                Glide.with(getApplicationContext()).load(photoURL).asBitmap().into(new BitmapImageViewTarget(navProfilePicture){
                    @Override
                    protected void setResource(Bitmap resource)
                    {
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(),resource);
                        roundedBitmapDrawable.setCircular(true);
                        navProfilePicture.setImageDrawable(roundedBitmapDrawable);
                    }

                });
                Glide.with(getApplicationContext()).load(coverPhotoURL).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(resource);
                        navBar.setBackground(drawable);

                    }
                });


                updateUI(true);

            }
            else
            {
                updateUI(false);
            }
        }

        private Drawable loadImageFromWeb(String coverURL)
        {
            try
            {
                InputStream inputStream = (InputStream) new URL(coverURL).getContent();
                Drawable drawable = Drawable.createFromStream(inputStream,"src");
                return drawable;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == RC_SIGN_IN)
            {
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(googleSignInResult);
            }
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
        {
            Log.d(TAG,"onConnectionFailed:"+connectionResult);
        }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
