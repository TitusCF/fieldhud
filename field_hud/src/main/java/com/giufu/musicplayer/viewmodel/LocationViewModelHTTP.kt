package com.giufu.musicplayer.viewmodel

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.giufu.musicplayer.MainActivity
import com.giufu.musicplayer.model.PhoneLocation
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URL

class LocationViewModelHTTP: ViewModel() {
    private var locationData = MutableLiveData<PhoneLocation>()
    private val exceptionData = MutableLiveData<Exception>()
    private val delay = 1000L*5
    private var client = OkHttpClient()
    private val sharedPref: SharedPreferences = MainActivity.sharedPreferences
    private val edit = sharedPref.edit()

    fun getLocationData() = locationData
    fun getExceptionData() = exceptionData

    private fun requestLocation(): PhoneLocation {
        val url =
            URL("${sharedPref.getString("gateway","http://192.168.1.186")}:${sharedPref.getInt("port",8080)}")
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        val responseBody = response.body()?.string()
        val jsonObj = JSONObject(responseBody)
        if (jsonObj.getInt("cod")==500) throw Exception("Server error")
        val lat = jsonObj.getString("lat")
        val lon = jsonObj.getString("lon")
        edit.putString("lat",lat)
        edit.putString("lon",lon)
        edit.commit()
        val city = jsonObj.getString("city")
        val address = jsonObj.getString("addr")
        return PhoneLocation(lat,lon,city,address)
    }

    private fun loop(){ Handler(Looper.getMainLooper()).postDelayed({updateLocation()},delay) }

    init {
        updateLocation()
    }
    private fun updateLocation(){
        AsyncTask.execute {
            try {
                locationData.postValue(requestLocation())
            }
            catch (e: Exception){
                Log.e("no location", e.toString())
                exceptionData.postValue(e)
                //locationData.value = null
                edit.putString("lat",null)
                edit.putString("lon",null)
                edit.commit()
            }
            loop()
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}