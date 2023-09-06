package com.navdroid.trackingservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackingServiceMine : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest = LocationRequest()
        .setInterval(1000)
        .setSmallestDisplacement(1f) // Update interval in milliseconds (e.g., 10 seconds)
        .setFastestInterval(1000) // Fastest update interval
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let {
                for (location in it.locations) {
                    // Handle the received location data (latitude and longitude)
                    checkForLocation(location)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val startTime = SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.getDefault()).format(Date())

        Log.e("LocationService", "onStartCommand: $startTime")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService()
        } else {
            startService()
        }

        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                ""
            }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Tracking Service")
            .setContentText("Tracking your location")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        requestLocationUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "location_tracking_channel"
        val channelName = "Location Tracking Service"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val notificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    private fun startService() {
        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkForLocation(location: Location) {
        val speed = ((location.speed * 3600) / 1000).toInt()
//        Toast.makeText(applicationContext,"speed $speed",Toast.LENGTH_SHORT).show()
        if (location.accuracy < 30.0 && speed > 0) { // 70 to 50
            val date: String =
                SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss aa",
                    Locale.getDefault(),
                ).format(Timestamp(System.currentTimeMillis()))
            val dates = date.split(" ")

            val latitude = location.latitude
            val longitude = location.longitude

            Log.e("Location", "Original: $latitude && $longitude")
        }
        Toast.makeText(this, "LocationChanged", Toast.LENGTH_SHORT).show()

        Log.e("Location", "EveryTime: ${location.longitude} && ${location.latitude}")
    }

}
