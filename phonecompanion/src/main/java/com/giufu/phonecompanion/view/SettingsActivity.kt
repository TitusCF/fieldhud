package com.giufu.phonecompanion.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.giufu.phonecompanion.LocationServiceOld
import com.giufu.phonecompanion.R
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.res.Resources
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.giufu.phonecompanion.Util
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity(){

    private lateinit var portEditText: EditText
    private lateinit var statusTextView: TextView
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var serverSwitch: Switch
    private lateinit var logsTextView: TextView
    private var mLocationService: LocationServiceOld = LocationServiceOld()
    private val sdf = SimpleDateFormat("HH:mm")

    private fun log(message: String){
        val editor = sharedPreferences.edit()
        val now = sdf.format(Date())
        editor.putString("log","$now - $message\n${sharedPreferences.getString("log","")}")
        editor.apply()
    }

    private fun getLog() = sharedPreferences.getString("log", "")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val serviceIntent = Intent(this, LocationServiceOld().javaClass )
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                    .setTitle("ACCESS_FINE_LOCATION")
                    .setMessage("Location permission required")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        requestFineLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                requestFineLocationPermission()
            }
        }
        portEditText = findViewById(R.id.port_edit_text)
        statusTextView = findViewById(R.id.status_textview)
        serverSwitch = findViewById(R.id.server_switch)
        logsTextView = findViewById(R.id.logs_textview)

        if (Util.isMyServiceRunning(mLocationService.javaClass, this)){
            serverSwitch.isChecked = true
            statusTextView.text = "STARTED"
            statusTextView.setTextColor(Color.GREEN)
        }
        else{
            statusTextView.text = "STOPPED"
            statusTextView.setTextColor(Color.RED)
        }
        serverSwitch.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked){
                try {
                    val port = portEditText.text.toString().toInt()
                    serviceIntent.putExtra("port", port)
                    startService(serviceIntent)
                    log("Server started on port $port")
                    statusTextView.text = "STARTED"
                    statusTextView.setTextColor(Color.GREEN)
                } catch (ioe: Exception) {
                    //log("The server could not start")
                    statusTextView.text = "ERROR"
                    statusTextView.setTextColor(Color.RED)
                }
            }
            else{
                stopService(serviceIntent)
                log("Server stopped")
                statusTextView.text = "STOPPED"
                statusTextView.setTextColor(Color.RED)
            }
            logsTextView.text = getLog()
        }
        sharedPreferences = getSharedPreferences("logs", MODE_PRIVATE)
    }

    private fun requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), MY_BACKGROUND_LOCATION_REQUEST)
    }

    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,), MY_FINE_LOCATION_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Toast.makeText(this, requestCode.toString(), Toast.LENGTH_LONG).show()
        when (requestCode) {
            MY_FINE_LOCATION_REQUEST -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        requestBackgroundLocationPermission()
                    }

                } else {
                    Toast.makeText(this, "ACCESS_FINE_LOCATION permission denied", Toast.LENGTH_LONG).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", this.packageName, null),),)
                    }
                }
                return
            }
            MY_BACKGROUND_LOCATION_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Background location Permission Granted", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Background location permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
    companion object {
        private const val MY_FINE_LOCATION_REQUEST = 99
        private const val MY_BACKGROUND_LOCATION_REQUEST = 100


        lateinit var resources: Resources
        lateinit var sharedPreferences: SharedPreferences
    }
}