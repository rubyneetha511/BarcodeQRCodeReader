package com.project.scanner.siva;

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

public class dbHelper1 extends SQLiteOpenHelper {
    String DB_PATH = null;
    private final Context myContext;

    // Contacts table name
    private static final String TABLE_FRIEND = "qrcode";



    public dbHelper1(Context ctx) {
        super(ctx, "contact.db", null, 1);
        this.myContext = ctx;
        DB_PATH = "/data/data/" + myContext.getPackageName() + "/"
                + "databases/contact.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        boolean dbExist = checkDataBase();

        if (dbExist) {
        } else {
            db.execSQL("create table qrcode(scantext text not null,scanformat text not null)");

            Log.d("create DB", "Success");
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
            System.out.println("DB does't exist yet!");
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// TODO Auto-generated method stub
        db.execSQL("drop table if exists qrcode");
        onCreate(db);
    }

//    public Long insertBook(String bname,
//                           String author, int pyear) {
//        ContentValues cv = new ContentValues();
//        cv.put("bname", bname);
//        cv.put("author", author);
//        cv.put("pubyear", pyear);
//
//        SQLiteDatabase db =
//                this.getWritableDatabase();
//        Long bid = db.insert("books", null, cv);
//        db.close();
//        return bid;
//    }
//

    public List<History_Data> getAllHistory(SQLiteDatabase db) {

        List<History_Data> res = new ArrayList<History_Data>();
        db = this.getReadableDatabase();

        Cursor cur = db.query(TABLE_FRIEND, new String[]{"scantext","scanformat"
                }, null, null, null, null, null);
        if (cur != null) {
            cur.moveToFirst();
            do {
                // res.add(cur.getString(0) + "," + cur.getString(1) + "," + cur.getString(2) + "\n");

                String scantext=cur.getString(0);
                String scanformat=cur.getString(1);
                History_Data fd=new History_Data(scantext,scanformat);
                res.add(fd);

            } while (cur.moveToNext());
        }
        db.close();
        return res;
    }

    public Long addHistory(SQLiteDatabase db, String scantext, String scanformat) {

        ContentValues cv = new ContentValues();
        cv.put("scantext", scantext);
        cv.put("scanformat", scanformat);
         db = this.getWritableDatabase();
        Long bid = db.insert("books", null, cv);
        db.close();

        return bid;


    }

}

