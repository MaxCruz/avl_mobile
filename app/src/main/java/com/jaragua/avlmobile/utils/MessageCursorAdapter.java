package com.jaragua.avlmobile.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.jaragua.avlmobile.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MessageCursorAdapter extends SimpleCursorAdapter {

    private final LayoutInflater inflater;
    private int layout;
    private String[] from;
    private int[] to;

    public MessageCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        this.layout = layout;
        this.from = from;
        this.to = to;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        int messageIndex = cursor.getColumnIndex(from[0]);
        int receivedIndex = cursor.getColumnIndex(from[1]);
        int statusIndex = cursor.getColumnIndex(from[2]);
        String message = cursor.getString(messageIndex);
        String received = cursor.getString(receivedIndex);
        int status = cursor.getInt(statusIndex);
        TextView textViewMessage = (TextView) view.findViewById(to[0]);
        TextView textViewReceived = (TextView) view.findViewById(to[1]);
        ImageView imageViewMessageIcon = (ImageView) view.findViewById(to[2]);
        textViewMessage.setText(message);
        try {
            Date dateReceived = Constants.LocationService.FORMAT_DATE.parse(received);
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
            String formatReceived = format.format(dateReceived);
            textViewReceived.setText(formatReceived);
        } catch (ParseException e) {
            textViewReceived.setText(received);
            e.printStackTrace();
        }
        Drawable image = ContextCompat.getDrawable(context, R.drawable.ic_markunread_black_36dp);
        if (status != 0) {
            textViewMessage.setTypeface(null, Typeface.NORMAL);
            image = ContextCompat.getDrawable(context, R.drawable.ic_drafts_black_36dp);
        }
        imageViewMessageIcon.setImageDrawable(image);
    }

}
