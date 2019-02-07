package com.maseno.franklinesable.lifereminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Frankline Sable on 7/23/2016. From Maseno University in Kenya. LifeReminderEpic
 */
public class SplashActivity extends Activity {

    private TextView textView;
    private ImageView imgView;
    private SoundPool sp;
    private int splashSound = 0;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = getApplicationContext();

        TextView versionIndicator = (TextView) findViewById(R.id.version_indicator);
        textView = (TextView) findViewById(R.id.splashTitle);
        imgView = (ImageView) findViewById(R.id.splashIcon);


        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Luckiest Guy (Open Type).ttf");

        final Animation imageAnim = AnimationUtils.loadAnimation(context, R.anim.splash_bell_anim);
        Animation titleAnim = AnimationUtils.loadAnimation(context, R.anim.fadein);

        String versionName = getString(R.string.version_label) + BuildConfig.VERSION_NAME;

        versionIndicator.setText(versionName);
        textView.setTypeface(font);
        imgView.startAnimation(imageAnim);
        textView.startAnimation(titleAnim);

        titleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                splashSync sync = new splashSync();
                sync.execute();

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                imgView.clearAnimation();
                imgView.clearAnimation();
                sp.release();

                startActivity(new Intent(context, reminder_lists.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        sp.release();
        textView.clearAnimation();
        imgView.clearAnimation();
        finish();
    }


    private class splashSync extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                AudioAttributes splashAttribute = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME).build();
                sp = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(splashAttribute).build();
            } else {

                sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
            }

            splashSound = sp.load(context, R.raw.ethereal_accents, 1);
            return splashSound;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            if (splashSound != 0)
                sp.play(splashSound, 0.9f, 0.9f, 1, 0, 1);
        }
    }
}
