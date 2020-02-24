package com.mapbox.navigation.ui.junction

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProviders
import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.android.synthetic.main.junction_view_layout.view.*

class JunctionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LifecycleObserver {

    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var lifecycleOwner: LifecycleOwner
    private val junctionViewModel: JunctionViewModel by lazy {
        ViewModelProviders.of(getContext() as FragmentActivity).get(JunctionViewModel::class.java)
    }

    fun subscribe(lifecycleOwner: LifecycleOwner, mapboxNavigation: MapboxNavigation) {
        if (!this::mapboxNavigation.isInitialized && !this::lifecycleOwner.isInitialized) {
            this.lifecycleOwner = lifecycleOwner
            this.mapboxNavigation = mapboxNavigation
            this.lifecycleOwner.lifecycle.addObserver(this)
        }
        this.mapboxNavigation.registerRoutesObserver(junctionViewModel)
        this.mapboxNavigation.registerRouteProgressObserver(junctionViewModel)

        junctionViewModel.junctionImageUrl.observe(this.lifecycleOwner, Observer {

        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unsubscribe() {
        if (::mapboxNavigation.isInitialized) {
            mapboxNavigation.unregisterRoutesObserver(junctionViewModel)
            mapboxNavigation.unregisterRouteProgressObserver(junctionViewModel)
        }
    }

    fun show() {
        junctionView.visibility = VISIBLE
    }

    fun hide() {
        junctionView.visibility = GONE
    }
}