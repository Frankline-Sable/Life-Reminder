package com.maseno.franklinesable.lifereminder;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class AboutApp extends AppCompatActivity {

    private boolean view_licence = true;
    private String licence_read = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        applyDefaultSettings();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpActionBar();
        final TextView aboutView = (TextView) findViewById(R.id.about_TextView);

        final boolean[] browsingAbout = {false};
        Typeface apple_Font = Typeface.createFromAsset(getAssets(), getString(R.string.appleFont));
        aboutView.setTypeface(apple_Font);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (view_licence) {

                    fab.setImageResource(R.drawable.about_48px);
                    if (!browsingAbout[0]) {
                        InputStream readLicence = getResources().openRawResource(R.raw.licence);
                        try {
                            byte[] licence_Input = new byte[readLicence.available()];
                            while (readLicence.read(licence_Input) != -1) {
                                //ignore loop
                            }
                            licence_read = new String(licence_Input);
                        } catch (IOException e) {

                            licence_read = "Error reading the licence";
                        } finally {

                            if (readLicence != null) {
                                try {
                                    readLicence.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        browsingAbout[0] = true;
                    }
                    aboutView.setText(licence_read);
                    view_licence = false;
                } else {
                    view_licence = true;
                    fab.setImageResource(R.drawable.ic_licence_24dp);
                    aboutView.setText(getResources().getString(R.string.about_text));

                }

            }
        });
    }

    public void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void applyDefaultSettings() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        applySettingsFragment fragment = new applySettingsFragment();
        fragmentTransaction.add(fragment, "settings_fragment");
        fragmentTransaction.commit();
    }
}
