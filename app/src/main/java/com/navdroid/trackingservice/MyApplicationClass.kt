package com.navdroid.trackingservice

import android.app.Application

/**
 * @Author: Naveed Ur Rehman
 * @Designation: SoftwareEngineer(Android)
 * @Gmail: naveed.rehman@axabiztech.com
 * @Company: Aksa SDS
 * @Created 9/6/2023 at 3:33 PM
 */
class MyApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        var INSTANCE: MyApplicationClass? = null
    }
}
