package com.d.ivan.weatheredu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.d.ivan.weatheredu.db.WeatherDataSource;
import com.d.ivan.weatheredu.model.CityCurrentWeatherModel;
import com.d.ivan.weatheredu.services.WeatherLoaderService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataAdapter {
    Context context;
    WeatherDataSource dataBase;

    //Тестовые флаги
    private static final boolean serviceMode =true; //Переключатель получения данных от сервиса или непосредственно от сервера

    //Для работы с сервисом
    private BroadcastReceiver br;   //BroadCastReceiver для работы с сервисом
    public static final String BROADCAST_ACTION = "com.d.ivan.weatheredu.WEATHER_LOADER_SERVICE";
    public static final String CITY = "City";

    DataAdapter(Context context, WeatherDataSource weatherDataSource){
        this.context = context;
        this.dataBase = weatherDataSource;
    }
    public CityCurrentWeatherModel getCityModel(int pageId, String cityName){

    }


    private CityCurrentWeatherModel downloadCityModel(final String city){

        new Thread() {//Отдельный поток для получения новых данных в фоне
            public void run() {

                //Работа через непосредственный запрос к серверу
                if (!serviceMode) {
                    //Получение и парсинг данных от сервера в модель
                    final CityCurrentWeatherModel model = CurrentWeatherDataLoader.getCurrentWeatherByCityName(city);   //Получение данных из сети напрямую из приложения

                    //Обновление при работе не через сервис
                    // Вызов методов напрямую может вызвать runtime error
                    // Мы не можем напрямую обновить UI, поэтому используем handler, чтобы обновить интерфейс в главном потоке.
                    if (model == null) {
                    } else {
                    }
                }


                //Работа с сервисом для получения данных о погоде.
                if (serviceMode){

                    //Запускаем сервис
                    Intent intent = new Intent(context, WeatherLoaderService.class);  //Подготавливаем интент для подключения к сервису
                    intent.putExtra("City", city);  //передаём в интент название города
                    try {
                        context.startService(intent); //запускаем сервис
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    //Обновление интерфейса при работе через сервис будет происходить при получении данных от сервиса через BroadCastReceiver.
                    br = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String rawWeatherData = intent.getStringExtra(CITY);    //Получаем данные из BR

                            final CityCurrentWeatherModel model = parseJsonWeatherData(rawWeatherData);   //Парсинг JSON-строки с погодой в модель

                            // Отрисовка на UI новых погодных данных
                            // Вызов методов напрямую может вызвать runtime error
                            // Мы не можем напрямую обновить UI, поэтому используем handler, чтобы обновить интерфейс в главном потоке.
                            if (model == null) {
//                                handler.post(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.place_not_found),
//                                                Toast.LENGTH_LONG).show();
//                                    }
//                                });
                            } else {
//                                //Отрисовка информации о выбранном городе
//                                handler.post(new Runnable() {
//                                    public void run() {
//                                        renderWeather(model);
//                                    }
//                                });
                            }

                            //отписываемся от BroadcastReceiver
                            context.unregisterReceiver(br);

                            //Закрываем сервис
                            closeService();
                        }
                    };

                    //Создаём фильтр для BroadcastReceiver
                    IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);

                    //Регистрируем ресивер и предаём ему строку фильтра
                    context.registerReceiver(br,intentFilter);
                }
            }
        }.start();
    }


    //Метод для парсинга JSON-строки с данными о погоде от сервера в объект модели погоды города
    private CityCurrentWeatherModel parseJsonWeatherData(String rawData){
        //Преобразуем полученную JSON-строку с объект класса CityCurrentWeatherModel c помощью библиотеки GSON
        final int ALL_GOOD = 200;    //Код в ответе сервера, что всё хорошо
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CityCurrentWeatherModel model = gson.fromJson(rawData, CityCurrentWeatherModel.class);

        //Проверка, что сервер правильно понял запрос и ответил кодом "200". Все коды REST-запросов http://www.restapitutorial.com/httpstatuscodes.html
        if (model.cod != ALL_GOOD) {
            return null;
        }
        return model;
    }

    //Закрытие сервиса
    private void closeService(){
        Intent intent = new Intent(context, WeatherLoaderService.class);  //Подготавливаем интент для подключения к сервису
        try {
            context.stopService(intent); //Останавливаем сервис
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
