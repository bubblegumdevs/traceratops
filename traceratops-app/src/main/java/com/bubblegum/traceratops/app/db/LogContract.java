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

import android.provider.BaseColumns;

public class LogContract {

    public LogContract() {}

    public static abstract class LogSchema implements BaseColumns {
        public static final String TABLE_NAME = "log_entry";
        public static final String _ID = "_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_STACKTRACE = "stacktrace";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = "NOTNULL"

    /**
     * Create table for log_entry
     */
    public static final String SQL_CREATE_LOG_ENTRIES =
            "CREATE TABLE " + LogSchema.TABLE_NAME + " (" +
                    LogSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LogSchema.COLUMN_TIMESTAMP + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    LogSchema.COLUMN_TAG + TEXT_TYPE + COMMA_SEP +
                    LogSchema.COLUMN_LEVEL + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    LogSchema.COLUMN_DESCRIPTION + TEXT_TYPE +
                    LogSchema.COLUMN_STACKTRACE + TEXT_TYPE +
                    " )";

    /**
     * Drop the log_entry table
     */
    public static final String SQL_DELETE_LOG_ENTRIES =
            "DROP TABLE IF EXISTS " + LogSchema.TABLE_NAME;
}
