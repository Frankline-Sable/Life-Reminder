package com.maseno.franklinesable.lifereminder;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class is responsible for maintaining the list reminders and has enhanced performance
 */
public class CursorAdapterForFavourite extends CursorAdapter {

    private final LayoutInflater cursorInflater;
    private Handler mHandler = new Handler();
    public static int colorView;


    static class ViewHolder {

        private TextView scheduledDate, currentState, title, body;
        private Typeface apple_Font;

    }

    public CursorAdapterForFavourite(Activity context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return cursorInflater.inflate(R.layout.favourite_layout_lists, parent, false);

    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        final ViewHolder viewHolder = new ViewHolder();

        viewHolder.scheduledDate = (TextView) view.findViewById(R.id.text3);
        viewHolder.currentState = (TextView) view.findViewById(R.id.text5);
        viewHolder.title = (TextView) view.findViewById(R.id.text4);
        viewHolder.body = (TextView) view.findViewById(R.id.text6);

        viewHolder.apple_Font = Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica Neue Light (Open Type).ttf");

        String title = cursor.getString(cursor.getColumnIndexOrThrow(app_database.tb_Struct.KEY_TITLE));
        String body = cursor.getString(cursor.getColumnIndexOrThrow(app_database.tb_Struct.KEY_BODY));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(app_database.tb_Struct.KEY_SCHEDULED_DATE_IN_WORDS));

        colorView = cursor.getInt(cursor.getColumnIndexOrThrow(app_database.tb_Struct.KEY_COLOR));

        viewHolder.body.setTextColor(context.getResources().getColor(colorView));

        viewHolder.scheduledDate.setTypeface(viewHolder.apple_Font);
        viewHolder.currentState.setTypeface(viewHolder.apple_Font);
        viewHolder.title.setTypeface(viewHolder.apple_Font);
        viewHolder.body.setTypeface(viewHolder.apple_Font);

        viewHolder.title.setText(title);
        viewHolder.body.setText(body);
        viewHolder.scheduledDate.setText(date);

        String reminderState = cursor.getString(cursor.getColumnIndexOrThrow(app_database.tb_Struct.KEY_EXPIRED));

        if (reminderState.equalsIgnoreCase("false")) {
            viewHolder.currentState.setText(R.string.pending);
            viewHolder.currentState.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.pending_icon,0);

        } else {
            viewHolder.currentState.setText(R.string.complete);
            viewHolder.currentState.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.completed_icon, 0);
        }

        if (favourite_reminders.deleteFav) {

            view.animate().alpha((float) 0.7).translationXBy(100).rotation(2).setDuration(700).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fav_delete_icon, 0, 0, 0);
                                    viewHolder.title.setAlpha((float) 0.9);
                                }
                            });
                        }
                    }).start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


        }
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (favourite_reminders.deleteFav) {

                    View parentView = (View) v.getParent();
                    final ListView listView = (ListView) parentView.getParent();
                    final int pos = listView.getPositionForView(parentView);

                    view.animate().translationXBy(300).setDuration(700).rotation(90).alpha(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            app_database dbHandler = new app_database(context);
                                            dbHandler.open();
                                            dbHandler.deleteDb(app_database.tb_Struct.KEY_ROW_ID + " = " + listView.getItemIdAtPosition(pos));
                                            new reminder_Manager(context).setReminderAlarm(listView.getItemIdAtPosition(pos), null, false);
                                        }
                                    });
                                }
                            }).start();

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            view.setVisibility(View.GONE);
                            Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                }
            }
        });
    }
}


