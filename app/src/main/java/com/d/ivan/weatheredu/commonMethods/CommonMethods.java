package com.d.ivan.weatheredu.commonMethods;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class CommonMethods {

    //Метод сохранения данных в указанном файле SharedPreferences по указанному ключу
    public static boolean storeToSharedCrypto(Context context, String keyValue, String dataValue){

        if (keyValue.isEmpty() || dataValue.isEmpty()) return false;

        SharedPreferences sPrefs = context.getSharedPreferences("FragmentMainCurrentWeatherData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putString(keyValue, dataValue);
        editor.commit();
        return true;
    }

    //Метод получения данных из указанного файла SharedPreferences по указанному ключу
    public static String loadSharedCrypto(Context context, String keyValue){

        if (keyValue.isEmpty()) return null;

        SharedPreferences sPrefs = context.getSharedPreferences("FragmentMainCurrentWeatherData", Context.MODE_PRIVATE);

        return sPrefs.getString(keyValue, null);
    }
}
