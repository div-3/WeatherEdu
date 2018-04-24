package com.d.ivan.weatheredu.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataSource {

    private WeatherDBHelper weatherDBHelper;
    private SQLiteDatabase sqLiteDatabase;

    private String[] allWeatherColumns = {
            WeatherDBHelper.COLUMN_ID,
            WeatherDBHelper.COLUMN_CITY,
            WeatherDBHelper.COLUMN_COUNTRY,
            WeatherDBHelper.COLUMN_CURRENT_TEMP,
            WeatherDBHelper.COLUMN_PRESSURE,
            WeatherDBHelper.COLUMN_HUMIDITY,
            WeatherDBHelper.COLUMN_WIND
    };

    private final String TAG = "WeatherDataSource";

    public WeatherDataSource(Context context){
        weatherDBHelper = new WeatherDBHelper(context);
    }

    public void open() throws SQLException {
        sqLiteDatabase = weatherDBHelper.getWritableDatabase();
    }

    public void close() {
        weatherDBHelper.close();
    }

    //Добавление новой строки города в БД
    public WeatherData addWeatherData(  String city,
                                        String country,
                                        float temp,
                                        float pressure,
                                        float humidity,
                                        float wind) {
        //Создаём временных MAP (ContentValue) и засовываем в неё данные по городу
        ContentValues values = new ContentValues();
        values.put(WeatherDBHelper.COLUMN_CITY, city);
        values.put(WeatherDBHelper.COLUMN_COUNTRY, country);
        values.put(WeatherDBHelper.COLUMN_CURRENT_TEMP, temp);
        values.put(WeatherDBHelper.COLUMN_PRESSURE, pressure);
        values.put(WeatherDBHelper.COLUMN_HUMIDITY, humidity);
        values.put(WeatherDBHelper.COLUMN_WIND, wind);

        //Добавление в БД кортежа из ContentValue, метод возвращает ID, в который вставил
        long insertId = sqLiteDatabase.insert(WeatherDBHelper.TABLE_WEATHER, null, values);

        WeatherData weatherData = new WeatherData();
        weatherData.setCity(city);
        weatherData.setCountry(country);
        weatherData.setTemp(temp);
        weatherData.setPressure(pressure);
        weatherData.setHumidity(humidity);
        weatherData.setWind(wind);
        weatherData.setId(insertId);

        return weatherData;
    }

    public void updateWeather (String city,
                               String country,
                               float temp,
                               float pressure,
                               float humidity,
                               float wind){

        ContentValues updatedWeather = new ContentValues();

        //updatedWeather.put(WeatherDBHelper.COLUMN_CITY, city);
        updatedWeather.put(WeatherDBHelper.COLUMN_COUNTRY, country);
        updatedWeather.put(WeatherDBHelper.COLUMN_CURRENT_TEMP, temp);
        updatedWeather.put(WeatherDBHelper.COLUMN_PRESSURE, pressure);
        updatedWeather.put(WeatherDBHelper.COLUMN_HUMIDITY, humidity);
        updatedWeather.put(WeatherDBHelper.COLUMN_WIND, wind);

        //Обновляем данные в БД. Метод update возвращает нам кол-во обновленных записей
       int nbr = 0;
        try {

            //Этот вариант обновления записи почему-то не заработал.
//           nbr = sqLiteDatabase.update(weatherDBHelper.TABLE_WEATHER,
//                   updatedWeather,
//                   weatherDBHelper.COLUMN_CITY + "=" + city,
//                   null);
            //Этот вариант обновления записи заработал нормально.
            nbr = sqLiteDatabase.update(weatherDBHelper.TABLE_WEATHER,
                    updatedWeather,
                    weatherDBHelper.COLUMN_CITY + "=?",
                    new String[]{city});
       } catch (Exception e){
           e.printStackTrace();
       }
        //Если ни одной записи не было обновлено, то добавляем новый кортеж в таблицу БД
        if (nbr == 0){
            addWeatherData(city,country,temp,pressure,humidity,wind);
        }
    }

    public List<WeatherData> getAllCitiesWeather() {
        List<WeatherData> weatherData = new ArrayList<WeatherData>();

        Cursor cursor = sqLiteDatabase.query(weatherDBHelper.TABLE_WEATHER,
                allWeatherColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WeatherData weatherDataTemp = cursorToWeather(cursor);
            weatherData.add(weatherDataTemp);
            cursor.moveToNext();
        }
        // Обязательно закрыть курсор
        cursor.close();
        return weatherData;
    }

    private WeatherData cursorToWeather(Cursor cursor) {
        WeatherData weatherData = new WeatherData();

        //Заполнение данными из курсора
        weatherData.setId(cursor.getLong(0));
        weatherData.setCity(cursor.getString(1));
        weatherData.setCountry(cursor.getString(2));
        weatherData.setTemp(cursor.getFloat(3));
        weatherData.setPressure(cursor.getFloat(4));
        weatherData.setHumidity(cursor.getFloat(5));
        weatherData.setWind(cursor.getFloat(6));

        return weatherData;
    }
}
