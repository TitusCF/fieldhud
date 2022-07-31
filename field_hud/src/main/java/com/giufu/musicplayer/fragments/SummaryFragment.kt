package com.giufu.musicplayer.fragments
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.IBinder
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextClock
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.giufu.musicplayer.R
import com.giufu.musicplayer.databinding.FragmentSummaryBinding
import com.giufu.musicplayer.menu.MenuActivity
import com.giufu.musicplayer.model.MusicItem
import com.giufu.musicplayer.model.Weather
import com.giufu.musicplayer.services.MusicService
import com.giufu.musicplayer.services.MusicService.LocalBinder
import com.giufu.musicplayer.viewmodel.LocationViewModelHTTP
import com.giufu.musicplayer.viewmodel.WeatherViewModel
import com.redinput.compassview.CompassView
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class SummaryFragment : BaseFragment(), MusicService.MusicServiceEvents{
    private lateinit var binding: FragmentSummaryBinding
    private lateinit var timestamp: TextClock
    private lateinit var footer: TextView
    private lateinit var compassView: CompassView
    private var sdf: SimpleDateFormat = SimpleDateFormat("EE", Locale.US)
    private lateinit var locationModel: LocationViewModelHTTP
    private lateinit var weatherModel: WeatherViewModel

    private lateinit var musicServiceIntent: Intent
    private var mService: MusicService? = null
    var mBound = false

    private lateinit var compass: Compass_2;

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val binder = iBinder as LocalBinder
            mService = binder.getService()
            mService?.registerEvents(this@SummaryFragment)
            mBound = true
        }
        override fun onServiceDisconnected(componentName: ComponentName) { mBound = false }
    }

    private fun getCompassListener(): Compass_2.CompassListener {

        return Compass_2.CompassListener { azimuth: Float ->
            run {/*
                adjustArrow(azimuth)
                adjustSotwLabel(azimuth)*/

                compassView.setDegrees(azimuth,true)
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        binding = FragmentSummaryBinding.inflate(layoutInflater)
        footer = binding.footer
        timestamp = binding.timestamp
        compassView = binding.compass

        compass = Compass_2(context)
        val cl: Compass_2.CompassListener = getCompassListener()
        compass.setListener(cl)

        weatherModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        locationModel = ViewModelProvider(this).get(LocationViewModelHTTP::class.java)

        locationModel.getLocationData().observe(viewLifecycleOwner) { i ->
            binding.footer.setTextColor(Color.WHITE)
            binding.footer.text =
                "${i.city}\n${i.addr}\n${i.lat}, ${i.lon}"
            weatherModel.getWeatherData().observe(viewLifecycleOwner) {
                updateWeatherViews(it)
            }
        }
        locationModel.getExceptionData().observe(viewLifecycleOwner){
            binding.footer.setTextColor(Color.RED)
            binding.footer.text = "Cannot retrieve location"
            binding.footer.text = it.message
            weatherModel.getExceptionData().removeObservers(viewLifecycleOwner);
        }


        musicServiceIntent = Intent(activity, MusicService::class.java)
        return binding.root
    }

    companion object {
        fun newInstance(menu: Int?): SummaryFragment {
            val myFragment = SummaryFragment()
            val args = Bundle()
            if (menu != null) { args.putInt(MENU_KEY, menu) }
            myFragment.arguments = args
            return myFragment
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateWeatherViews(weathers: List<Weather>){
        try {
            binding.currentTemperatureTv.text = weathers[0].currentTemperature+ "°"
            binding.currentHumidityTv.text = weathers[0].humidity+"%"
            binding.currentDescriptionTv.text = weathers[0].description
            binding.currentMaxMinTemperatureTv.text =
                "${weathers[0].minimumTemperature}° ${weathers[0].maximumTemperature}°"
            binding.tomorrowDay.text = sdf.format(Date(weathers[1].timestamp)).toString()
            binding.tomorrowDescription.text = weathers[1].description
            binding.tomorrowTempMax.text = weathers[1].maximumTemperature
            binding.tomorrowTempMin.text = weathers[1].minimumTemperature
            binding.tomorrowDay2.text = sdf.format(Date(weathers[2].timestamp)).toString()
            binding.tomorrowDescription2.text = weathers[2].description
            binding.tomorrowTempMax2.text = weathers[2].maximumTemperature
            binding.tomorrowTempMin2.text = weathers[2].minimumTemperature
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSingleTapUp() {
        if (arguments != null) {
            val menu = requireArguments().getInt(MENU_KEY, MENU_DEFAULT_VALUE)
            if (menu != MENU_DEFAULT_VALUE) {
                val intent = Intent(activity, MenuActivity::class.java)
                intent.putExtra(MENU_KEY, menu)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE)
            if (isMusicServiceRunning()){
                when (id) {
                    R.id.action_play_pause -> {
                        if (mService?.isPaused() == true) {
                            mService?.resumeMusic()
                        } else {
                            mService?.pauseMusic()
                        }
                    }
                    R.id.action_next_track -> {
                        mService?.nextTrack()
                    }
                    R.id.previous__track -> {
                        mService?.previousTrack()
                    }
                    R.id.action_stop_music -> {
                        mService = null
                        activity?.unbindService(serviceConnection)
                        activity?.stopService(musicServiceIntent)
                    }
                }
            }
            else{
                startMusicService()
            }
        }
    }

    private fun updateSongTextView(m: MusicItem?) {
        binding.musicTextView.text = m?.title;
    }

    private fun isMusicServiceRunning(): Boolean {
        val activityManager = activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (MusicService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @AfterPermissionGranted(123)
    private fun startMusicService() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (context?.let { EasyPermissions.hasPermissions(it, *perms) } == true) {
            musicServiceIntent.putExtra("random", "Music")
            activity?.startService(musicServiceIntent)
            val intent = Intent(activity, MusicService::class.java)
            activity?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        else {
            EasyPermissions.requestPermissions(this,
                getString(R.string.empty_string), 123, *perms)
        }
    }

    override fun onSongChanged(song: MusicItem) {
        updateSongTextView(song)
    }


}