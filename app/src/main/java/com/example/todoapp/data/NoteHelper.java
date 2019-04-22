package com.example.todoapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.todoapp.data.Contract.note;

public class NoteHelper extends SQLiteOpenHelper {
    private static int DB_VERSION = 1;
    private static String DB_NAME = "notes.db";
    public NoteHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String database = "CREATE TABLE "+ note.TABLE_NAME+"("+note.NOTE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                note.NOTE_TITLE+" TEXT NOT NULL,"+
                note.BODY+" TEXT,"+
                note.DATE +" TEXT,"+
                note.TIME +" TEXT,"+
                note.REMINDER+" INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(database);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor SearchNotes(String text){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+Contract.note.TABLE_NAME+" WHERE "+Contract.note.NOTE_TITLE+" LIKE '%"+text+"%'";
        Cursor c = db.rawQuery(query,null);
        return c;
    }
}
