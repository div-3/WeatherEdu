package com.d.ivan.weatheredu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentMainCurrentCityWeather extends Fragment {

    //Определяем интерфейс для связи фрагмента с активностью
    public interface OnCurrentCityChangeListener{
        public void onCityWeatherLoadError(String currentCity);
    }

    OnCurrentCityChangeListener mCallback;  //Создание экземпляра интерфейса для передачи данных в callback'е активности


    //Привязка к разметке
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_current_city_weather, container, false);
    }


    //Проверка, что активность переопределила метод из интерфейса фрагмента
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnCurrentCityChangeListener) context;
        } catch (ClassCastException cce){
            throw new ClassCastException(context.toString() + " must implement OnCurrentCityChangeListener");
        }
    }

    //Callback для активности, если что-то произошло при загрузке данных
    public void smthWrongHappend(String currentCity){
        mCallback.onCityWeatherLoadError(currentCity);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Получение данных от погодного сервера https://openweathermap.org/api
    ///////////////////////////////////////////////////////////////////////////


}
