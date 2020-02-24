package com.mapbox.navigation.ui.junction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver

class JunctionViewModel(
    app: Application
): AndroidViewModel(app), RoutesObserver, RouteProgressObserver {

    private var _junctionImageUrl: MutableLiveData<String> = MutableLiveData()
    val junctionImageUrl: LiveData<String>
        get() = _junctionImageUrl

    override fun onRoutesChanged(routes: List<DirectionsRoute>) {

    }

    override fun onRouteProgressChanged(routeProgress: RouteProgress) {
        _junctionImageUrl.value = "Hello World"
    }

    override fun onCleared() {
        super.onCleared()
    }
}