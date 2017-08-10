package com.project.scanner.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.project.scanner.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private String DB_PATH = null;
    public static final int DATABASE_VERSION = 1;

    private final Context myContext;
    private SQLiteDatabase db;

    public DBHelper(Context ctx) {
        super(ctx, Constants.DB_NAME, null, DATABASE_VERSION);
        this.myContext = ctx;
        DB_PATH = "/data/data/" + myContext.getPackageName() + "/databases/" + Constants.DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        boolean dbExist = checkDataBase();

        if (dbExist) {
//            db.execSQL("drop table if exists " + Constants.TABLE_NAME);
//
//            db.execSQL("create table " + Constants.TABLE_NAME + " ("
//                    + Constants.DATA_VALUE + " text not null, "
//                    + Constants.DATA_FORMAT + " number not null, "
//                    + Constants.DATA_TYPE + " text not null, "
//                    + Constants.DATA_DATE + " text not null, "
//                    + "PRIMARY KEY (" + Constants.DATA_VALUE + ", "
//                    + Constants.DATA_FORMAT + ", " + Constants.DATA_TYPE + "))");

            Log.d("SQLiteDatabase", "DB Exists");

        } else {
            db.execSQL("create table " + Constants.TABLE_NAME + " ("
                    + Constants.DATA_VALUE + " text not null, "
                    + Constants.DATA_FORMAT + " number not null, "
                    + Constants.DATA_TYPE + " text not null, "
                    + Constants.DATA_DATE + " text not null, "
                    + "PRIMARY KEY (" + Constants.DATA_VALUE + ", "
                    + Constants.DATA_FORMAT + ", " + Constants.DATA_TYPE + "))");

            Log.d("SQLiteDatabase", "DB created successfully");
        }

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
        db.execSQL("drop table if exists " + Constants.TABLE_NAME);
        onCreate(db);
    }


    public List<BarcodeData> getAllHistory() {

        db = this.getReadableDatabase();
        onCreate(db);

        List<BarcodeData> res = new ArrayList<BarcodeData>();

        Cursor cur = db.query(Constants.TABLE_NAME, new String[]{Constants.DATA_VALUE, Constants.DATA_FORMAT, Constants.DATA_TYPE, Constants.DATA_DATE
                }, null, null, null, null, null);
        if (cur != null) {
            while (cur.moveToNext()) {

                String displayValue = cur.getString(0);
                int format = cur.getInt(1);
                String type = cur.getString(2);
                String date = cur.getString(3);

                BarcodeData fd = new BarcodeData(displayValue, format, type, date);
                res.add(fd);

            };
        }
        db.close();
        return res;
    }

    public Long addHistory(BarcodeData barcodeData) {
        db = this.getWritableDatabase();
        onCreate(db);

        ContentValues initialValues = new ContentValues();
        initialValues.put(Constants.DATA_VALUE, barcodeData.getDisplayValue());
        initialValues.put(Constants.DATA_FORMAT, barcodeData.getFormat());
        initialValues.put(Constants.DATA_TYPE, barcodeData.getType());
        initialValues.put(Constants.DATA_DATE, barcodeData.getDate());

        Long id = db.insertWithOnConflict(Constants.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();

        return id;
    }

    public int deleteHistory(String displayValue) {

        db = this.getWritableDatabase();
        onCreate(db);

        StringBuilder whereClause = new StringBuilder();
        whereClause.append(Constants.DATA_VALUE + "=? ");
        //whereClause.append(Constants.DATA_FORMAT + "=? AND");
        //whereClause.append(Constants.DATA_TYPE + "=? ");

        int id = db.delete(Constants.TABLE_NAME, whereClause.toString(),
                new String[] { displayValue});

        db.close();

        return id;
    }
}

