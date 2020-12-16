package com.rubywei.realtimeupdatemap


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE = 1001;
    private val TAG = "MapActivity"
    private lateinit var mapFragment: SupportMapFragment
    private  var mLocation = LatLng(-34.0, 151.0)
    private  var marker : Marker? = null
    private lateinit var geocoder : Geocoder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            startLocation()
        } else
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE
            )
    }

    private fun startLocation() {
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingResponseTask = settingsClient.checkLocationSettings(locationSettingsRequest)
        locationSettingResponseTask.addOnSuccessListener {
            locationUpdate()

        }
            .addOnFailureListener {
            }
    }

    @SuppressLint("MissingPermission")
    private fun locationUpdate() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
//        mMap.addMarker(MarkerOptions().position(mLocation).title("Marker in Sydney"))
//            .isDraggable = true
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation))

    }
    private fun createMarker(location : Location){

        if(marker == null){
            Log.d("testing","createMarker")
            val markerOptions = MarkerOptions().position(LatLng(location.latitude,location.longitude))
                .title("RubyLearner Here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))

            marker =  mMap.addMarker(markerOptions)
        }

        marker!!.position = LatLng(location.latitude,location.longitude)
        marker!!.rotation = location.bearing
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude), 17f));
    }
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLocation = LatLng(location.latitude,location.longitude)
                createMarker(locationResult.lastLocation)
            }

        }
    }
}