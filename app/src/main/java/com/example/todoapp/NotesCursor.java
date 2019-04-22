package com.example.todoapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.todoapp.data.Contract.note;

public class NotesCursor extends CursorAdapter {

    public NotesCursor(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.note_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title =view.findViewById(R.id.title);
        TextView date =view.findViewById(R.id.date);
        TextView time =view.findViewById(R.id.time);

            String note_title = cursor.getString(cursor.getColumnIndex(note.NOTE_TITLE));
            String note_time = cursor.getString(cursor.getColumnIndex(note.TIME));
            String note_date = cursor.getString(cursor.getColumnIndex(note.DATE));

            title.setText(note_title);
            date.setText(note_date);
            time.setText(note_time);

    }
}
