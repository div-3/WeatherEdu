package com.d.ivan.weatheredu.db;

//Описание одного кортежа (записи) в БД
public class WeatherData {

    private long id;
    private String city;
    private String country;
    private float temp;
    private float pressure;
    private float humidity;
    private float wind;

    //Геттеры и сеттеры для всех полей таблицы в БД
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getWind() {
        return wind;
    }

    public void setWind(float wind) {
        this.wind = wind;
    }

    // это нужно для ArrayAdapter, чтобы правильно отображался текст
    @Override
    public String toString() {
        return city;
    }
}
