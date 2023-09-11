package com.navdroid.trackingservice

import android.annotation.SuppressLint // ktlint-disable import-ordering
import android.app.* // ktlint-disable no-wildcard-imports
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.net.Uri
import android.os.* // ktlint-disable no-wildcard-imports
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationCallback
import com.navdroid.trackingservice.db.AppDatabase
import com.navdroid.trackingservice.db.DeviceLocationModel
// import com.readystatesoftware.chuck.internal.ui.MainActivity
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class TrackingService : Service(), IGPSActivity {

    var serviceBinder: ServiceBinder? = null
    var mLocationCallback: LocationCallback? = null
    var gps: GPS? = null

    var firstEntryExcluded = false

    companion object {
        val TRACKING_BACKUP_TIME = "18:00:00"
        const val NOTIFICATION_ID = 101
        const val MAIN_ACTION = "mainAction"
        val channelId = "location_tracking_channel"
        val channelName = "Location Tracking Service"

        /* var lat = ""
         var lng = ""
         var src = ""
 */
    }

    override fun onCreate() {
        super.onCreate()

        firstEntryExcluded = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, startMyOwnForeground())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                startMyOwnForeground(),
                FOREGROUND_SERVICE_TYPE_LOCATION,
            )
        }

        serviceBinder = ServiceBinder(this@TrackingService)
    }

    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val startTime =
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.getDefault()).format(Date())

        Log.e("LocationService", "onStartCommand: $startTime")

        gps = GPS(this@TrackingService as IGPSActivity, true)
        // findCurrentLocation()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return serviceBinder
    }

    class ServiceBinder(private val mSerice: TrackingService) : Binder() {
        fun getService(): TrackingService = mSerice
    }

    override fun onDestroy() {
        super.onDestroy()
        gps?.stopGPS()

        stopForeground(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    @RequiresApi(26)
    private fun createChannel() {
        val notifyChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH,
        )
            .apply {
                this.setSound(Uri.parse(""), audioAttributes)
            }
        val notifyManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifyManager.createNotificationChannel(notifyChannel)
    }

    fun startMyOwnForeground(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)

        notificationIntent.action = MAIN_ACTION
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        } else {
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        return notificationBuilder
            .setOngoing(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("ZTBL")
            .setContentText("Tracking Service")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setSound(null)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()
    }

    private fun checkForLocation(location: Location) {
        val speed = ((location.speed * 3600) / 1000).toInt()
//        Toast.makeText(applicationContext,"speed $speed",Toast.LENGTH_SHORT).show()
        if (location.accuracy < 50.0 && speed > 0) { // 70 to 50
            val date: String =
                SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss aa",
                    Locale.getDefault(),
                ).format(Timestamp(System.currentTimeMillis()))
            val dates = date.split(" ")

            val appDatabase = MyApplicationClass.INSTANCE?.let { AppDatabase.getInstance(it) }!!

            val locationModel = DeviceLocationModel(
                "",
                "",
                "",
                "" + location.latitude,
                "" + location.longitude,
                dates[0],
                dates[1] + " " + dates[2],
                appDatabase.locationDao().getLatestStartEntry(),
                location.speed, // raw data // ((location.speed * 3600) / 1000).toInt(), //in km/h
                location.accuracy,
            )

//          Toast.makeText(this, "Location Inserted", Toast.LENGTH_SHORT).show()
            Log.e("LocationInserted", "checkForLocation: $locationModel")
            appDatabase.locationDao().insert(deviceLocationModel = locationModel)

            val latitude = location.latitude
            val longitude = location.longitude

            Log.e("Location", "Original: $latitude && $longitude")
        }
        Toast.makeText(this, "LocationChanged", Toast.LENGTH_SHORT).show()

        Log.e("Location", "EveryTime: ${location.longitude} && ${location.latitude}")
    }

    override fun locationChanged(location: Location) {
        if (firstEntryExcluded) {
            checkForLocation(location)
        } else {
            firstEntryExcluded = true
        }
    }
}
