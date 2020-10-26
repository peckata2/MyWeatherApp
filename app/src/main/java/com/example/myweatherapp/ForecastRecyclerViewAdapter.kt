package com.example.myweatherapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.data.Data
import kotlinx.android.synthetic.main.layout_forecast.view.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class ForecastRecyclerViewAdapter( val hourly_forecasts: List<Data>,val temperature:String):
    RecyclerView.Adapter<ForecastRecyclerViewAdapter.ForecastViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
            return ForecastViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_forecast, parent ,false)
            )
    }

    override fun getItemCount() = hourly_forecasts.size


    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {

        val hourlyForecast = hourly_forecasts[position]
        val time = DateTimeFormatter.ISO_INSTANT
            .format(java.time.Instant.ofEpochSecond(hourlyForecast.time))
        val time2 = Instant.parse(time).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"))
        holder.view.timeTextView.text = time2
        holder.view.summaryTextView.text = hourlyForecast.summary
        holder.view.temperatureTextView.text = hourlyForecast.temperature.toString().plus(temperature)

    }

    class ForecastViewHolder(val view: View):RecyclerView.ViewHolder(view)
    {

    }
}