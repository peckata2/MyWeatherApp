package com.example.myweatherapp
import com.example.myweatherapp.data.ForecastItem
import com.example.myweatherapp.data.Hourly
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("forecast/146b09569b4683ad022c07da191e0e16/{location}")
    fun getHourlyForecast(@Path("location")location:String,
    @Query("units")units:String,
    @Query("exclude")hourlyExclude:String): Call<ForecastItem>

    companion object {
        operator fun invoke() : Api {
            return Retrofit.Builder()
                .baseUrl("https://api.darksky.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api::class.java)
        }
    }

}