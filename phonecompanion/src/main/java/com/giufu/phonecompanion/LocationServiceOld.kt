package com.giufu.phonecompanion

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProviders
import com.giufu.phonecompanion.view.SettingsActivity
import com.giufu.phonecompanion.viewmodel.LocationViewModel
import fi.iki.elonen.NanoHTTPD
import java.text.SimpleDateFormat

class LocationServiceOld : Service(), LocationListener{
    private lateinit var server: WebServer
    var lastLocation: Location? = null;
    private lateinit var locationManager: LocationManager
    //var sharedLogs: SharedPreferences = getSharedPreferences("logs", Context.MODE_PRIVATE);
    private val sdf = SimpleDateFormat("HH:mm")
    private lateinit var coder: Geocoder

    override fun onCreate() {
        super.onCreate()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val inputPort = intent.getIntExtra("port",8080)
        val notificationIntent = Intent(this, SettingsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setContentTitle("Glass location service")
            .setContentText("Serving on port $inputPort")
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
        coder= Geocoder(this)
        server = WebServer(inputPort)
        server.start()


        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
        locationManager.removeUpdates(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(p0: Location) {
        lastLocation=p0
    }

    private inner class WebServer(port: Int) : NanoHTTPD(port) {
        override fun serve(session: IHTTPSession): Response {


            val answer =try {
                val place = coder.getFromLocation(lastLocation!!.latitude,lastLocation!!.longitude,10)[0]
                val lat = lastLocation!!.latitude.toString().take(9)
                var lon = lastLocation!!.longitude.toString().take(9)
                var city = place.locality
                val addr = "${place.thoroughfare},${place.subThoroughfare}"
                "{\"cod\":\"200\",\"lat\":\"$lat\",\"lon\":\"$lon\",\"city\":\"$city\",\"addr\":\"$addr\"}"
            }
            catch (e: Exception){ "{\"cod\":\"500\"}"}
            return newFixedLengthResponse(answer)
        }
    }

}