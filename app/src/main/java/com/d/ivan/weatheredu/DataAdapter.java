package com.d.ivan.weatheredu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.d.ivan.weatheredu.db.WeatherDataSource;
import com.d.ivan.weatheredu.model.CityCurrentWeatherModel;
import com.d.ivan.weatheredu.services.WeatherLoaderService;
import com.d.ivan.weatheredu.viewPager.MyPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataAdapter {
    private static final String TAG = "DataAdapter";
    Context context;
    WeatherDataSource dataBase;

    MyPagerAdapter pagerAdapter;

    CityCurrentWeatherModel modelMain;


    //Тестовые флаги
    private static final boolean serviceMode =false; //Переключатель получения данных от сервиса или непосредственно от сервера

    //Для работы с сервисом
    private BroadcastReceiver br;   //BroadCastReceiver для работы с сервисом
    public static final String BROADCAST_ACTION = "com.d.ivan.weatheredu.WEATHER_LOADER_SERVICE";
    public static final String CITY = "City";

    DataAdapter(Context context){
        this.context = context;

        //Создаём объект источника данных для БД
        this.dataBase = new WeatherDataSource(this.context);
        this.dataBase.open();
    }

    //Закрытие DataAdapter
    public void closeDataAdapter(){
        closeService();
        closeDB();
    }

    public CityCurrentWeatherModel getCityModel(int pageId, String cityName){
        CityCurrentWeatherModel tempModel;
        tempModel = downloadCityModel(cityName);
        return tempModel;
    }

    public void setModelMain(CityCurrentWeatherModel modelMain) {
        this.modelMain = modelMain;
    }

    public void setPagerAdapter(MyPagerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }

    //Метод добавления нового города в указанную позицию в БД с указанным именем
    public void addNewCityToPosition(int position, String city){
        CityCurrentWeatherModel model = this.getCityModel(dataBase.getCityCountFromDB()+1, city);
        dataBase.addWeatherData(model);
        Log.d(TAG, "addNewCityToPosition: ");
    }

    //Возвращает количество строк с городами в БД
    public int getDbRawNumber(){
        return dataBase.getCityCountFromDB();
    }

    //Возвращает название города из конкретной строки в БД
    public String getCityNameFromDBByNumber(int position){
        return dataBase.getCityNameFromDBByNumber(position);
    }

    private CityCurrentWeatherModel downloadCityModel(final String city){

        new Thread() {//Отдельный поток для получения новых данных в фоне
            public void run() {

                //Работа через непосредственный запрос к серверу
                if (!serviceMode) {
                    //Получение и парсинг данных от сервера в модель
                    final CityCurrentWeatherModel modelServer = CurrentWeatherDataLoader.getCurrentWeatherByCityName(city);   //Получение данных из сети напрямую из приложения

                    //Обновление при работе не через сервис
                    // Вызов методов напрямую может вызвать runtime error
                    // Мы не можем напрямую обновить UI, поэтому используем handler, чтобы обновить интерфейс в главном потоке.
                    if (modelServer == null) {
                    } else {
                    }
                    setModelMain(modelServer);
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

                            final CityCurrentWeatherModel modelService = parseJsonWeatherData(rawWeatherData);   //Парсинг JSON-строки с погодой в модель

                            // Отрисовка на UI новых погодных данных
                            // Вызов методов напрямую может вызвать runtime error
                            // Мы не можем напрямую обновить UI, поэтому используем handler, чтобы обновить интерфейс в главном потоке.
                            if (modelService == null) {
                            } else {
                            }
                            setModelMain(modelService);

                            //отписываемся от BroadcastReceiver
                            context.unregisterReceiver(br);

                            //Закрываем сервис
                            closeService();
                        }
                    };

                    //Создаём фильтр для BroadcastReceiver
                    IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);

                    //Регистрируем ресивер и предаём ему строку фильтра
                    context.registerReceiver(br, intentFilter);
                }
            }
        }.start();
        return this.modelMain;
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

    public void onCityChanged(String city){
        pagerAdapter.onCityChanged(city);
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

    //Закрытие базы данных
    private void closeDB(){
        this.dataBase.close();
    }


}
