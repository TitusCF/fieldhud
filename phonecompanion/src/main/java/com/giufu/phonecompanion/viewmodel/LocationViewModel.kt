package com.giufu.phonecompanion.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@SuppressLint("MissingPermission")
class LocationViewModel(application: Application) : AndroidViewModel(application), LocationListener {

    private lateinit var location: LiveData<Location>
    private var locationData = MutableLiveData<Location>()
    private val sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()

    fun getLocationData() = locationData

    init {
        val locationManager: LocationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.NO_REQUIREMENT
        criteria.isAltitudeRequired = true;
        val providers = locationManager.getProviders(criteria, true)
        for (provider in providers) {
            locationManager.requestLocationUpdates(provider, 0, 0f, this)
            try {
                onLocationChanged(locationManager.getLastKnownLocation(provider)!!)//mmm just in case
            } catch (e: Exception){}
        }
    }


    override fun onLocationChanged(p0: Location) {
        Log.d("location", "${p0.latitude} ${p0.longitude}")
        locationData.value = p0
        editor.putString("lat",p0.latitude.toString())
        editor.putString("lon",p0.latitude.toString())
        editor.commit()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("Not yet implemented")
    }
}