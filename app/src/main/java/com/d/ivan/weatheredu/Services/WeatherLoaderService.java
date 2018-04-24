package com.d.ivan.weatheredu.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.d.ivan.weatheredu.FragmentMainCurrentCityWeather;

import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherLoaderService extends IntentService {
    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric%s";
    private static final String KEY = "&appid=54acf73196d5575a01ed032b905f8eaa";    //API-ключ для доступа к серверу http://api.openweathermap.org





    public WeatherLoaderService() {
        super("WeatherLoaderService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String city = intent.getStringExtra("City");
        if (city == null) return;
        try {
            //Формируем URL для GET-запроса от сервера
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city,KEY));

            //Создаем запрос к серверу через OkHttp
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            //Выполняем запрос и получаем ответ
            Response response = client.newCall(request).execute();

            //Вытаскиваем JSON-строку с погодными данными из ответа
            String rawCurrentWeatherData = response.body().string();

            //Если строка не нулевая, то отправляем её широковещательно
            if (!rawCurrentWeatherData.isEmpty()){
                Intent sendMSG = new Intent();
                sendMSG.setAction(FragmentMainCurrentCityWeather.BROADCAST_ACTION);
                sendMSG.putExtra(FragmentMainCurrentCityWeather.CITY, rawCurrentWeatherData);
                sendBroadcast(sendMSG);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return; //FIXME Обработка ошибки
        }

    }




}
