package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.retrofit.Constant;
import com.example.weatherapp.retrofit.cityWeatherResponce.CityWeatherResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CityDialog.OnDialogResultListener{

    private WeatherViewModel viewModel;

    private TextView addressTextView;
    private TextView updatedAtTextView;
    private TextView statusTextView;
    private TextView tempTextView;
    private TextView tempMinTextView;
    private TextView tempMaxTextView;
    private TextView sunriseTextView;
    private TextView sunsetTextView;
    private TextView windTextView;
    private TextView pressureTextView;
    private TextView humidityTextView;
    private ProgressBar loaderProgressBar;
    private TextView errorTextView;

    private static final int PERMISSION_REQUEST_CODE = 700;

    private boolean isGPS = false;

    public static ArrayList<CityInfo> cityList = new ArrayList<>();

    ImageView search;

    private CityDialog cityDialog;

    private SwipeRefreshLayout swipeContainer;

    String CityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        addressTextView = findViewById(R.id.address);
        updatedAtTextView = findViewById(R.id.updated_at);
        statusTextView = findViewById(R.id.status);
        tempTextView = findViewById(R.id.temp);
        tempMinTextView = findViewById(R.id.temp_min);
        tempMaxTextView = findViewById(R.id.temp_max);
        sunriseTextView = findViewById(R.id.sunrise);
        sunsetTextView = findViewById(R.id.sunset);
        windTextView = findViewById(R.id.wind);
        pressureTextView = findViewById(R.id.pressure);
        humidityTextView = findViewById(R.id.humidity);
        loaderProgressBar = findViewById(R.id.loader);
        errorTextView = findViewById(R.id.errorText);
        search = findViewById(R.id.search);
        swipeContainer = findViewById(R.id.swipeContainer);

        cityDialog = new CityDialog(MainActivity.this,MainActivity.this);
        cityDialog.setOnDialogResultListener(MainActivity.this);

        // Request location permission
        requestLocationPermission();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityDialog.showDialog();

            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform refresh operation, such as fetching new data from a server
                fetchData();
            }
        });



        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        // Observe ViewModel LiveData
        observeViewModel();

        getCityLists();


    }

    private void fetchData() {
        requestLocationPermission();
        // Once the data has been fetched, call the setRefreshing(false) method to hide the refresh indicator
        swipeContainer.setRefreshing(false);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getCurrentLocation();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            // Do something with latitude and longitude
                            viewModel.fetchWeatherDataByCity(String.valueOf(latitude),
                                    String.valueOf(longitude),null);
                            // Toast.makeText(this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_LONG).show();
                        } else {
                          //  Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();

                            new GpsUtils(MainActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
                                @Override
                                public void gpsStatus(boolean isGPSEnable) {
                                    // turn on GPS
                                    isGPS = isGPSEnable;
                                }
                            });

                            initLocationUpdates();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    private void initLocationUpdates() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Create location request
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Create location callback
            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            viewModel.fetchWeatherDataByCity(String.valueOf(latitude),
                                    String.valueOf(longitude),null);

                            // Do something with latitude and longitude
                           // Toast.makeText(MainActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_LONG).show();
                        }
                        else {
                            if (CityName == null){
                                CityName = "Dhaka,bd";
                            }
                            viewModel.fetchWeatherDataByCity(null, null, CityName);
                        }
                    }
                    else {
                        if (CityName == null){
                            CityName = "Dhaka,bd";
                        }
                        viewModel.fetchWeatherDataByCity(null,null,CityName);
                    }
                }
            };

            // Request a single location update
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    private void observeViewModel() {
        // Make sure viewModel is not null before observing its LiveData
        if (viewModel != null) {
            viewModel.getWeatherData().observe(this, weatherResponse -> {
                if (weatherResponse != null) {
                    updateUI(weatherResponse);
                }
            });
            viewModel.getIsLoading().observe(this, isLoading -> {
                loaderProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            });
            viewModel.getErrorMessage().observe(this, errorMessage -> {
                if (errorMessage != null) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(errorMessage);
                }
            });
        }
    }

    private void updateUI(CityWeatherResponse weatherResponse) {
        addressTextView.setText(weatherResponse.getName() + ", " + weatherResponse.getSys().getCountry());
        long updatedAt = weatherResponse.getDt();
        String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
        updatedAtTextView.setText("Updated at: " + updatedAtText);
        statusTextView.setText(weatherResponse.getWeather().get(0).getDescription().toUpperCase());
        tempTextView.setText(weatherResponse.getMain().getTemp() + "°C");
        tempMinTextView.setText("Min Temp: " + weatherResponse.getMain().getTempMin() + "°C");
        tempMaxTextView.setText("Max Temp: " + weatherResponse.getMain().getTempMax() + "°C");
        sunriseTextView.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                .format(new Date(weatherResponse.getSys().getSunrise() * 1000)));
        sunsetTextView.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                .format(new Date(weatherResponse.getSys().getSunset() * 1000)));
        windTextView.setText(weatherResponse.getWind().getSpeed()+"");
        pressureTextView.setText(weatherResponse.getMain().getPressure()+"");
        humidityTextView.setText(weatherResponse.getMain().getHumidity()+"");
    }


    private void getCityLists() {

        try {
            JSONObject obj = new JSONObject(loadJSONFromAssetUser());
            JSONArray m_Arry = obj.getJSONArray("cities");

            cityList.clear();
            for (int a = 0; a < m_Arry.length(); a++) {
                JSONObject object = (JSONObject) m_Arry.get(a);
                int id = object.getInt("id");
                String country = object.getString("country");
                String name = object.getString("name");

                cityList.add(new CityInfo(id, country, name));
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String loadJSONFromAssetUser() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("city_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onCitySelect(String city) {
        CityName = city;
        viewModel.fetchWeatherDataByCity(null, null, city);
    }
}