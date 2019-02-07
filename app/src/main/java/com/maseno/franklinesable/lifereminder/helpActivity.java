package com.maseno.franklinesable.lifereminder;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class helpActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private int multipleTap = 5;
    private TextToSpeech tts;
    private boolean ttsToggle = true, ttsSupported = true;
    private ImageButton toggleTTSButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        tts = new TextToSpeech(this, this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpActionBar();

        final ListView helpList = (ListView) findViewById(R.id.helpListView);
        final String listArray[] = {"Like us on Facebook", "Follow us on Google+", "Contact us"};
        toggleTTSButton = (ImageButton) findViewById(R.id.toggle_tts);

        ArrayAdapter<String> arrayAdapter = new arrayAdapterForHelp(this, listArray);
        Typeface apple_Font = Typeface.createFromAsset(getAssets(), getString(R.string.appleFont));
        helpList.setAdapter(arrayAdapter);

        final TextView helpImageButton = (TextView) findViewById(R.id.imageButton);
        final String versionInfo = getString(R.string.version_label) + BuildConfig.VERSION_NAME;
        helpImageButton.setText(versionInfo);
        helpImageButton.setTypeface(apple_Font);
        helpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Toast toast = Toast.makeText(helpActivity.this, "" + --multipleTap, Toast.LENGTH_SHORT);
                toast.show();

                if (multipleTap == 3)
                    helpImageButton.append("{List_Authors}, approx 3 Guys");
                if (multipleTap < 1) {
                    helpImageButton.setEnabled(false);
                    helpImageButton.setText(getString(R.string.help_anim_display));
                    helpImageButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                    Animation anim = AnimationUtils.loadAnimation(helpActivity.this, R.anim.help_text_animate);
                    helpImageButton.startAnimation(anim);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                            helpImageButton.setTextColor(Color.rgb(80, 80, 80));

                            if (ttsSupported) {
                                toggleVisibility(true);
                            }
                            if (ttsToggle) {
                                tts.stop();

                            } else {
                                speakOut();
                            }
                            toggleTTSButton.setAlpha(1f);
                            toggleTTSButton.animate().alpha(0f).setDuration(7000).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    if (ttsSupported) {
                                        toggleVisibility(false);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            helpImageButton.setEnabled(true);
                            helpImageButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.help_headericon, 0, 0);
                            helpImageButton.setText(versionInfo);
                            toggleTTSButton.setVisibility(View.GONE);
                            tts.stop();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    multipleTap = 5;
                }
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);
            }
        });

        helpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {

                    case 0:

                        Intent fbIntent = new Intent(Intent.ACTION_VIEW);
                        fbIntent.setData(Uri.parse("http://facebook.com/lifereminder"));
                        startActivity(fbIntent);

                        break;
                    case 1:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"lifereminder@gmail.com"});
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{"franklineodero@gmail.com"});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Life reminder help");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "( We'll give a Quick feedback) Please state your problem: "+phoneInfo());
                        startActivity(Intent.createChooser(emailIntent, "Send help email using.."));

                        break;
                    case 2:
                        try {
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            sendIntent.setData(Uri.parse("sms:" + +716843583));
                            sendIntent.putExtra("sms_body", "( We'll give a Quick feedback) Please state your problem: "+phoneInfo());
                            startActivity(sendIntent);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "message client not installed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }

    public void setUpActionBar() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Do Contact Us:");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);
            tts.setPitch(0.9f);
            tts.setSpeechRate(1.2f);

            if (result == TextToSpeech.LANG_MISSING_DATA) {
                ttsSupported = false;
                toggleVisibility(false);
            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                toggleTTSButton.setEnabled(false);
            } else {

            }
        } else {
            ttsSupported = false;
            toggleVisibility(false);
        }
    }

    private void speakOut() {

        tts.speak(getString(R.string.help_anim_display), TextToSpeech.QUEUE_FLUSH, null);
    }

    public void toggleTTS(View v) {

        if (!ttsToggle) {

            toggleTTSButton.setImageResource(R.drawable.ic_volume_up_24dp);
            ttsToggle = true;
            tts.stop();
        } else {
            toggleTTSButton.setImageResource(R.drawable.ic_volume_off_24dp);
            ttsToggle = false;
            speakOut();
        }
        toggleTTSButton.clearAnimation();
        toggleTTSButton.setAlpha(1f);
        toggleTTSButton.animate().alpha(0f).setDuration(3000).withEndAction(new Runnable() {
            @Override
            public void run() {
                toggleTTSButton.setVisibility(View.GONE);
            }
        });
    }

    public void toggleVisibility(Boolean isVisible) {

        if (isVisible) {
            toggleTTSButton.setVisibility(View.VISIBLE);
        } else {
            toggleTTSButton.setVisibility(View.GONE);
        }
    }
    public String phoneInfo(){

        return "{Please don't edit this fields:"
                +" \n Device "+ Build.DEVICE
                +" \n Manufacturer "+Build.MANUFACTURER
                +" \n Android Version(OS) "+Build.VERSION.RELEASE
                +" \n Model "+Build.MODEL
                +" \n Brand "+Build.BRAND
                +" \n Fingerprint "+Build.FINGERPRINT+
                "}";
    }
}
