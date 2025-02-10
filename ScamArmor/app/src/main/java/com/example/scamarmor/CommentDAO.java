package com.example.scamarmor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public CommentDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addComment(String comment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COMMENT, comment);
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis()); // Add current timestamp
        database.insert(DatabaseHelper.TABLE_COMMENTS, null, values);
    }

    public void deleteComment(long id) {
        database.delete(DatabaseHelper.TABLE_COMMENTS, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(DatabaseHelper.TABLE_COMMENTS,
                    new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_COMMENT, DatabaseHelper.COLUMN_TIMESTAMP},
                    null, null, null, null, DatabaseHelper.COLUMN_ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                    int commentIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_COMMENT);
                    int timestampIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP);
                    if (commentIndex != -1 && timestampIndex != -1) {
                        long id = cursor.getLong(idIndex);
                        String comment = cursor.getString(commentIndex);
                        long timestamp = cursor.getLong(timestampIndex);
                        comments.add(new Comment(id, comment, timestamp));
                    } else {
                        Log.e("CommentDAO", "Column does not exist.");
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return comments;
    }

    public long getCommentId(String comment) {
        Cursor cursor = null;
        long id = -1;
        try {
            cursor = database.query(DatabaseHelper.TABLE_COMMENTS,
                    new String[]{DatabaseHelper.COLUMN_ID},
                    DatabaseHelper.COLUMN_COMMENT + " = ?",
                    new String[]{comment},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                if (idIndex != -1) {
                    id = cursor.getLong(idIndex);
                } else {
                    Log.e("CommentDAO", "Column '" + DatabaseHelper.COLUMN_ID + "' does not exist.");
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return id;
    }

    public class Comment {
        private long id;
        private String comment;
        private long timestamp;

        public Comment(long id, String comment, long timestamp) {
            this.id = id;
            this.comment = comment;
            this.timestamp = timestamp;
        }

        public long getId() {
            return id;
        }

        public String getComment() {
            return comment;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
