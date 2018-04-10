package com.d.ivan.weatheredu.Model;

///////////////////////////////////////////////////////////////////////////
// Класс-описание части структуры JSON-строки ответа сервера с данными о погоде
///////////////////////////////////////////////////////////////////////////

public class CityCurrentWeatherModel {
    public String name;
    public CurrentWeatherMain main;
    public CurrentWind wind;
    public long cod;
    public CurrentSys sys;
}
