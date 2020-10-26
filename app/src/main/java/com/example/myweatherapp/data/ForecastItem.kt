package com.example.myweatherapp.data

data class ForecastItem (val latitude: Double, val longitude: Double, val timezone: String, val hourly: Hourly) {
}