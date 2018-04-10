package com.d.ivan.weatheredu;

///////////////////////////////////////////////////////////////////////////
// Вспомогательный класс для получения данных с текущей погодой
// с сайта api.openweathermap.org по API.
// Для GET запросов используется библиотека OkHttp
///////////////////////////////////////////////////////////////////////////

import android.content.Context;
import android.util.Log;

import com.d.ivan.weatheredu.Model.CityCurrentWeatherModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URL;

//Библиотека OkHttp
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrentWeatherDataLoader {
    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric%s";
    private static final String KEY = "&appid=54acf73196d5575a01ed032b905f8eaa";    //API-ключ для доступа к серверу http://api.openweathermap.org
    private static final int ALL_GOOD = 200;    //Код в ответе сервера, что всё хорошо

    private static final String TAG = "CurrentWeatherDataLdr";


    //Метод для получения данных от сервера
    static CityCurrentWeatherModel getCurrentWeatherByCityName(Context context, String city) {
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
            Log.d(TAG, "getCurrentWeatherByCityName: " + rawCurrentWeatherData);

            //Преобразуем полученную JSON-строку с объект класса CityCurrentWeatherModel c помощью библиотеки GSON
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            CityCurrentWeatherModel model = gson.fromJson(rawCurrentWeatherData, CityCurrentWeatherModel.class);
            Log.v(TAG, "getCurrentWeatherByCityName: model.name: " + model.name + ", temp: " + model.main.temp + ", wind: " + model.wind.speed);

            //Проверка, что сервер правильно понял запрос и ответил кодом "200". Все коды REST-запросов http://www.restapitutorial.com/httpstatuscodes.html
            if (model.cod != ALL_GOOD) {
                return null;
            }
            return model;

            //Обработка, если получили ошибку при запросе.
        } catch (Exception e) {
            e.printStackTrace();
            return null; //FIXME Обработка ошибки
        }
    }
}
