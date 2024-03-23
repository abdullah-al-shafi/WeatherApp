package com.example.weatherapp.retrofit;


import com.example.weatherapp.retrofit.cityWeatherResponce.CityWeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;



public interface ApiInterface {


    @GET("2.5/weather/")
    Call<CityWeatherResponse> getCityWeather(
            @Query("q")String city,
            @Query("units")String units,
            @Query("appid")String appid


    );
    @GET("2.5/weather/")
    Call<CityWeatherResponse> getCurrentLocationWeather(
            @Query("lat")String lat,
            @Query("lon")String lon,
            @Query("units")String units,
            @Query("appid")String appid


    );

}
