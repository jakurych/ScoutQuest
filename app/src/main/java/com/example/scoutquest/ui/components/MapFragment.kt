/*
package com.example.scoutquest.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.scoutquest.R
import com.example.scoutquest.viewmodels.CreateNewGameViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val viewModel: CreateNewGameViewModel<Any?> by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            map.clear()
            tasks.forEach { task ->
                val location = LatLng(task.latitude, task.longitude)
                val markerIcon = getMarkerIcon(task.sequenceNumber)
                val markerOptions = MarkerOptions()
                    .position(location)
                    .title(task.name)
                    .icon(markerIcon)
                map.addMarker(markerOptions)
            }
        }
    }

    private fun getMarkerIcon(sequenceNumber: Int): BitmapDescriptor {
        val resourceId = resources.getIdentifier("marker_$sequenceNumber", "drawable", context?.packageName)
        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
*/
