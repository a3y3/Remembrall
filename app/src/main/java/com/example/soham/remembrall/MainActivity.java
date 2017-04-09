package com.example.soham.remembrall;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.FileOutputStream;

    public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = MainActivity.class.getSimpleName();
        private static final int RC_SIGN_IN = 007;
        private GoogleApiClient googleApiClient;

        private SignInButton signInButton;
        //private Button signOutButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //Starting Sign-in <code></code>
            signInButton = (SignInButton)findViewById(R.id.signInButton);

            signInButton.setOnClickListener(this);

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope(Scopes.PLUS_LOGIN)).requestEmail().build();
            googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).addApi(Plus.API).build();


            signInButton.setSize(SignInButton.SIZE_WIDE);
        }

        @Override
        public void onClick(View view)
        {
            switch(view.getId())
            {
                case R.id.signInButton:
                    signIn();
                    break;
            }
        }

        private void signIn()
        {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent,007);
        }

      /*  private void signOut()
        {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status)
                {

                }
            });
        }*/

        private void handleSignInResult(GoogleSignInResult googleSignInResult)
        {
            String coverPhotoURL="";
            Log.d(TAG,"handleSignInResult"+googleSignInResult.isSuccess());
            if(googleApiClient.hasConnectedApi(Plus.API))
            {
                Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
                if(person!=null) {
                    try {
                        Person.Cover.CoverPhoto coverPhoto = person.getCover().getCoverPhoto();
                        coverPhotoURL = coverPhoto.getUrl();
                    }
                    catch (Exception e){
                        Log.e("404","User has no cover photo");
                    }
                }
            }
            if(googleSignInResult.isSuccess())
            {
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

                String personEmail = googleSignInAccount.getEmail();
                Intent goToDisplayNotes = new Intent(this,DisplayNotes.class);
                String fileName = "coverPhoto";
                FileOutputStream fileOutputStream;
                try
                {
                    fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                    fileOutputStream.write(coverPhotoURL.getBytes());
                    fileOutputStream.close();
                    fileOutputStream.flush();
                }
                catch(Exception exception)
                {
                    Log.e(TAG, "File Exception occured"+exception.toString());
                }
                Toast.makeText(this, "Welcome. You're signed in as "+personEmail, Toast.LENGTH_SHORT).show();
                startActivity(goToDisplayNotes);
                finish();
            }
            else
            {
                //TODO handle if not signed in here. IMPORTANT.
            }
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


}
