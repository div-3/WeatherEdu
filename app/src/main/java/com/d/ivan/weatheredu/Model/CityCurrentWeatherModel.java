package com.d.ivan.weatheredu.Model;

///////////////////////////////////////////////////////////////////////////
// Класс-описание части структуры JSON-строки ответа сервера с данными о погоде
///////////////////////////////////////////////////////////////////////////

import java.util.Arrays;
import java.util.List;

public class CityCurrentWeatherModel {
    public String name;
    public CurrentWeatherMain main;
    public CurrentWind wind;
    public long cod;
    public CurrentSys sys;
    public List <CurrentWeatherWeather> weather;

    public CityCurrentWeatherModel(){
        this.main = new CurrentWeatherMain();
        this.wind = new CurrentWind();
        this.sys = new CurrentSys();
        CurrentWeatherWeather cww = new CurrentWeatherWeather();
        this.weather = Arrays.asList(cww);
    }
}
