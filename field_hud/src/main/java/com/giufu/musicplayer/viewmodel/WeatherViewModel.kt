package com.giufu.musicplayer.viewmodel

import android.content.SharedPreferences
import android.content.res.Resources
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.giufu.musicplayer.MainActivity
import com.giufu.musicplayer.R
import com.giufu.musicplayer.model.Weather
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception
import java.net.URL


class WeatherViewModel() : ViewModel() {
    private var weatherData = MutableLiveData<List<Weather>>()
    private val exceptionData = MutableLiveData<Exception>()
    private var checkDelay = 1000L*60//millis
    private var client = OkHttpClient()
    private val sharedPref: SharedPreferences = MainActivity.sharedPreferences
    private val resources: Resources = MainActivity.resources


    fun getWeatherData(): LiveData<List<Weather>> = weatherData
    fun getExceptionData() = exceptionData



    private fun requestWeather(): List<Weather> {
        val url = URL(
        resources.getString(R.string.forecastUrl)+
            resources.getString(R.string.appidParameter)+
            resources.getString(R.string.OpenWeatherMapApiKey)+
            resources.getString(R.string.latParameter)+
            sharedPref.getString("lat", null)+
            resources.getString(R.string.lonParameter) +
            sharedPref.getString("lon", null)+
            resources.getString(R.string.unitsParameter) +
            sharedPref.getString("units", "imperial")
        )
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        val responseBody = response.body()?.string()
        val jsonObj = JSONObject(responseBody)
        val forecasts = jsonObj.getJSONArray("daily")
        return listOf(
            parseCurrent(jsonObj),
            parseForecast(forecasts.getJSONObject(1)),
            parseForecast(forecasts.getJSONObject(2))
        )
    }

    private fun parseCurrent(jsonObj: JSONObject): Weather{
        val main = jsonObj.getJSONObject("current")//or description to be accurate
        val temp = main.getDouble("temp").toInt().toString()
        val humidity = main.getInt("humidity").toString()
        val timestamp = main.getLong("dt")/1000
        val weather = main.getJSONArray("weather").getJSONObject(0)
        val weatherDescription = weather.getString("description")
        val f = parseForecast(jsonObj.getJSONArray("daily").getJSONObject(0))
        return Weather(
            temp,
            f.minimumTemperature,
            f.maximumTemperature,
            humidity,
            timestamp,
            "",
            weatherDescription)
    }

    private fun parseForecast(main: JSONObject): Weather{
        val temps = main.getJSONObject("temp")
        val temp = temps.getDouble("day").toInt().toString()
        val tempMin = temps.getDouble("min").toInt().toString()
        val tempMax = temps.getDouble("max").toInt().toString()
        val humidity = main.getInt("humidity").toString()
        val timestamp = main.getLong("dt")*1000
        val weather = main.getJSONArray("weather").getJSONObject(0)
        val weatherDescription = weather.getString("description")
        return Weather(temp, tempMin,tempMax,humidity,timestamp,"",weatherDescription)
    }

    private fun loop(){
        Handler(Looper.getMainLooper()).postDelayed({updateWeather()},checkDelay)
    }

    private fun updateWeather(){
        AsyncTask.execute {
            try {
                weatherData.value = requestWeather()
            }
            catch (e: Exception) {
                exceptionData.value = e
            }
        }
        loop()
    }

    init {
        updateWeather()
        //mRepo = WeatherRepository.getInstance()
        //weatherData = mRepo.weathers
    }
}