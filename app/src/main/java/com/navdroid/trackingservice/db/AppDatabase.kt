package com.navdroid.trackingservice.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.navdroid.trackingservice.MyApplicationClass

@Database(
    entities = [
        DeviceLocationModel::class,
    ],
    version = 5,
    exportSchema = false,
)

@TypeConverters(value = [Converters::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): DeviceLocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: MyApplicationClass): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java,
                            "ztbl.db",
                        ).allowMainThreadQueries().fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}
