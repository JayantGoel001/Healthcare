package com.example.healthcare.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper.getMainLooper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthcare.LocationChangeListeningActivityLocationCallback
import com.example.healthcare.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.net.URI
import java.net.URISyntaxException


@Suppress("DEPRECATION")
class Location : Fragment(),OnMapReadyCallback, PermissionsListener{
    private lateinit var mapView: MapView
    lateinit var mapboxMap: MapboxMap
    private var permissionsManager: PermissionsManager? = null
    private var locationEngine: LocationEngine? = null
    private val callback: LocationChangeListeningActivityLocationCallback = LocationChangeListeningActivityLocationCallback(this)
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private var mLastKnownLocation:Location?=null

    private val HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID"
    private val HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID"
    private lateinit var fab:FloatingActionButton
    private val listOfHeatmapColors: ArrayList<Expression> = ArrayList()
    private val listOfHeatmapRadiusStops: ArrayList<Expression> =ArrayList()
    private var listOfHeatmapIntensityStops: ArrayList<Float> =ArrayList()
    private var index = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        val view = inflater.inflate(R.layout.fragment_location, container, false)
        fab=view.findViewById(R.id.switch_heatmap_style_fab)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return view
    }
    @SuppressLint("LogNotTimber")
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                style -> enableLocationComponent(style)
            try {
                style.addSource(
                    GeoJsonSource(
                        HEATMAP_SOURCE_ID,
                        (requireContext().assets.open( "datacorona.json" )).read().toString()
                )
                )
            } catch (exception: URISyntaxException) {
                Log.i("TAGA",exception.toString())
            }
            initHeatmapColors()
            initHeatmapRadiusStops()
            initHeatmapIntensityStops()
            addHeatmapLayer(style)

            fab.setOnClickListener {
                index++
                if (index == listOfHeatmapColors.size - 1) {
                    index = 0
                }
                style.getLayer(HEATMAP_LAYER_ID)?.setProperties(
                    heatmapColor(listOfHeatmapColors[index]),
                    heatmapRadius(listOfHeatmapRadiusStops[index]),
                    heatmapIntensity(listOfHeatmapIntensityStops[index])
                )
            }


        }
        val uiSettings=mapboxMap.uiSettings
        uiSettings.isCompassEnabled=true


    }
    private fun enableLocationComponent(loadedMapStyle: Style) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {

            val locationComponent = mapboxMap.locationComponent

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                    .useDefaultLocationEngine(false)
                    .build()
            locationComponent.activateLocationComponent(locationComponentActivationOptions)

            locationComponent.isLocationComponentEnabled = true
            locationComponent.cameraMode = CameraMode.TRACKING
            locationComponent.renderMode = RenderMode.COMPASS
            initLocationEngine()
            mLastKnownLocation=locationComponent.lastKnownLocation
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(requireActivity())
        }
    }
    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        Toast.makeText(requireContext(), "User Location Granted", Toast.LENGTH_LONG).show()
    }
    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
        val request =
            LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()
        locationEngine!!.requestLocationUpdates(request, callback, getMainLooper())
        locationEngine!!.getLastLocation(callback)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(requireContext(), "User Location Permission Not Granted", Toast.LENGTH_LONG).show()
        }
    }
    private fun addHeatmapLayer( loadedMapStyle: Style) {
        val layer = HeatmapLayer(HEATMAP_LAYER_ID, HEATMAP_SOURCE_ID)
        layer.maxZoom = 18F
        layer.setProperties(
            heatmapColor(listOfHeatmapColors[index]),
            heatmapIntensity(listOfHeatmapIntensityStops[index]),
            heatmapRadius(listOfHeatmapRadiusStops[index]),
            heatmapOpacity(1f))
        loadedMapStyle.addLayerAbove(layer, "waterway-label")
    }
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (locationEngine != null) {
            locationEngine!!.removeLocationUpdates(callback)
        }
        mapView.onDestroy()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
    private fun initHeatmapColors() {
        listOfHeatmapColors.addAll(
            arrayListOf(interpolate(linear(), heatmapDensity(), literal(0.01), rgba(0, 0, 0, 0.01), literal(0.25), rgba(224, 176, 63, 0.5), literal(0.5), rgb(247, 252, 84), literal(0.75), rgb(186, 59, 30), literal(0.9), rgb(255, 0, 0)),
            interpolate(linear(), heatmapDensity(), literal(0.01), rgba(255, 255, 255, 0.4), literal(0.25), rgba(4, 179, 183, 1.0), literal(0.5), rgba(204, 211, 61, 1.0), literal(0.75), rgba(252, 167, 55, 1.0), literal(1), rgba(255, 78, 70, 1.0)),
            interpolate(linear(), heatmapDensity(), literal(0.01), rgba(12, 182, 253, 0.0), literal(0.25), rgba(87, 17, 229, 0.5), literal(0.5), rgba(255, 0, 0, 1.0),literal(0.75), rgba(229, 134, 15, 0.5), literal(1), rgba(230, 255, 55, 0.6)),
            interpolate(linear(), heatmapDensity(), literal(0.01), rgba(135, 255, 135, 0.2), literal(0.5), rgba(255, 99, 0, 0.5), literal(1), rgba(47, 21, 197, 0.2)),
            interpolate(linear(), heatmapDensity(), literal(0.01), rgba(4, 0, 0, 0.2), literal(0.25), rgba(229, 12, 1, 1.0), literal(0.30), rgba(244, 114, 1, 1.0), literal(0.40), rgba(255, 205, 12, 1.0), literal(0.50), rgba(255, 229, 121, 1.0), literal(1), rgba(255, 253, 244, 1.0)),
            interpolate(linear(), heatmapDensity(), literal(0.01), rgba(0, 0, 0, 0.01), literal(0.05), rgba(0, 0, 0, 0.05), literal(0.4), rgba(254, 142, 2, 0.7), literal(0.5), rgba(255, 165, 5, 0.8), literal(0.8), rgba(255, 187, 4, 0.9), literal(0.95), rgba(255, 228, 173, 0.8), literal(1), rgba(255, 253, 244, .8)),
            interpolate(linear(), heatmapDensity(), literal(0.01), rgba(0, 0, 0, 0.01), literal(0.3), rgba(82, 72, 151, 0.4), literal(0.4), rgba(138, 202, 160, 1.0), literal(0.5), rgba(246, 139, 76, 0.9), literal(0.9), rgba(252, 246, 182, 0.8), literal(1), rgba(255, 255, 255, 0.8)),
            interpolate(linear(), heatmapDensity(), literal(0.01), rgba(0, 0, 0, 0.01), literal(0.1), rgba(0, 2, 114, .1), literal(0.2), rgba(0, 6, 219, .15), literal(0.3), rgba(0, 74, 255, .2), literal(0.4), rgba(0, 202, 255, .25), literal(0.5), rgba(73, 255, 154, .3), literal(0.6), rgba(171, 255, 59, .35), literal(0.7), rgba(255, 197, 3, .4), literal(0.8), rgba(255, 82, 1, 0.7), literal(0.9), rgba(196, 0, 1, 0.8), literal(0.95), rgba(121, 0, 0, 0.8)),
            interpolate(linear(), heatmapDensity(),literal(0.01), rgba(0, 0, 0, 0.01), literal(0.1), rgba(0, 2, 114, .1), literal(0.2), rgba(0, 6, 219, .15), literal(0.3), rgba(0, 74, 255, .2), literal(0.4), rgba(0, 202, 255, .25), literal(0.5), rgba(73, 255, 154, .3), literal(0.6), rgba(171, 255, 59, .35), literal(0.7), rgba(255, 197, 3, .4), literal(0.8), rgba(255, 82, 1, 0.7), literal(0.9), rgba(196, 0, 1, 0.8), literal(0.95), rgba(121, 0, 0, 0.8)),
            interpolate(linear(), heatmapDensity(), literal(0.01), rgba(0, 0, 0, 0.01), literal(0.1), rgba(0, 2, 114, .1), literal(0.2), rgba(0, 6, 219, .15), literal(0.3), rgba(0, 74, 255, .2), literal(0.4), rgba(0, 202, 255, .25), literal(0.5), rgba(73, 255, 154, .3), literal(0.6), rgba(171, 255, 59, .35), literal(0.7), rgba(255, 197, 3, .4), literal(0.8), rgba(255, 82, 1, 0.7), literal(0.9), rgba(196, 0, 1, 0.8), literal(0.95), rgba(121, 0, 0, 0.8)),
            interpolate(linear(), heatmapDensity(),literal(0.01), rgba(0, 0, 0, 0.01), literal(0.1), rgba(0, 2, 114, .1), literal(0.2), rgba(0, 6, 219, .15), literal(0.3), rgba(0, 74, 255, .2), literal(0.4), rgba(0, 202, 255, .25), literal(0.5), rgba(73, 255, 154, .3), literal(0.6), rgba(171, 255, 59, .35), literal(0.7), rgba(255, 197, 3, .4), literal(0.8), rgba(255, 82, 1, 0.7), literal(0.9), rgba(196, 0, 1, 0.8), literal(0.95), rgba(121, 0, 0, 0.8)), interpolate(linear(), heatmapDensity(), literal(0.01), rgba(0, 0, 0, 0.25), literal(0.25), rgba(229, 12, 1, .7), literal(0.30), rgba(244, 114, 1, .7), literal(0.40), rgba(255, 205, 12, .7), literal(0.50), rgba(255, 229, 121, .8), literal(1), rgba(255, 253, 244, .8))
            )
        )
    }

    private fun initHeatmapRadiusStops() {
        listOfHeatmapRadiusStops.addAll(
            arrayListOf(
                interpolate(linear(), zoom(), literal(6), literal(50), literal(20), literal(100)),
                interpolate(linear(), zoom(), literal(12), literal(70),literal(20), literal(100)),
                interpolate(linear(), zoom(), literal(1), literal(7), literal(5), literal(50)),
                interpolate(linear(), zoom(), literal(1), literal(7), literal(5), literal(50)),
                interpolate(linear(), zoom(), literal(1), literal(7), literal(5), literal(50)),
                interpolate(linear(), zoom(), literal(1), literal(7), literal(15), literal(200)),
                interpolate(linear(), zoom(), literal(1), literal(10), literal(8), literal(70)),
                interpolate(linear(), zoom(), literal(1), literal(10), literal(8), literal(200)),
                interpolate(linear(), zoom(), literal(1), literal(10), literal(8), literal(200)),
                interpolate(linear(), zoom(), literal(1), literal(10), literal(8), literal(200)),
                interpolate(linear(), zoom(), literal(1), literal(10), literal(8), literal(200)),
                interpolate(linear(), zoom(), literal(1), literal(10), literal(8), literal(200))))
    }
    private fun initHeatmapIntensityStops() {
        listOfHeatmapIntensityStops = ArrayList(arrayListOf(
            45f,  // 1
            0.3f,  // 2
            1f,  // 3
            1f,  // 4
            1f,  // 5
            1f,  // 6
            1.5f,  // 7
            0.8f,  // 8
            0.25f,  // 9
            0.8f,  // 10
            0.25f,  // 11
            0.5f
        ))
    }
}