package com.example.myweatherapp.data

data class Data (val time: Long, val summary: String,
                 val precipIntensity : Double, val precipProbability: Float,
                 val temperature: Float, val dewPoint: Float,
                 val humidity : Float, val windSpeed: Float,
                 val uvIndex : Int , val ozone: Float) {
}