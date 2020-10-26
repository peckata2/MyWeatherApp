package com.example.myweatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapp.data.ForecastItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){
    val PERMISSION_ID=42
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var loc: Location
    lateinit var locationURL: String
    lateinit var units: String
    lateinit var hourlyExclude: String
    lateinit var temperature: String


    override fun onCreate(savedInstanceState: Bundle?) {
        units= "si"
        temperature=getString(R.string.celsius)
        hourlyExclude= "currently,minutely,alerts,flags"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        HourlyRefreshLayout.setOnRefreshListener {
            fetchHourlyForecast(temperature)
        }
        getLastLocation()
        fetchHourlyForecast(temperature)



    }

    private fun fetchHourlyForecast(temperature: String) {

       if (::loc.isInitialized) {
            HourlyRefreshLayout.isRefreshing = true
            locationURL = loc.latitude.toString() + "," + loc.longitude.toString()

           RetrofitClient.instance.getHourlyForecast(locationURL,units,hourlyExclude)
               .enqueue(object : Callback<ForecastItem> {
                    override fun onFailure(call: Call<ForecastItem>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        Log.d("onFailure", "FAILURE ${t.message}")
                    }

                    override fun onResponse(
                        call: Call<ForecastItem>,
                        response: Response<ForecastItem>
                    ) {
                       HourlyRefreshLayout.isRefreshing = false
                        Log.d("onResponse", "${response.code()}+ ${response.body()}")

                        val forecasts = response.body()
                        forecasts?.let {
                            showHourlyForecast(it,temperature)
                        }
                    }

                })

        }
        else
            HourlyRefreshLayout.isRefreshing=false

    }

    private fun showHourlyForecast(forecast_item: ForecastItem,temperature:String)
    {
        ForecastRecyclerView.layoutManager=LinearLayoutManager(this)
        ForecastRecyclerView.adapter = ForecastRecyclerViewAdapter(forecast_item.hourly.data,temperature)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        if (checkPermissions()) {
            Log.d("check permissions result: ","${checkPermissions()}")
            if (isLocationEnabled()) {
                Log.d("isLocationEnabled result: ","${isLocationEnabled()}")
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        loc=requestNewLocationData()
                        Log.d("if location == null l=requestNewLocationData: ","$loc")
                        fetchHourlyForecast(temperature)

                    } else {
                        Log.d("location not null  ","$location")
                        loc=location
                        Log.d("location not null l=location: ","$loc")
                        fetchHourlyForecast(temperature)
                    }

                }

            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }

    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData():Location {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, LocationCallback(),
            Looper.myLooper()
        ).addOnCompleteListener(this) {
            mFusedLocationClient.lastLocation.result!!
        }

        return mFusedLocationClient.lastLocation.result!!
    }


    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
       )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}