# Weather App

Weather App is a simple Android application that provides weather information based on the user's location or a selected city. It utilizes the OpenWeatherMap API to fetch weather data.

## Features

- Displays current weather information including temperature, description, minimum and maximum temperature, sunrise and sunset times, wind speed, pressure, and humidity.
- Automatically fetches weather data based on the user's current location.
- Allows users to search for weather information in different cities.
- Supports swipe-to-refresh functionality to manually update weather data.

## Requirements

- Android Studio
- Android device or emulator with API level 21 or higher
- Internet connection

## Getting Started

1. Clone this repository to your local machine.
2. Open the project in Android Studio.
3. Build and run the project on your Android device or emulator.

## Usage

Upon launching the app, it will attempt to fetch weather data based on your device's location. If location permission is granted, it will display weather information for your current location; otherwise, it will prompt you to enable location services or manually select a city.

You can manually search for weather information in different cities by tapping the search icon and entering the city name.

To manually refresh weather data, you can swipe down on the screen to trigger a refresh.

## Dependencies

- Retrofit: A type-safe HTTP client for Android and Java.
- Gson: A Java library for serializing and deserializing JSON objects.
- Glide: An image loading and caching library for Android.
- ViewModel and LiveData: Android Architecture Components for managing UI-related data in a lifecycle-conscious way.

