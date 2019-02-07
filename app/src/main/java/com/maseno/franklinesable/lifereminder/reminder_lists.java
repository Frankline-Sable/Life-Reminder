package com.maseno.franklinesable.lifereminder;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class reminder_lists extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private app_database dbHandler;
    private TabLayout tabLayout;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_nav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Everyone's favourite reminder manager");

        ViewPager mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        setUpViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.mTabs);
        tabLayout.setupWithViewPager(mViewPager);
        setTabIcons();
        applyDefaultSettings();

        context = this.getApplicationContext();
        dbHandler = new app_database(context);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context, reminder_edit.class));
            }
        });

        Animation fabAnim= AnimationUtils.loadAnimation(context, R.anim.scale);
        fab.startAnimation(fabAnim);


    }
    public void emailClick(View v){

        Snackbar.make(v, "Thanks to Maseno University, The Fountain of Excellence!", Snackbar.LENGTH_LONG)
                .setAction("Goto", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.maseno.ac.ke/"));
                        startActivity(intent);
                    }
                }).setActionTextColor(getResources().getColor(android.R.color.holo_blue_light)).show();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminder_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_settings:
                startActivity(new Intent(context, preference_activity.class));
                break;

            case R.id.help_action:
                startActivity(new Intent(context, helpActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (exit) {
                super.onBackPressed();
            } else {
                exit = true;
                Toast.makeText(context, "click again to exit", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_favourite) {
            startActivity(new Intent(context, favourite_reminders.class));
        } else if (id == R.id.nav_clear) {

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(context, AboutApp.class));

        } else if (id == R.id.nav_manage) {

            Intent intentPref = new Intent(context, preference_activity.class);
            startActivity(intentPref);

        } else if (id == R.id.nav_share) {

            //Get current ApplicationInfo to find .apk path
            ApplicationInfo app = context.getApplicationInfo();
            String filePath = app.sourceDir;

            Intent intent = new Intent(Intent.ACTION_SEND);

            //MIME of .apk is "application/vnd.android.package-archive".
            //but Bluetooth does not accept this. Let's use "*/*" instead.

            intent.setType("*/*");

            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            startActivity(Intent.createChooser(intent, "Share Life Reminder Apk Via"));

        } else if (id == R.id.nav_donate) {

            BitmapDrawable frame[] = new BitmapDrawable[19];

            frame[0] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0001);
            frame[1] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0002);
            frame[2] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0003);
            frame[3] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0004);
            frame[4] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0005);
            frame[5] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0006);
            frame[6] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0007);
            frame[7] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0008);
            frame[8] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0009);
            frame[9] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0010);
            frame[10] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0011);
            frame[11] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0012);
            frame[12] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0013);
            frame[13] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0014);
            frame[14] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0015);
            frame[15] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0016);
            frame[16] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0017);
            frame[17] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0018);
            frame[18] = (BitmapDrawable) getResources().getDrawable(R.drawable.send_prog_0019);

            AnimationDrawable animationDrawable = new AnimationDrawable();
            int duration = 40;

            for (BitmapDrawable aFrame : frame) {
                animationDrawable.addFrame(aFrame, duration);
            }
            animationDrawable.setOneShot(false);

            ProgressDialog progressDialog = new ProgressDialog(reminder_lists.this);
            progressDialog.setIndeterminateDrawable(animationDrawable);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Still under development...(85% complete)");
            progressDialog.show();
            animationDrawable.start();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void applyDefaultSettings() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        applySettingsFragment fragment = new applySettingsFragment();
        fragmentTransaction.add(fragment, "settings_fragment");
        fragmentTransaction.commit();
    }

    private void setUpViewPager(ViewPager mViewPager) {

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new FragmentA(), "All Reminders");
        pagerAdapter.addFragment(new FragmentB(), "Regular Reminders");
        mViewPager.setAdapter(pagerAdapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> mFragmentList = new ArrayList<>();
        List<String> mFragmentNames = new ArrayList<>();

        public ViewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentNames.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentNames.add(title);

        }
    }

    public void setTabIcons() {

        tabLayout.getTabAt(0).setIcon(R.drawable.thumbnails_48px);
        tabLayout.getTabAt(1).setIcon(R.drawable.repeat_48px);
    }

    private void setTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!sharedPreferences.getAll().isEmpty()) {
            int appTheme = Integer.parseInt(sharedPreferences.getString(preference_activity.KEY_THEME, ""));
            if (appTheme == 1)
                setTheme(R.style.DarkTheme);
        }
    }
}


