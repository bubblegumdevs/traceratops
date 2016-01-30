/*
 *
 * Copyright 2015 Bubblegum Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bubblegum.traceratops.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.bubblegum.traceratops.app.model.LogEntry;

import java.util.ArrayList;

public class TraceratopsDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "bubblegum_db";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Logs.db";

    public TraceratopsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table for log entries
        sqLiteDatabase.execSQL(TraceratopsContract.SQL_CREATE_LOG_ENTRIES);
        // Create a table for pings
        sqLiteDatabase.execSQL(TraceratopsContract.SQL_CREATE_PING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now discarding the entire database and creating a new one
        sqLiteDatabase.execSQL(TraceratopsContract.SQL_DELETE_LOG_ENTRIES);
        sqLiteDatabase.execSQL(TraceratopsContract.SQL_DELETE_PING_TABLE);

        onCreate(sqLiteDatabase);
    }

    public void insertLogInDb(@NonNull long timestamp, @NonNull int level, String tag, String description, String stacktrace) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TraceratopsContract.LogSchema.COLUMN_TIMESTAMP, timestamp);
        contentValues.put(TraceratopsContract.LogSchema.COLUMN_LEVEL, level);
        if (tag != null) {
            contentValues.put(TraceratopsContract.LogSchema.COLUMN_TAG, tag);
        }
        if (description != null) {
            contentValues.put(TraceratopsContract.LogSchema.COLUMN_DESCRIPTION, description);
        }
        if (stacktrace != null) {
            contentValues.put(TraceratopsContract.LogSchema.COLUMN_STACKTRACE, stacktrace);
        }

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TraceratopsContract.LogSchema.TABLE_NAME, null, contentValues);
    }

    /**
     * Reads all log entries from the database
     * @return Cursor pointing to all log entries
     */
    public Cursor readAllLogs() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TraceratopsContract.LogSchema.TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    /**
     * Reads all log entries from the database
     * @return List of all log entries
     */
    public ArrayList<LogEntry> getAllLogs() {
        ArrayList<LogEntry> allLogs = new ArrayList();
        Cursor cursor = readAllLogs();

        if (cursor.moveToFirst()) {
            do {
                long timestamp = cursor.getLong(cursor.getColumnIndex(TraceratopsContract.LogSchema.COLUMN_TIMESTAMP));
                int level = cursor.getInt(cursor.getColumnIndex(TraceratopsContract.LogSchema.COLUMN_LEVEL));
                String tag = cursor.getString(cursor.getColumnIndex(TraceratopsContract.LogSchema.COLUMN_TAG));
                String desc = cursor.getString(cursor.getColumnIndex(TraceratopsContract.LogSchema.COLUMN_DESCRIPTION));
                String stacktrace = cursor.getString(cursor.getColumnIndex(TraceratopsContract.LogSchema.COLUMN_STACKTRACE));

                LogEntry logEntry = new LogEntry();
                logEntry.timestamp = timestamp;
                logEntry.level = level;
                logEntry.tag = tag;
                logEntry.description = desc;
                logEntry.stackTrace = stacktrace;

                allLogs.add(logEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allLogs;
    }
}
