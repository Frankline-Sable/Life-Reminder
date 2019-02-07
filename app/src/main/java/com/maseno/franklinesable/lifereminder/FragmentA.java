package com.maseno.franklinesable.lifereminder;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentA extends ListFragment {

    private String sortOrder = app_database.tb_Struct.KEY_TITLE + " ASC";
    private static final int ACTIVITY_CREATE = 69;
    private static final int ACTIVITY_EDIT = 32;
    private static boolean refreshArg = false;
    private Button view_reminderState;
    private app_database dbHandler;
    private TextView checkEmpty;
    private String searchQuery;
    private String searchArgs;
    private Context context;
    private Typeface roboto_Font;
    private Uri mCurrentPhotoPath;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
        dbHandler = new app_database(context);

        roboto_Font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto Light (Open Type).ttf");
        setHasOptionsMenu(true);

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                clearReminders();
                return false;
            }
        });

    }

    public void clearReminders() {

        if (getListView().getCount() < 1) {
            Toast.makeText(context, "No Reminders", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialDialogStyle).setCancelable(false).setTitle("Delete all reminders").setMessage("This will permanently delete all the reminders, are you sure you want to proceed?").setIcon(android.R.drawable.ic_menu_delete).setNegativeButton("No way", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dbHandler.open();
                            dbHandler.deleteDb(null);
                            new Handler(context.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {

                                    refresh();
                                    Toast.makeText(context, "Reminders Cleared", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }).start();

                }
            });
            builder.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fraga_mainlayout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        view_reminderState = (Button) getActivity().findViewById(R.id.rem_State);
        view_reminderState.setTypeface(roboto_Font);
        checkEmpty = (TextView) getActivity().findViewById(R.id.android_empty);
        registerForContextMenu(getListView());

        view_reminderState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (view_reminderState.getText().toString().equalsIgnoreCase(getResources().getString(R.string.show_comp))) {
                    view_reminderState.setText(R.string.hide_comp);
                    refreshArg = true;

                } else {
                    view_reminderState.setText(R.string.show_comp);
                    refreshArg = false;
                }
                refresh();
            }
        });
        refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_amenu, menu);

        Intent intent = getActivity().getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchActivity(query);
            AsyncSearchReminder asyncSearch = new AsyncSearchReminder();
            asyncSearch.execute();
        }
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setListAdapter(null);
                checkEmpty.setText(R.string.searchText);
                checkEmpty.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_64px, 0, 0, 0);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                refresh();
                checkEmpty.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshArg = false;
        refresh();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.ctx_reminder_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.sortTitle:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    changeSortOrder(1);
                }
                break;
            case R.id.sortDateModified:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    changeSortOrder(2);

                }
                break;
            case R.id.sortDateScheduled:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    changeSortOrder(3);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchActivity(String searchQuery) {

        searchArgs = app_database.tb_Struct.KEY_TITLE + " LIKE \'%" + searchQuery + "%\'";
        this.searchQuery = searchQuery;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Intent intentCli = new Intent(context, reminder_view.class);
        intentCli.putExtra(app_database.tb_Struct.KEY_ROW_ID, id);
        startActivityForResult(intentCli, ACTIVITY_EDIT);

    }

    public void refresh() {
        AsyncRefresh asyncRefresh = new AsyncRefresh();
        asyncRefresh.execute();
    }


    private class AsyncSearchReminder extends AsyncTask<Void, Cursor, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {

            dbHandler.open();
            Cursor cursor = dbHandler.readDb(searchArgs, null, sortOrder);
            getActivity().startManagingCursor(cursor);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            CursorAdapter cursorAdapter = new fragmentA_cursorAdapter(getActivity(), cursor, 0);
            setListAdapter(cursorAdapter);


            if (getListView().getCount() < 1) {
                String no_search = "\'" + searchQuery + "\' " + getString(R.string.no_match);
                checkEmpty.setText(no_search);
            } else {
                checkEmpty.setText("");
            }

        }
    }

    public class AsyncRefresh extends AsyncTask<Void, Cursor, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            dbHandler.open();
            Cursor updateStatus = dbHandler.readDb(null, null, sortOrder);
            getActivity().startManagingCursor(updateStatus);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat(reminder_edit.DATE_TIME_FORMAT, Locale.US);

            if (updateStatus != null) {

                updateStatus.moveToFirst();
                int dateInt = updateStatus.getColumnIndexOrThrow(app_database.tb_Struct.KEY_SCHEDULED_DATE);
                int rowInt = updateStatus.getColumnIndexOrThrow(app_database.tb_Struct.KEY_ROW_ID);

                while (!updateStatus.isAfterLast()) {

                    Long rowID = updateStatus.getLong(rowInt);

                    try {
                        Date date = dateFormat.parse(updateStatus.getString(dateInt));
                        calendar.setTime(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (calendar.getTimeInMillis() < getCurrentDate().getTimeInMillis()) {

                        dbHandler.updateDbCompleted(rowID, Boolean.toString(true));
                    } else {
                        dbHandler.updateDbCompleted(rowID, Boolean.toString(false));
                    }
                    updateStatus.moveToNext();
                }
            }

            Cursor cursor = dbHandler.readDb(app_database.tb_Struct.KEY_EXPIRED + " =\"" + refreshArg + "\"", null, sortOrder);
            getActivity().startManagingCursor(cursor);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            CursorAdapter cursorAdapter;
            if (refreshArg) {
                cursorAdapter = new CursorAdapterCompletedReminders(getActivity(), cursor, 0);
            } else {
                cursorAdapter = new fragmentA_cursorAdapter(getActivity(), cursor, 0);
            }
            setListAdapter(cursorAdapter);
            checkEmpty.setText(emptyList(refreshArg));
        }
    }

    public Calendar getCurrentDate() {

        return Calendar.getInstance();
    }

    public String emptyList(Boolean view) {

        if (getListView().getCount() < 1) {
            if (view)
                return getString(R.string.empty_list2);
            else
                return getString(R.string.empty_list);

        } else {
            return null;
        }
    }

    public void changeSortOrder(int orderBy) {

        switch (orderBy) {

            case 1:
                sortOrder = app_database.tb_Struct.KEY_TITLE + " ASC";
                break;
            case 2:
                sortOrder = app_database.tb_Struct.KEY_CREATED_DATE + " ASC";
                break;
            case 3:
                sortOrder = app_database.tb_Struct.KEY_SCHEDULED_DATE + " ASC";
                break;
        }
        refresh();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (itemId) {

            case R.id.delete_ctx:
                deleteColumn(info.id);
                return true;
            case R.id.edit_ctx:
                Intent intentCli = new Intent(context, reminder_edit.class);
                intentCli.putExtra(app_database.tb_Struct.KEY_ROW_ID, info.id);
                startActivityForResult(intentCli, ACTIVITY_EDIT);
                return true;
            case R.id.share_ctx:
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        dbHandler.open();

                        new Handler(getActivity().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        final Cursor cursor = dbHandler.readDb(app_database.tb_Struct.KEY_ROW_ID + "=" + info.id, null, null);
                                        getActivity().startManagingCursor(cursor);
                                        new Handler(getActivity().getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {

                                                String title = cursor.getString(cursor.getColumnIndexOrThrow(app_database.tb_Struct.KEY_TITLE));
                                                String body = cursor.getString(cursor.getColumnIndexOrThrow(app_database.tb_Struct.KEY_BODY));
                                                String time = cursor.getString(cursor.getColumnIndexOrThrow(app_database.tb_Struct.KEY_SCHEDULED_DATE_IN_WORDS));
                                                mCurrentPhotoPath = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(app_database.tb_Struct.KEY_IMAGE_URI)));
                                                //cursor.close();
                                                final String shareString = "To remind you about: " + title + " , regarding: " + body + ". Will take place on: " + time;
                                                new Handler(getActivity().getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareString);
                                                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareString);

                                                        Intent chooser = Intent.createChooser(shareIntent, getString(R.string.shareReminder));
                                                        startActivity(chooser);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }).start();

                            }
                        });
                    }
                }).start();

                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void deleteColumn(final long info) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHandler.open();
                dbHandler.deleteDb(app_database.tb_Struct.KEY_ROW_ID + " = " + info);
                new reminder_Manager(context).setReminderAlarm(info, null, false);
                new Handler(getActivity().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                        refresh();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_CREATE || requestCode == ACTIVITY_EDIT) {
            refresh();
        }
    }
}
