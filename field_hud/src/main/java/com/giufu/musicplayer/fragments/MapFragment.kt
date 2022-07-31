package com.giufu.musicplayer.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProviders
import com.giufu.musicplayer.R
import com.giufu.musicplayer.menu.MenuActivity
import com.giufu.musicplayer.model.PhoneLocation
import com.giufu.musicplayer.viewmodel.LocationViewModelHTTP
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import kotlin.random.Random


class MapFragment : BaseFragment(), OnMapReadyCallback{
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var locationModel: LocationViewModelHTTP

    var lat = 45.802553
    var lon = 14.5234244

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(requireContext().applicationContext, getString(R.string.MapboxApiKey))
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return view
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setStyle(Style.Builder().fromUrl("mapbox://styles/mapbox/dark-v10"))
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(lat, lon))
            .zoom(15.0)
            .build()
        map.uiSettings.isZoomGesturesEnabled = false
        map.uiSettings.isScrollGesturesEnabled = false
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun setCameraPosition(lat: Double, lon: Double){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(lat,lon),13.0))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        locationModel = ViewModelProviders.of(this).get(LocationViewModelHTTP::class.java)
        locationModel.getLocationData().observe(this) {
            setCameraPosition(it.lat.toDouble(),it.lon.toDouble())
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    companion object {
        private const val BODY_TEXT_SIZE = 40
        fun newInstance(menu: Int?): MapFragment {
            val myFragment = MapFragment()
            val args = Bundle()
            if (menu != null) {
                args.putInt(MENU_KEY, menu)
            }
            myFragment.arguments = args
            return myFragment
        }
    }

    override fun onSingleTapUp() {
        //super.onSingleTapUp();
        if (arguments != null) {
            val menu = requireArguments().getInt(MENU_KEY, MENU_DEFAULT_VALUE)
            if (menu != MENU_DEFAULT_VALUE) {
                val intent = Intent(activity, MenuActivity::class.java)
                intent.putExtra(MENU_KEY, menu)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getIntExtra(
                MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE)
            when(id){
                R.id.map_random -> {
                    lon += 0.01
                    setCameraPosition(lat,lon)
                }
            }
        }
    }
}