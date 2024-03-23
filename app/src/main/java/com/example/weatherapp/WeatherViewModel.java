package com.example.weatherapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.retrofit.Constant;
import com.example.weatherapp.retrofit.RetrofitClient;
import com.example.weatherapp.retrofit.cityWeatherResponce.CityWeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends ViewModel {
    private MutableLiveData<CityWeatherResponse> weatherData = new MutableLiveData<>();
    // Other LiveData objects and methods...
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();


    public LiveData<CityWeatherResponse> getWeatherData() {
        return weatherData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchWeatherDataByCity(String lat, String lon, String city) {
        isLoading.setValue(true);

        Call<CityWeatherResponse> call;
        if (city !=null) {
            call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getCityWeather(city, "metric", Constant.API);
        }
        else {
            call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getCurrentLocationWeather(lat, lon,"metric", Constant.API);
        }
        call.enqueue(new Callback<CityWeatherResponse>() {
            @Override
            public void onResponse(Call<CityWeatherResponse> call, Response<CityWeatherResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    weatherData.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to fetch data");
                }
            }

            @Override
            public void onFailure(Call<CityWeatherResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network Error");
            }
        });
    }
}
