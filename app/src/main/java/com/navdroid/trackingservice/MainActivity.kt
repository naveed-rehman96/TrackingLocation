package com.navdroid.trackingservice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.navdroid.trackingservice.databinding.ActivityMainBinding
import com.navdroid.trackingservice.db.AppDatabase
import com.navdroid.trackingservice.db.DeviceLocationModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnStartService.setOnClickListener {
            val intent = Intent(this, TrackingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

        binding.btnFetchAllLocation.setOnClickListener {
            getTrackingDataNav()
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
                    Toast.makeText(this@MainActivity, "Records ${a.size}", Toast.LENGTH_SHORT).show()
                    appDatabase.locationDao().clearTable()
                }
            })
    }
}
