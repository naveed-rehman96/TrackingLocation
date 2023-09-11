package com.navdroid.trackingservice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.navdroid.trackingservice.databinding.ActivityMainBinding
import com.navdroid.trackingservice.db.AppDatabase
import com.navdroid.trackingservice.db.DeviceLocationModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val appDatabase = MyApplicationClass.INSTANCE?.let { AppDatabase.getInstance(it) }!!

        binding.btnStartService.setOnClickListener {
            val intent = Intent(this, TrackingServiceMine::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            val date: String = SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa").format(Timestamp(System.currentTimeMillis()))
            val dates = date.split(" ")
            val locationModel = DeviceLocationModel(
                "",
                "",
                "",
                "",
                "",
                dates[0],
                dates[1] + " " + dates[2],
                0,
                0f, 0f, LocationStatusEnumerations.START.code,
            )
            SharePrefData(this).startTrackingId = appDatabase.locationDao().insert(locationModel)
        }

        binding.btnFetchAllLocation.setOnClickListener {
            val date: String =
                SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss aa",
                    Locale.getDefault(),
                ).format(Timestamp(System.currentTimeMillis()))
            val dates = date.split(" ")

            // Add Empty stop Entry
            val locationModel = DeviceLocationModel(
                "",
                "",
                "",
                "",
                "",
                dates[0],
                dates[1] + " " + dates[2],
                SharePrefData(this).startTrackingId, 0f, 0f,
                LocationStatusEnumerations.STOP.code,

            )

            var stopEntry = appDatabase.locationDao().insert(locationModel)
            SharePrefData(this).startTrackingId = 0

            val intent = Intent(this, TrackingService::class.java)
            stopService(intent)
        }
        binding.btnFetchCurrentStatus.setOnClickListener {
            val appDatabase = MyApplicationClass.INSTANCE?.let { AppDatabase.getInstance(it) }!!

            Log.e("Database", "onCreate: ${appDatabase.locationDao().getLatestStartEntry()}")
        }
    }

    fun getTrackingDataNav() {
        val appDatabase = MyApplicationClass.INSTANCE?.let { AppDatabase.getInstance(it) }!!

        val locationDao = appDatabase.locationDao()
        val locationObserver = locationDao.getAllLocation()
        locationObserver.subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(object :
                SingleObserver<List<DeviceLocationModel>> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                }

                override fun onSuccess(t: List<DeviceLocationModel>) {
                    val a = t as ArrayList
                    Toast.makeText(this@MainActivity, "Records ${a.size}", Toast.LENGTH_SHORT)
                        .show()
                    appDatabase.locationDao().clearTable()
                }
            })
    }
}
