package com.example.todoapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {
    public static final String content = "com.example.todoapp";
    public static final String notes = "notes";
    public static final Uri base_uri = Uri.parse("content://"+content);


    public static class note  implements BaseColumns {
        public static final String TABLE_NAME ="notes";
        public static final String NOTE_ID =BaseColumns._ID;
        public static final String NOTE_TITLE ="title";
        public static final String BODY ="body";
        public static final String DATE ="date";
        public static final String TIME ="time";
        public static final String REMINDER ="reminder";

        public static final int REMINDER_TRUE = 1;
        public static final int REMINDER_FALSE = 0;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(base_uri,notes);
    }
}
