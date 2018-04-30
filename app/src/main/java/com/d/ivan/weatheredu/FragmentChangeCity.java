package com.d.ivan.weatheredu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class FragmentChangeCity extends DialogFragment implements AdapterView.OnItemClickListener {

    String[] cities = { "Irkutsk", "Moscow", "Ottawa", "Beijing", "Havana", "Prague",
            "Paris", "London", "Honolulu", "Rome", "Luxemburg" };
    ListView listView;

    private static final String TAG = "FrChangeCity";

    //Определяем интерфейс для связи фрагмента с активностью
    public interface OnCityChangeListener{
        void onCityChanged(String newCity);
    }

    //Создание экземпляра интерфейса для передачи данных в callback'е активности
    OnCityChangeListener mCallback;

    //Метод создания фрагмента для ViewPager
    public static FragmentChangeCity newInstance (){
        FragmentChangeCity fragmentChangeCity = new FragmentChangeCity();
//        Bundle args = new Bundle();
//        args.putString(CURRENT_CITY_KEY_VALUE, city);
//        fragmentMainCurrentCityWeather.setArguments(args);
        return fragmentChangeCity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chang_city, container, false);
        listView = (ListView) view.findViewById(R.id.change_city_list_view);

//        getDialog().getWindow().setTitle("Chose City^");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // создаем адаптер
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, cities);

        // присваиваем адаптер списку
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnCityChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    //Callback для активности с новым выбранным городом
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        dismiss();
        Log.d(TAG, "onItemClick: Selected Item: " + adapterView.getAdapter().getItem(i).toString());
        mCallback.onCityChanged(adapterView.getAdapter().getItem(i).toString());

    }
}
