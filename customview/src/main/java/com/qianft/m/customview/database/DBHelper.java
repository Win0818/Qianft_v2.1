package com.qianft.m.customview.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper {
    private static final String DATABASE_NAME = "download.db";
    private static final int DATABASE_VERSION = 2;
    public static final String DOWNLOADS_TABLE_NAME = "downloads";
    public static final String DYNAMIC_TABLE_NAME = "dynamic";

    @SuppressWarnings("unused")
    private final Context mContext;
    private DatabaseHelper mDatabaseHelper;
    private static DBHelper mInstance = null;
    public static final String DOWNLOADS_TAB = "create table " + DOWNLOADS_TABLE_NAME + " (_id integer primary key autoincrement, "
            + " download_id text,"
            + " app_id text,"
            + " title text,"
            + " icon text,"
            + " status integer,"
            + " url text,"
            + " file_name text,"
            + " filepath text,"
            + " packageName text,"
            + " totalBytes integer,"
            + " currentBytes integer,"
            + "main_type_name text,"
            + "parent_type_name text,"
            + "app_en_name text,"
            + "age_type text);";

    public static final String DOWNLOADS_DYNAMIC_TAB = "create table " + DYNAMIC_TABLE_NAME + " (_id integer primary key autoincrement, "
            + " download_id text,"
            + " app_id text,"
            + " title text,"
            + " icon text,"
            + " status integer,"
            + " url text,"
            + " file_name text,"
            + " filepath text,"
            + " packageName text,"
            + " totalBytes integer,"
            + " currentBytes integer,"
            + "main_type_name text,"
            + "parent_type_name text,"
            + "app_en_name text,"
            + "age_type text);";

    private SQLiteDatabase db;

    public static class DownLoads {
        public static final String ID = "_id";
        public static final String DOWNLOAD_ID = "download_id";
        public static final String APP_ID = "app_id";
        public static final String TITLE = "title";
        public static final String ICON = "icon";
        public static final String STATUS = "status";
        public static final String URL = "url";
        public static final String FILE_NAME = "file_name";
        public static final String FILE_PATH = "filepath";
        public static final String TOTAL_BYTES = "totalBytes";
        public static final String CURRENT_BYTES = "currentBytes";
        public static final String PACKAGE_NAME = "packageName";
        public static final String MAIN_TYPE_NAME = "main_type_name";
        public static final String PARENT_TYPE_NAME = "parent_type_name";
        public static final String APP_EN_NAME = "app_en_name";
        public static final String AGE_TYPE = "age_type";
    }

    public DBHelper(Context context) {
        this.mContext = context;
        mDatabaseHelper = new DatabaseHelper(context);
    }

    public synchronized static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context.getApplicationContext());
        }
        return mInstance;
    }
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DOWNLOADS_TAB);
            db.execSQL(DOWNLOADS_DYNAMIC_TAB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DOWNLOADS_TAB);
            db.execSQL("DROP TABLE IF EXISTS " + DOWNLOADS_DYNAMIC_TAB);
        }
    }

    public DBHelper open() throws SQLException {
        close();
        db = mDatabaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDatabaseHelper.close();
    }

    public boolean deleteRow(String tableName, long rowId) {
        open();
        return db.delete(tableName, "_id =" + rowId, null) > 0;
    }

    public boolean deleteRow(String tableName, String whereSql) {
        open();
        Log.d("Wing", "deleteRow");
        return db.delete(tableName, whereSql, null) > 0;
    }

    public Cursor getCursorBySql(String sqlStr) {
        open();
        return db.rawQuery(sqlStr, null);
    }

    public Cursor getCursorGroupBy(String tableName, String columnName, String groupBy) throws SQLException {
        open();
        return db.rawQuery("select " + columnName + " from " + tableName + " " + groupBy, null);
    }

    public synchronized Cursor getCursor(String tableName, String columeName, String value) throws SQLException {
        open();
        return db.rawQuery("select * from " + tableName + " where " + columeName + " ='" + value + "'", null);
    }

    public Cursor getTopN(String tableName, String columeName, int topN) throws SQLException {
        open();
        return db.rawQuery("select * from " + tableName + " order by " + columeName + " desc limit " + topN, null);
    }

    public long insertValues(String tableName, ContentValues values) {
        open();
        return db.insert(tableName, null, values);
    }

    public boolean updateValues(String tableName, ContentValues values, String whereStr) {
        open();
        return db.update(tableName, values, whereStr, null) > 0;
    }

    public void InsertValuesWithTrans(String tableName, ContentValues values) {
        open();
        db.beginTransaction();
        db.insert(tableName, null, values);
        db.endTransaction();
    }
    public boolean updateColumeValue(String tableName, String columeName, String value, String whereColume, String whereValue) {
        ContentValues args = new ContentValues();
        args.put(columeName, value);
        open();
        return db.update(tableName, args, whereColume + "='" + whereValue + "'", null) > 0;
    }
}