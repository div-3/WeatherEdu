package com.d.ivan.weatheredu;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.d.ivan.weatheredu.model.CityCurrentWeatherModel;
import com.d.ivan.weatheredu.viewPager.MyPagerAdapter;


public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    FragmentMainCurrentCityWeather.OnCurrentCityChangeListener{

    private DialogFragment dialogFragment;
    private FragmentMainCurrentCityWeather fragmentMainCurrentCityWeather;
    private final static String TAG = "MainActivity";
    public static final String CURRENT_CITY_KEY_VALUE = "CURRENT_CITY";

    //Для работы с ViewPager
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    //Для работы с данными
    private DataAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataAdapter = new DataAdapter(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        dialogFragment = new FragmentChangeCity();

        //Загрузка фрагментов через ViewPager
        viewPager = findViewById(R.id.viewPagerMain);
        pagerAdapter = new MyPagerAdapter(this, getSupportFragmentManager(),dataAdapter);  //Создаём адаптер для ViewPager и передаём ему объект для работы с БД
        viewPager.setAdapter(pagerAdapter);

        //Обработка FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialogFragment.show(getSupportFragmentManager(), "changeCity");
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dataAdapter.closeDataAdapter();
        super.onDestroy();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Метод обработки ошибки загрузки данных во фрагменте.
    @Override
    public void onCityWeatherLoadError(String currentCity) {

    }

    //Метод обновления данных в БД по callback'у из фрагмента
    @Override
    public void updateCityDataToDB(String city, String country, float temp, float pressure, float humidity, float wind) {
//        weatherDataSource.updateWeather(city,country,temp,pressure,humidity,wind);
    }

    //Метод для получения данных из БД в оффлайне по последнему названию.
    @Override
    public CityCurrentWeatherModel getWeatherDataFromDBOffline(String name) {
//        return weatherDataSource.getCityWeatherDataFromDBByName(name);
       return dataAdapter.getCityModel(0,name);
    }
}
