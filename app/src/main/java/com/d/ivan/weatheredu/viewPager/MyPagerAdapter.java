package com.d.ivan.weatheredu.viewPager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.d.ivan.weatheredu.DataAdapter;
import com.d.ivan.weatheredu.FragmentChangeCity;
import com.d.ivan.weatheredu.FragmentMainCurrentCityWeather;
import com.d.ivan.weatheredu.commonMethods.CommonMethods;

import java.util.Map;

import static com.d.ivan.weatheredu.MainActivity.CURRENT_CITY_KEY_VALUE;

public class MyPagerAdapter extends FragmentPagerAdapter{

    private Context context;
    private static final String TAG = "MyPagerAdapter";
    private int pageCount = 0;      //Количество загруженных ранее городов
    private int prevousPosition =0; //Позиция во ViewPager, на которой пользователь остановился
    private Map<Integer, String> newCitiesPages;    //Map новых добавленных городов, которые надо

    private DataAdapter dataAdapter;    //Для работы с данными

    public MyPagerAdapter(Context context, FragmentManager fm, DataAdapter da) {
        super(fm);
        this.dataAdapter = da;
        da.setPagerAdapter(this);
        this.context = context;
        pageCount = dataAdapter.getDbRawNumber();
        pageCount++;    //увеличиваем количесво страниц у ViewPager, т.к. к страницам из БД всегда добавляется страница с выбором города
    }

    //Запуск нужного фрагмента в зависимости от позиции в ViewPager
    @Override
    public Fragment getItem(int position) {
        String name = null;
        name = dataAdapter.getCityNameFromDBByNumber(position);

        if(name != null){
            return FragmentMainCurrentCityWeather.newInstance(dataAdapter, position, name);
        } else {
            return FragmentChangeCity.newInstance(dataAdapter);
        }
    }

    @Override
    public int getCount() {
        pageCount = dataAdapter.getDbRawNumber();
        pageCount++;    //увеличиваем количесво страниц у ViewPager, т.к. к страницам из БД всегда добавляется страница с выбором города
        return pageCount;
    }

    //Метод передаёт название города во фрагмент для отображения
    public void onCityChanged(String newCity) {
        Log.d(TAG, "onCityChanged: New City: " + newCity);
        CommonMethods.storeToSharedCrypto(context, CURRENT_CITY_KEY_VALUE, newCity);   //Сохранение текущего города в SharedPreferences
        dataAdapter.addNewCityToPosition(getItemPosition(this), newCity);
//        viewPager.setCurrentItem();


        this.notifyDataSetChanged();
//        fragmentMainCurrentCityWeather.updateWeatherData(newCity);
    }
}
