package com.contentproviders_sqlite_example;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Ramesh on 9/30/16.
 */

public class MyContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.contentproviders_sqlite_example.MyContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/cpcontacts";
    static final Uri CONTENT_URL = Uri.parse(URL);

//    static final String id = "id";
//    static final String name = "name";
    static final int uriCode = 1;

    private static HashMap<String, String> contentValues;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "cpcontacts", uriCode);
    }

    private SQLiteDatabase sqlDB;
    static final String DATABASE_NAME = "myContacts";
    static final String TABLE_NAME = "names";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + " name TEXT NOT NULL);";

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        if (sqlDB != null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        // Used to create a SQL query
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set table to query
        queryBuilder.setTables(TABLE_NAME);

        // Used to match uri's with Content Providers
        switch (uriMatcher.match(uri)) {
            case uriCode:
                // A projection map maps from passed column names to database column names
                queryBuilder.setProjectionMap(contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // Cursor provides read and write access to the database
        Cursor cursor = queryBuilder.query(sqlDB, strings, s, strings1, null, null, s1);
        // Register to watch for URI changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Handles requests for the MIME type (Type of Data) of the data at the URI
    @Nullable
    @Override
    public String getType(Uri uri) {
        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case uriCode:
                return "vnd.android.cursor.dir/cpcontacts";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = sqlDB.insert(TABLE_NAME, null, contentValues);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URL, rowID);

            // getContentResolver provides access to the content model
            // notifyChange notifies all observers that a row was updated
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        } else {
            Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int rowsDeleted = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                rowsDeleted = sqlDB.delete(TABLE_NAME, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int rowsUpdated = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                rowsUpdated = sqlDB.update(TABLE_NAME, contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int i, int i1) {
            sqlDB.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
            onCreate(sqlDB);
        }
    }
}
