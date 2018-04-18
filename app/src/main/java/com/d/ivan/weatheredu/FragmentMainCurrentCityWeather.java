package com.d.ivan.weatheredu;

import android.content.Context;
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
import com.d.ivan.weatheredu.Model.CityCurrentWeatherModel;
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
    private final String CURRENT_CITY_KEY_VALUE = "CURRENT_CITY";
    private final String DEFAULT_CITY = "CURRENT_CITY";

    private static final String TAG = "FrCurrCityWeather";

    //Определяем интерфейс для связи фрагмента с активностью
    public interface OnCurrentCityChangeListener{
        public void onCityWeatherLoadError(String currentCity);
        public void updateCityDataToDB(String city, String country, float temp,float pressure,
                                       float humidity, float wind);
    }

    //Создание экземпляра интерфейса для передачи данных в callback'е активности
    OnCurrentCityChangeListener mCallback;


    //Привязка к разметке
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Загрузка ранее выбранного города или сохранённого из SharedPreferences
        if (currentCity == null) {
            String tmp = loadSharedCrypto(CURRENT_CITY_KEY_VALUE);
            if (tmp != null){
                updateWeatherData(tmp);
            }
        } else {
            updateWeatherData(currentCity);
        }
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

    //Обновление/загрузка погодных данных
    public void updateWeatherData(final String city) {

        if (!city.isEmpty()) {
            currentCity = city;

            //Сохранение названия города в SharedPreferences
            storeToSharedCrypto(CURRENT_CITY_KEY_VALUE, currentCity);

            new Thread() {//Отдельный поток для получения новых данных в фоне
                public void run() {
                    //Получение и парсинг данных от сервера в модель
                    final CityCurrentWeatherModel model = CurrentWeatherDataLoader.getCurrentWeatherByCityName(city);

                    // Вызов методов напрямую может вызвать runtime error
                    // Мы не можем напрямую обновить UI, поэтому используем handler, чтобы обновить интерфейс в главном потоке.
                    if (model == null) {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.place_not_found),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {

                        //Отрисовка информации о выбранном городе
                        handler.post(new Runnable() {
                            public void run() {
                                renderWeather(model);
                            }
                        });
                    }
                }
            }.start();
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
            tvWindValue.setText(String.valueOf(model.wind.speed));
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
//                updateWeatherData(tmp);
//            }
//        } else {updateWeatherData(currentCity);}
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    //Метод сохранения данных в указанном файле SharedPreferences по указанному ключу
    private boolean storeToSharedCrypto(String keyValue, String dataValue){

        if (keyValue.isEmpty() || dataValue.isEmpty()) return false;

        SharedPreferences sPrefs = getActivity().getSharedPreferences("FragmentMainCurrentWeatherData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putString(keyValue, dataValue);
        editor.commit();
        return true;
    }

    //Метод получения данных из указанного файла SharedPreferences по указанному ключу
    private String loadSharedCrypto(String keyValue){

        if (keyValue.isEmpty()) return null;

        SharedPreferences sPrefs = getActivity().getSharedPreferences("FragmentMainCurrentWeatherData", Context.MODE_PRIVATE);

        return sPrefs.getString(keyValue, null);
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
