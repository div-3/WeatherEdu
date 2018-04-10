package com.d.ivan.weatheredu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FragmentChangeCity extends DialogFragment implements View.OnClickListener {

    String[] cities = { "Irkutsk", "Moscow", "Ottawa", "Beijing", "Havana", "Prague",
            "Paris", "London", "Honolulu", "Rome", "Luxemburg" };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chang_city, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // находим список
        ListView lvMain = (ListView) getActivity().findViewById(R.id.change_city_list_view);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, cities);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

    }
}
