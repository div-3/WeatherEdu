package com.d.ivan.weatheredu.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//Определяем специальный класс для работы с БД SQLite
public class WeatherDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weather4.db"; // название бд
    private static final int DATABASE_VERSION = 2; // версия базы данных
    static final String TABLE_WEATHER = "weather"; // название таблицы в бд

    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_CURRENT_TEMP = "temp";
    public static final String COLUMN_PRESSURE = "pressure";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_WIND = "wind";

    private static final String TAG = "WeatherDBHelper";


    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_WEATHER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CITY + " TEXT, " +
                COLUMN_COUNTRY + " TEXT, " +
                COLUMN_CURRENT_TEMP + " REAL, " +
                COLUMN_PRESSURE + " REAL, " +
                COLUMN_HUMIDITY + " REAL, " +
                COLUMN_WIND + " REAL" +
                ");"
        );
      }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
        onCreate(sqLiteDatabase);
    }
}