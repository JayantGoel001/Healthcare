package com.example.healthcare.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper.getMainLooper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthcare.LocationChangeListeningActivityLocationCallback
import com.example.healthcare.R
import com.google.android.material.button.MaterialButton
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class Location : Fragment(),OnMapReadyCallback, PermissionsListener,MapboxMap.OnMapClickListener{
    private lateinit var mapView: MapView
    lateinit var mapboxMap: MapboxMap
    private var permissionsManager: PermissionsManager? = null
    private var locationEngine: LocationEngine? = null
    private val callback: LocationChangeListeningActivityLocationCallback = LocationChangeListeningActivityLocationCallback(this)
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private lateinit var startButton:Button
    private lateinit var originPosition: Point
    lateinit var destinationPosition:Point
    lateinit var destinationMarker:Marker
    private var originLocation:Location?=null
    private var navigationMapRouter:NavigationMapRoute?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        val view = inflater.inflate(R.layout.fragment_location, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        startButton=view.findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val option=NavigationLauncherOptions.builder()
                .shouldSimulateRoute(true)
                .build()
            NavigationLauncher.startNavigation(requireActivity(),option)
        }
        return view
    }
    fun getRoute(origin:Point,destination:Point)
    {
        NavigationRoute.builder(requireContext()).accessToken(Mapbox.getAccessToken()!!)
            .origin(origin)
            .destination(destination)
            .build()
            .getRoute(object :Callback<DirectionsResponse>
            {
                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {

                }

                @SuppressLint("LogNotTimber")
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    val routeResponse=response?:return
                    val body=routeResponse.body()?:return
                    if(body.routes().count()==0)
                    {
                        Log.i("HelloMap","No Route Found")
                        return
                    }
                    if(navigationMapRouter!=null)
                    {
                        navigationMapRouter?.removeRoute()
                    }
                    else
                    {
                        navigationMapRouter= NavigationMapRoute(null,mapView,mapboxMap)
                    }
                    navigationMapRouter?.addRoute(body.routes().first())
                }

            })
    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                style -> enableLocationComponent(style)
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
            originLocation=locationComponent.lastKnownLocation
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
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

    override fun onMapClick(point: LatLng): Boolean {
        Toast.makeText(requireContext(),"${point.latitude} && ${point.longitude}",Toast.LENGTH_LONG).show()
        destinationMarker.let {
            mapboxMap.removeMarker(it)
        }
        val markerOption=MarkerOptions()
        destinationMarker=mapboxMap.addMarker(markerOption.position(point))
        destinationPosition= Point.fromLngLat(point.longitude,point.latitude)
        originPosition= Point.fromLngLat(originLocation!!.longitude,originLocation!!.latitude)
        getRoute(originPosition,destinationPosition)

        startButton.isEnabled=true
        startButton.setBackgroundResource(R.color.background)
        return true
    }
}