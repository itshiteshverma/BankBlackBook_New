package com.itshiteshverma.bankblackbook.HelperClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler_AutoComplete extends SQLiteOpenHelper {

    // for our logs
    public static final String TAG = "DatabaseHandler_AutoComplete.java";
    // database name
    protected static final String DATABASE_NAME = "PolicyAndFDManager";
    // database version
    private static final int DATABASE_VERSION = 4;
    // table details
    public String tableName = "MasterTable";
    public String fieldObjectId = "id";
    public String fieldObjectName = "name";
    public String fieldValueName = "value";
    public String table_Current;

    // constructor
    public DatabaseHandler_AutoComplete(Context context, String table_Current) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.table_Current = table_Current;
    }

    // creating table
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "";

        sql += "CREATE TABLE " + tableName;
        sql += " ( ";
        sql += fieldObjectId + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += fieldObjectName + " TEXT, ";
        sql += fieldValueName + " TEXT NOT NULL UNIQUE ";
        sql += " ) ";

        db.execSQL(sql);

    }

    // When upgrading the database, it will drop the current table and recreate.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(sql);

        onCreate(db);
    }

    // create new record
    // @param myObj contains details to be added as single row.
    public boolean create(MyObject myObj) {

        boolean createSuccessful = false;

        if (!checkIfExists(myObj.objectName)) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(fieldObjectName, myObj.objectName);
            values.put(fieldValueName, myObj.objectValue);
            createSuccessful = db.insert(tableName, null, values) > 0;

            db.close();

            if (createSuccessful) {
                //  Log.e(TAG, myObj.objectName + " created.");
            }
        }

        return createSuccessful;
    }

    // check if a record exists so it won't insert the next time you run this code
    public boolean checkIfExists(String objectName) {

        boolean recordExists = false;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + fieldObjectId +
                " FROM " + tableName + " WHERE " + fieldValueName + " = '" + objectName + "'", null);

        if (cursor != null) {

            if (cursor.getCount() > 0) {
                recordExists = true;
            }
        }

        cursor.close();
        db.close();

        return recordExists;
    }

    // Read records related to the search term
    public List<MyObject> read(String searchTerm) {

        List<MyObject> recordsList = new ArrayList<MyObject>();

        // select query
        String sql = "";
        sql += "SELECT DISTINCT * FROM " + tableName;
        sql += " WHERE " + fieldObjectName + " = '" + table_Current + "' AND " + fieldValueName + " LIKE '%" + searchTerm + "%'";
        sql += " ORDER BY " + fieldObjectId + " DESC";
        sql += " LIMIT 0,5";

        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                //String objectName = cursor.getString(cursor.getColumnIndex(fieldObjectName));
                String objectValue = cursor.getString(cursor.getColumnIndex(fieldValueName));
                MyObject myObject = new MyObject(objectValue);
                // add to list
                recordsList.add(myObject);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return the list of records
        return recordsList;
    }

}