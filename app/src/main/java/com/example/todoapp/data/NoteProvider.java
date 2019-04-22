package com.example.todoapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class NoteProvider extends ContentProvider {

    public static final int Notes = 100;
    public static final int Note_id = 101;

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(Contract.content,Contract.notes,Notes);
        uriMatcher.addURI(Contract.content,Contract.notes+"/#",Note_id);
    }

    NoteHelper noteHelper ;
    @Override
    public boolean onCreate() {
        noteHelper = new NoteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor c ;
        SQLiteDatabase db = noteHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case Notes:
                c = db.query(Contract.note.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case Note_id:
                selection = Contract.note.NOTE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = db.query(Contract.note.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = noteHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match){
            case Notes:
                long id = db.insert(Contract.note.TABLE_NAME,null,values);

                if (id == -1) {
                    Toast.makeText(getContext(), "not saved", Toast.LENGTH_SHORT).show();
                    return null;
                }

                Toast.makeText(getContext(), "saved ", Toast.LENGTH_SHORT).show();
                getContext().getContentResolver().notifyChange(uri,null);
                return ContentUris.withAppendedId(uri,id);
                default:
                    throw new IllegalArgumentException("Cannot insert note " + uri);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = noteHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match){
            case Notes:
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(Contract.note.TABLE_NAME,selection,selectionArgs);
            case Note_id:
                selection = Contract.note.NOTE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri,null);

                return db.delete(Contract.note.TABLE_NAME,selection,selectionArgs);

                default:
                    throw new IllegalArgumentException("Cannot delete unknown URI " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = noteHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match){
            case Notes :
                return db.update(Contract.note.TABLE_NAME,values,selection,selectionArgs);
            case Note_id:
                selection = Contract.note.NOTE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri,null);

                return db.update(Contract.note.TABLE_NAME,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot delete unknown URI " + uri);
        }

    }


}
