package com.maseno.franklinesable.lifereminder;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Frankline Sable on 8/13/2016. From Maseno University in Kenya. Life Reminder
 */
public class arrayAdapterForHelp extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] helpInfo;



    static class ViewHolder{
        public TextView helpTexts;
        public int imageArray[]={R.drawable.share_facebook, R.drawable.email_us, R.drawable.ic_email};

    }

    public arrayAdapterForHelp(Activity context, String[] helpInfo) {
        super(context, R.layout.help_view_layout, helpInfo);
        this.context=context;
        this.helpInfo = helpInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView=convertView;
        //reuse views
        if(rowView==null){

            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.help_view_layout, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.helpTexts = (TextView) rowView.findViewById(R.id.textHelp);

            rowView.setTag(viewHolder);
        }
        // fill data
        Typeface apple_Font = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.appleFont));


        ViewHolder holder = (ViewHolder) rowView.getTag();
        String s = helpInfo[position];
        holder.helpTexts.setText(s);
        holder.helpTexts.setTypeface(apple_Font);
        holder.helpTexts.setCompoundDrawablesWithIntrinsicBounds(holder.imageArray[position],0,0,0);

        return rowView;
    }
}
