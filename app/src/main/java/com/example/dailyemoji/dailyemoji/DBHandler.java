package com.example.dailyemoji.dailyemoji;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "dailyEmoji";
    // Contacts table name
    private static final String TABLE_RATES = "rates";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_RATING = "rating";
    private static final String KEY_EMOJI = "emoji";
    private static final String KEY_NOTE = "note";
    private static final String KEY_TIMESTAMP = "timestamp";

    DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RATES_TABLE = "CREATE TABLE " + TABLE_RATES + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_RATING + " INTEGER,"
        + KEY_EMOJI + " TEXT, " + KEY_NOTE + " TEXT, " + KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_RATES_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATES);
        // Creating tables again
        onCreate(db);
    }

    // Adding new rating
    void addRating(Rating rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_RATING, rating.getRating());
        values.put(KEY_EMOJI, rating.getEmoji());
        values.put(KEY_NOTE, rating.getNote());
        values.put(KEY_TIMESTAMP, getDateTime());

        // Inserting Row
        db.insert(TABLE_RATES, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Ratings
    List<Rating> getAllRatings() {
        List<Rating> ratingList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RATES + " ORDER BY id DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Rating rating = new Rating();
                rating.setRating(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_RATING))));
                rating.setEmoji(cursor.getString(cursor.getColumnIndex(KEY_EMOJI)));
                rating.setNote(cursor.getString(cursor.getColumnIndex(KEY_NOTE)));
                rating.setTimestamp(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP)));

                // Adding contact to list
                ratingList.add(rating);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ratingList;
    }

    void addRandomData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Random rand = new Random();

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int currYear = year;
        int month = now.get(Calendar.MONTH);
        int currMonth = month;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int currDay = day;

        // start from a year ago
        year = year - 1;

        while (year <= currYear) {

            day++;

            if (day > 31) {
                day = 1;
                month++;
            }

            if (month > 11) {
                month = 0;
                year++;
            }

            String strMonth = Integer.toString(month);

            if (month < 10) {
                strMonth = "0" + strMonth;
            }

            String strDay = Integer.toString(day);

            if (day < 10) {
                strDay = "0" + strDay;
            }

            values.put(KEY_RATING, rand.nextInt(11));
            values.put(KEY_EMOJI, "emoji");
            values.put(KEY_NOTE, "note");
            values.put(KEY_TIMESTAMP, year + "-" + strMonth + "-" + strDay + " " + rand.nextInt(25) + ":" + rand.nextInt(61) + ":" + rand.nextInt(61));

            // Inserting Row
            db.insert(TABLE_RATES, null, values);
        }

        db.close(); // Closing database connection
    }

    void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_RATES);
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}

