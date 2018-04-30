package com.d.ivan.weatheredu.viewPager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;

import com.d.ivan.weatheredu.FragmentChangeCity;
import com.d.ivan.weatheredu.FragmentMainCurrentCityWeather;
import com.d.ivan.weatheredu.db.WeatherDataSource;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "MyPagerAdapter";
    private int pageCount = 0;      //Количество загруженных ранее городов
    private int prevousPosition =0; //Позиция во ViewPager, на которой пользователь остановился

    //Для работы с БД
    private WeatherDataSource dataBase;

    public MyPagerAdapter(FragmentManager fm, WeatherDataSource dataBase) {
        super(fm);
        this.dataBase = dataBase;   //Получаем объект для управления БД
        pageCount = dataBase.getCityCountFromDB();
        pageCount++;    //увеличиваем количесво страниц у ViewPager, т.к. к страницам из БД всегда добавляется страница с выбором города
        Log.d(TAG, "MyPagerAdapter: pageCount: " + pageCount);
    }

    //Запуск нужного фрагмента в зависимости от позиции в ViewPager
    @Override
    public Fragment getItem(int position) {
        String name = null;
        name = dataBase.getCityNameFromDBByNumber(position);

        if(name != null){
            return FragmentMainCurrentCityWeather.newInstance(name);
        } else {
            return FragmentChangeCity.newInstance();
        }
    }


    @Override
    public int getCount() {
        return pageCount;
    }

    //Адаптер определяет какой город был выбран последним.

}
