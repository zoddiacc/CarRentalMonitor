package com.infosys.carrentalmonitor.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class SpeedTrackingService : Service() {

    @Inject lateinit var firestore: FirebaseFirestore
    @Inject lateinit var auth: FirebaseAuth

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var maxSpeed = 60.0 // default in km/h

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    maxSpeed = doc.getDouble("maxSpeed") ?: 60.0
                    startTracking()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Speed tracking failed to start", Toast.LENGTH_SHORT).show()
                    stopSelf()
                }
        } else {
            stopSelf()
        }
    }

    private fun startTracking() {
        val request = LocationRequest.create().apply {
            interval = 3000L
            fastestInterval = 2000L
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    checkSpeed(location)
                }
            }
        }

        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationClient.requestLocationUpdates(request, locationCallback, mainLooper)
        } else {
            Toast.makeText(this, "Location permission missing", Toast.LENGTH_SHORT).show()
            stopSelf()
        }
    }

    private fun checkSpeed(location: Location) {
        val speedKmh = location.speed * 3.6
        if (speedKmh > maxSpeed) {
            Toast.makeText(this, "Speed limit exceeded: ${speedKmh.toInt()} km/h", Toast.LENGTH_SHORT).show()
            logSpeedAlert(speedKmh)
        }
    }

    private fun logSpeedAlert(speed: Double) {
        val user = auth.currentUser ?: return
        val alert = mapOf(
            "userId" to user.uid,
            "speed" to speed,
            "timestamp" to Date()
        )
        firestore.collection("alerts").add(alert)
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
