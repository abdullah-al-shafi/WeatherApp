package com.example.weatherapp;

public class CityInfo {
    int id;
    String country;
    String name;

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public CityInfo(int id, String country, String name) {
        this.id = id;
        this.country = country;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Country: " + country + ", Name: " + name;
    }
}
