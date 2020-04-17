package com.example.healthcare

import android.widget.Toast
import com.example.healthcare.fragments.Location
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import java.lang.Exception
import java.lang.ref.WeakReference


class LocationChangeListeningActivityLocationCallback(activity: Location?) :LocationEngineCallback<LocationEngineResult> {
    private var activityWeakReference: WeakReference<Location?>? = WeakReference(activity)


    override fun onSuccess(result: LocationEngineResult?) {
        val activity = activityWeakReference?.get()

        if (activity != null) {
            result!!.lastLocation ?: return
            if (result.lastLocation != null) {
                activity.mapboxMap.locationComponent.forceLocationUpdate(result.lastLocation)
            }
        }
    }

    override fun onFailure(exception: Exception) {

    }
}