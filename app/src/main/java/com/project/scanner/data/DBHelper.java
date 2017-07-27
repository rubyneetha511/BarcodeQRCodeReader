package com.project.scanner.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siva on 7/10/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    String DB_PATH = null;

    private final Context myContext;

    private static final String TABLE_NAME = "qrcodes";
    private static final String DB_NAME = "spaces.db";

    // Data
    private static final String DATA_VALUE = "value";
    private static final String DATA_FORMAT = "format";
    private static final String DATA_TIMESTAMP = "timestamp";


    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, 1);
        this.myContext = ctx;
        DB_PATH = "/data/data/" + myContext.getPackageName() + "/databases/" + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            Log.d("SQLiteDatabase", "DB Exists");

        } else {
            db.execSQL("create table " + TABLE_NAME + " ("+ DATA_VALUE + " text not null, "
                    + DATA_FORMAT + " text not null)");

            Log.d("SQLiteDatabase", "DB created successfully");
        }
        //do some insertions or whatever you need


    }

    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            // database does't exist yet.
            Log.w("SQLiteDatabase", "DB does't exist yet!");
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists" + TABLE_NAME);
        onCreate(db);
    }


    public List<HistoryData> getAllHistory(SQLiteDatabase db) {

        List<HistoryData> res = new ArrayList<HistoryData>();
        db = this.getReadableDatabase();

        Cursor cur = db.query(TABLE_NAME, new String[]{DATA_VALUE, DATA_FORMAT
                }, null, null, null, null, null);
        if (cur != null) {
            while (cur.moveToNext()) {

                String dataValue = cur.getString(0);
                String dataFormat = cur.getString(1);

                HistoryData fd = new HistoryData(dataValue, dataFormat);
                res.add(fd);

            };
        }
        db.close();
        return res;
    }

    public Long addHistory(SQLiteDatabase db, String dataValue, String dataFormat) {

        onCreate(db);

        ContentValues cv = new ContentValues();
        cv.put(DATA_VALUE, dataValue);
        cv.put(DATA_FORMAT, dataFormat);
        Long bid = db.insert(TABLE_NAME, null, cv);
        db.close();

        return bid;
    }

    public Long dataExists(SQLiteDatabase db, String dataValue, String dataFormat) {

        ContentValues cv = new ContentValues();
        cv.put(DATA_VALUE, dataValue);
        cv.put(DATA_FORMAT, dataFormat);
        db = this.getWritableDatabase();
        Long bid = db.replace(TABLE_NAME, null, cv);
        db.close();

        return bid;
    }

}

