package com.maseno.franklinesable.lifereminder;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Frankline Sable on 7/20/2016. Fragment
 */
public class preference_fragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String KEY_ANIM_LIST = "pref_choose_key";
    private static final String KEY_PICK_NOTIFICATION_SOUND = "notification_reminder_complete_ringtone";
    private static final String KEY_PREF_ABOUT = "pref_key_about";
    private static final String KEY_SNOOZE = "snooze_frequency";
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Preference aboutPreference = findPreference(KEY_PREF_ABOUT);
        aboutPreference.setOnPreferenceClickListener(this);

        setUpAnimPref();
        setUpSnoozePref();
        setUpRingtonePreference();
        setUpDisplayName();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setUpAnimPref();
        setUpSnoozePref();
        setUpRingtonePreference();
        setUpDisplayName();
    }

    public void setUpRingtonePreference() {
        Preference ringtonePref = findPreference(KEY_PICK_NOTIFICATION_SOUND);
        String ringValue = sharedPreferences.getString(KEY_PICK_NOTIFICATION_SOUND, "");

        if (TextUtils.isEmpty(ringValue)) {
            // Empty values correspond to 'silent' (no ringtone).
            ringtonePref.setSummary("Silent");

        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(
                    ringtonePref.getContext(), Uri.parse(ringValue));

            if (ringtone == null) {
                // Clear the summary if there was a lookup error.
                ringtonePref.setSummary(null);
            } else {
                // Set the summary to reflect the new ringtone display
                // name.
                String name = ringtone.getTitle(ringtonePref.getContext());
                ringtonePref.setSummary(name);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void setUpAnimPref() {
        int i = 0;
        BitmapDrawable frame1 = (BitmapDrawable) getResources().getDrawable(R.drawable.spinner_frame_1_48px);
        BitmapDrawable frame2 = (BitmapDrawable) getResources().getDrawable(R.drawable.spinner_frame_2_48px);
        BitmapDrawable frame3 = (BitmapDrawable) getResources().getDrawable(R.drawable.spinner_frame_3_48px);
        BitmapDrawable frame4 = (BitmapDrawable) getResources().getDrawable(R.drawable.spinner_frame_4_48px);
        BitmapDrawable frame5 = (BitmapDrawable) getResources().getDrawable(R.drawable.spinner_frame_5_48px);
        BitmapDrawable frame6 = (BitmapDrawable) getResources().getDrawable(R.drawable.spinner_frame_6_48px);
        BitmapDrawable frame7 = (BitmapDrawable) getResources().getDrawable(R.drawable.spinner_frame_7_48px);
        BitmapDrawable frame8 = (BitmapDrawable) getResources().getDrawable(R.drawable.spinner_frame_8_48px);

        AnimationDrawable animationDrawable = new AnimationDrawable();
        int duration = 100;
        if (frame1 != null && frame2 != null && frame3 != null && frame4 != null && frame5 != null && frame6 != null && frame7 != null && frame8 != null) {
            animationDrawable.addFrame(frame1, duration);
            animationDrawable.addFrame(frame2, duration);
            animationDrawable.addFrame(frame3, duration);
            animationDrawable.addFrame(frame4, duration);
            animationDrawable.addFrame(frame5, duration);
            animationDrawable.addFrame(frame6, duration);
            animationDrawable.addFrame(frame7, duration);
            animationDrawable.addFrame(frame8, duration);
        }

        Preference animPref = findPreference(KEY_ANIM_LIST);
        animPref.setIcon(animationDrawable);
        animationDrawable.setOneShot(true);
        animationDrawable.start();

        String animValue = sharedPreferences.getString(KEY_ANIM_LIST, "43");

        ListPreference animPreference_List = (ListPreference) animPref;
        int index2 = animPreference_List.findIndexOfValue(animValue);
        animPref.setSummary(
                index2 >= 0
                        ? animPreference_List.getEntries()[index2]
                        : null);

        setUpRingtonePreference();
    }

    public void setUpSnoozePref() {

        Preference snoozePref = findPreference(KEY_SNOOZE);
        String snoozeValue = sharedPreferences.getString(KEY_SNOOZE, "5");

        ListPreference snoozePreference_List = (ListPreference) snoozePref;
        int index = snoozePreference_List.findIndexOfValue(snoozeValue);
        snoozePref.setSummary(
                index >= 0
                        ? snoozePreference_List.getEntries()[index]
                        : null);
    }

    public void setUpDisplayName() {

        EditTextPreference namePreference = (EditTextPreference) findPreference("display_name");
        String nickname = null;

        if (namePreference.getText() == null) {

            AccountManager manager = AccountManager.get(getActivity());
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            try {
                Account[] accounts = manager.getAccountsByType("com.google");
                List<String> username = new LinkedList<>();

                for (Account account : accounts) {
                    username.add(account.name);
                }
                String email = username.get(0);
                String[] parts = email.split("@");
                nickname = parts[0];
            } catch (IndexOutOfBoundsException e) {

                nickname = "Maseno Comrade";
            } finally {
                namePreference.setText(nickname);
            }


        } else {
            nickname = sharedPreferences.getString("display_name", "");
        }
        namePreference.setSummary(nickname);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MaterialDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.custom_dialog_about, null);
        TextView messageAbout=(TextView)layout.findViewById(R.id.textView);
        messageAbout.setText(getString(R.string.version_label) + BuildConfig.VERSION_NAME+"\nDeveloped by Frankline Sable");
        builder.setCancelable(true).setView(layout).setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("More", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which
            ) {
                dialog.cancel();
            }
        }).setTitle("Life Reminder").setIcon(R.drawable.about_48px);
        builder.create().show();
        return true;
    }


}

