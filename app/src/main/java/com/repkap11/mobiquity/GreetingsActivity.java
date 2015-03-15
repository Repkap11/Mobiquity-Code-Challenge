package com.repkap11.mobiquity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class GreetingsActivity extends ActionBarActivity implements ImageGridFragment.FragmentInteractionListener, LoginFragment.FragmentInteractionListener {
    private static final String TAG = GreetingsActivity.class.getSimpleName();
    public DropboxAPI<AndroidAuthSession> mDBApi;
    private String mDBAccessToken;

    //Used for DB access key caching
    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        String appKey = getResources().getString(R.string.db_app_key);
        String appsecret = getResources().getString(R.string.db_app_secret);
        AppKeyPair appKeys = new AppKeyPair(appKey, appsecret);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        loadAuth(session);//load the saved session if avaliable
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        String fragTag = "tag";
        if (savedInstanceState == null){
            Log.e(TAG,"Recreating Fragments");
        //if(getFragmentManager().findFragmentByTag(fragTag) == null) {
            Fragment frag;
            if (session.isLinked()) {
                frag = new ImageGridFragment();
            } else {
                frag = new LoginFragment();
            }
            //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getFragmentManager().beginTransaction().replace(R.id.container, frag,frag.getClass().getSimpleName()).commit();
        } else{
            Log.e(TAG,"NOT Recreating Fragments");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_greetings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_logout) {
            Log.i(TAG,"Log out clicked");
            mDBApi.getSession().unlink();
            clearKeys();
            try {
                ImageLoader.getInstance().getMemoryCache().clear();
                ImageLoader.getInstance().getDiskCache().clear();
            } catch (IllegalStateException e) {
                //This is fine, just means ImageLoader hasn't been inited yet.
                //We don't want to init here and there is no way to check if
                //we have initted.
            }
            getFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment(),LoginFragment.class.getSimpleName()).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onImageGridFragmentInteraction(int position) {
        Log.i(TAG, "Fragment Interaction:" + position);
    }

    @Override
    public void onLoginFragmentInteraction(String event) {
        if (event.equals(LoginFragment.DROPBOX_LOGIN_CLICKED)) {
            Log.i(TAG, "Activity saw login");
            doDropboxLogin();
        } else {
            Log.i(TAG, "Fragment Interaction:" + event);
        }
    }

    private void doDropboxLogin() {
        mDBApi.getSession().startOAuth2Authentication(this);
        //control is sent to dropbox api, it is returned in onResume
    }

    protected void onResume() {
        super.onResume();
        if (!mDBApi.getSession().isLinked()) {
            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                    // Required to complete auth, sets the access token on the session
                    mDBApi.getSession().finishAuthentication();
                    mDBAccessToken = mDBApi.getSession().getOAuth2AccessToken();
                    storeAuth(mDBApi.getSession());
                    Log.i(TAG, "DB Auth successful:" + mDBAccessToken);
                    getFragmentManager().beginTransaction().replace(R.id.container, new ImageGridFragment()).commit();

                } catch (IllegalStateException e) {
                    Log.i(TAG, "DB Auth Error:", e);
                }
            }
        }
    }
    /**
     * Loads the keys from SharedPreferences if avaliable
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
            Log.i(TAG, "DB Account set from saved memory");
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Stores the keys in SharedPreferences
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            Log.i(TAG, "DB Account saved 2");
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            Log.i(TAG, "DB Account saved 1");
            return;
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
}
