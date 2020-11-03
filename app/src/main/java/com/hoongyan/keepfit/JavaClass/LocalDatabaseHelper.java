package com.hoongyan.keepfit.JavaClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LocalDatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "user_data_table";
    private static final String COL1 = "date";
    private static final String COL2 = "steps";

    public LocalDatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL1 + " TEXT PRIMARY KEY, " +
                COL2 + " INT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean updateDailyData(int steps, Date date){

        if(date == null){
            date = new Date();
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(date);

        long result;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, formattedDate);
        contentValues.put(COL2, steps);

        Cursor cursor = getDateData(formattedDate);

        if(cursor.getCount() == 0){
            result = db.insert(TABLE_NAME, null, contentValues);
        }else{
            result = db.update(TABLE_NAME, contentValues, COL1 + " = '" + formattedDate + "'", null);
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);

        return data;
    }

    public Cursor getDateData(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + date + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public boolean deleteDateData(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, COL1 + " = '" + date + "'", null);

        return result != -1;
    }

    public Cursor getPreviousDateData(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME +
                " WHERE " + COL1 + " <= '" + date + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
