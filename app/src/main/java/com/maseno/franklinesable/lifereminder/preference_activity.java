package com.maseno.franklinesable.lifereminder;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

/**
 * For app settings only!
 * Created by Frankline Sable on 7/20/2016.
 */
public class preference_activity extends AppCompatActivity/*preference activity*/ implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_FULLSCREEN = "pref_key_fullscreen";
    public static final String KEY_SCREEN_ON = "pref_key_screen";
    public static final String KEY_GUARD_LOCK = "pref_key_keyguard";
    public static final String KEY_SECURE_BAR = "pref_key_secure";
    public static final String KEY_ANIM = "pref_enable_anim_key";
    public static final String KEY_ANIM_LIST = "pref_choose_key";
    public static final String KEY_ENABLE_VIBRATIONS = "notification_reminder_complete_vibrate";
    public static final String KEY_THEME = "customize_app_theme";

    private SharedPreferences sharedPreferences;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setupActionBar();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new preference_fragment()).commit();

        applyDefaultSettings();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        preferences_values();

        if (key.equals(KEY_ENABLE_VIBRATIONS))
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {

            } else {
                vibrator.vibrate(50);//ms
            }

        else if (key.equals(KEY_THEME)) {
            //  finish();
            Intent i=new Intent(this, preference_activity.class);
            setResult(RESULT_OK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        preferences_values();
    }

    public void preferences_values() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean(KEY_FULLSCREEN, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (sharedPref.getBoolean(KEY_SECURE_BAR, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        if (sharedPref.getBoolean(KEY_GUARD_LOCK, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
        if (sharedPref.getBoolean(KEY_SCREEN_ON, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (sharedPref.getBoolean(KEY_ANIM, false)) {

            switch (sharedPref.getString(preference_activity.KEY_ANIM_LIST, "43")) {
                case "32":
                    overridePendingTransition(R.anim.window_anim_fade_in, R.anim.window_anim_fade_in);

                    break;
                case "43":
                    overridePendingTransition(R.anim.window_anim_rotate, R.anim.window_anim_rotate);
                    break;
                case "69":
                    overridePendingTransition(R.anim.window_anim_blank, R.anim.window_anim_blank);
                    break;
            }
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
        }
    }

    public void applyDefaultSettings() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        applySettingsFragment fragment = new applySettingsFragment();
        fragmentTransaction.add(fragment, "settings_fragment");
        fragmentTransaction.commit();
    }
    private void setTheme(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            int appTheme =Integer.parseInt(sharedPreferences.getString(preference_activity.KEY_THEME, ""));
            if(appTheme==0){
                setTheme(R.style.AppTheme_NoActionBarSettings);
            }
            else{
                setTheme(R.style.DarkThemeSettings);
            }
        }
        catch (Exception e){}

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
            startActivity(new Intent(preference_activity.this, reminder_lists.class));
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(preference_activity.this, reminder_lists.class));
    }


}



