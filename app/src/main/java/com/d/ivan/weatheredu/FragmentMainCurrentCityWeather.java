package com.d.ivan.weatheredu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.d.ivan.weatheredu.commonMethods.CommonMethods;
import com.d.ivan.weatheredu.model.CityCurrentWeatherModel;
import com.d.ivan.weatheredu.services.WeatherLoaderService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

// Взаимодействие с родительской активити через callback интерфейс.
// Туториал Communicating with Other Fragments https://developer.android.com/training/basics/fragments/communicating.html

public class FragmentMainCurrentCityWeather extends Fragment {

    // Handler - это класс, позволяющий отправлять и обрабатывать сообщения и объекты runnable. Он используется в двух
    // случаях - когда нужно применить объект runnable когда-то в будущем, и, когда необходимо передать другому потоку
    // выполнение какого-то метода. Второй случай наш.
    private final Handler handler = new Handler();


    //Вьюшки
    private ImageView iv;
    private TextView tvCity;
    private TextView tvCurrentTempValue;
    private TextView tvPressureValue;
    private TextView tvHumidityValue;
    private TextView tvWindValue;

    private String currentCity;

    private final String DEFAULT_CITY = "CURRENT_CITY";




    private static final String TAG = "FrCurrCityWeather";

    //Определяем интерфейс для связи фрагмента с активностью
    public interface OnCurrentCityChangeListener{
        public void onCityWeatherLoadError(String currentCity);
        public void updateCityDataToDB(String city, String country, float temp,float pressure,
                                       float humidity, float wind);
        public CityCurrentWeatherModel getWeatherDataFromDBOffline(String name);
    }

    //Создание экземпляра интерфейса для передачи данных в callback'е активности
    OnCurrentCityChangeListener mCallback;

    //Метод создания фрагмента для ViewPager
    public static FragmentMainCurrentCityWeather newInstance (String city){
        FragmentMainCurrentCityWeather fragmentMainCurrentCityWeather = new FragmentMainCurrentCityWeather();
        Bundle args = new Bundle();
        args.putString(MainActivity.CURRENT_CITY_KEY_VALUE, city);
        fragmentMainCurrentCityWeather.setArguments(args);
        return fragmentMainCurrentCityWeather;
    }

    //Привязка к разметке
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        //Загрузка ранее выбранного города или сохранённого из SharedPreferences
//        if (currentCity == null) {
//            String tmp = loadSharedCrypto(MainActivity.CURRENT_CITY_KEY_VALUE);
//            if (tmp != null){
//                CityCurrentWeatherModel model = mCallback.getWeatherDataFromDBOffline(tmp);
//                renderWeather(model);
////                updateWeatherData(tmp);
//            }
//        } else {
//            updateWeatherData(currentCity);
//        }
        //Получение города, переданного от ViewPager
        if (getArguments().getString(MainActivity.CURRENT_CITY_KEY_VALUE) != null) {
            currentCity = getArguments().getString(MainActivity.CURRENT_CITY_KEY_VALUE);
        }
        return inflater.inflate(R.layout.fragment_main_current_city_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iv = (ImageView) getActivity().findViewById(R.id.ivIco);
        tvCity = (TextView) getActivity().findViewById(R.id.tvCity);
        tvCurrentTempValue = (TextView) getActivity().findViewById(R.id.tvCurrentTempValue);
        tvPressureValue = (TextView) getActivity().findViewById(R.id.tvPressureValue);
        tvHumidityValue = (TextView) getActivity().findViewById(R.id.tvHumidityValue);
        tvWindValue = (TextView) getActivity().findViewById(R.id.tvWindValue);

//        //Загрузка ранее выбранного города или сохранённого из SharedPreferences
//        if (currentCity == null) {
//            String tmp = loadSharedCrypto(CURRENT_CITY_KEY_VALUE);
//            if (tmp != null){
//                CityCurrentWeatherModel model = mCallback.getWeatherDataFromDBOffline(tmp);
//                renderWeather(model);
////                updateWeatherData(tmp);
//            }
//        } else {
//            updateWeatherData(currentCity);
//        }

        //Загрузка города, переданного от ViewPager
        if (currentCity != null) {
            CityCurrentWeatherModel model = mCallback.getWeatherDataFromDBOffline(currentCity); //Поиск данных города в БД
            if(model != null){
                renderWeather(model);   //Если нашли базе, то выводим инфу из БД
            } else {
                updateWeatherData(currentCity);     //Если не нашли в базе, то запрашиваем от сервака.
            }
        }

//        //Загрузка ранее выбранного города или сохранённого из SharedPreferences
//        if (currentCity == null) {
//            String tmp = CommonMethods.loadSharedCrypto(getActivity(), CURRENT_CITY_KEY_VALUE);
//            if (tmp != null){
//                updateWeatherData(tmp);
//            }
//        } else {updateWeatherData(currentCity);}
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    @Override
    public void onPause() {
        super.onPause();
    }

    //Callback для активности, если что-то произошло при загрузке данных
    public void smthWrongHappend(String currentCity){
        mCallback.onCityWeatherLoadError(currentCity);
    }

    //Обновление/загрузка погодных данных
    public void updateWeatherData(final String city) {

        if (!city.isEmpty()) {
//            currentCity = city;

            //Сохранение названия города в SharedPreferences
//            CommonMethods.storeToSharedCrypto(getActivity(), MainActivity.CURRENT_CITY_KEY_VALUE, currentCity);

            if (model == null) {
//                                handler.post(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.place_not_found),
//                                                Toast.LENGTH_LONG).show();
//                                    }
//                                });
            } else {
                //Отрисовка информации о выбранном городе
                handler.post(new Runnable() {
                    public void run() {
                        renderWeather(model);
                    }
                });
            }
        }
    }

    //Обработка загруженных данных и обновление UI
    private void renderWeather(CityCurrentWeatherModel model) {
        try {
            tvCity.setText(model.name.toUpperCase(Locale.US) + ", " + model.sys.country);

//            String description = "";
//            long id = 0;
//            if(model.weather.size() != 0){
//                description = model.weather.get(0).description.toUpperCase(Locale.US);
//                id =model.weather.get(0).id;
//            }

            tvPressureValue.setText(String.valueOf(model.main.pressure));
            tvHumidityValue.setText(String.valueOf(model.main.humidity));
//            tvWindValue.setText(String.valueOf(model.wind.speed));
            tvWindValue.setText(String.format("%.1f", model.wind.speed));
            tvCurrentTempValue.setText(String.format("%.1f", model.main.temp));

//            DateFormat df = DateFormat.getDateTimeInstance();
//            String updatedOn = df.format(new Date(model.dt * 1000));    //Точное время обновления информации от сервера
//            updatedTextView.setText("Last update: " + updatedOn);
            if (model.weather.get(0).icon != null) {
                setWeatherIcon(model.weather.get(0).icon);
            }

            //Запись данных о городе в БД через callback
            mCallback.updateCityDataToDB(model.name,
                                        model.sys.country,
                                        (float) model.main.temp,
                                        (float) model.main.pressure,
                                        (float)model.main.humidity,
                                        (float) model.wind.speed);

        } catch (Exception e) {
            e.printStackTrace();
             Log.d(TAG, "One or more fields not found in the JSON data");//FIXME Обработка ошибки
        }

    }

    //Вывод иконки погоды с сайта в ImageView с помощью библиотеки Glide
    private boolean setWeatherIcon(String ico){

        if (!ico.isEmpty()){
            String tempURL = "http://openweathermap.org/img/w/" + ico + ".png";
            Glide.with(getActivity().getBaseContext())
                    .load(tempURL)
                    .into(iv);
        }
        return false;
    }




}
