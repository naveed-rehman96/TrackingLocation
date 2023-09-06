package com.navdroid.trackingservice.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeviceLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deviceLocationModel: DeviceLocationModel): Long

    @Query("SELECT * FROM deviceLocation WHERE date = :date AND startId=:startId AND status = -1  ORDER BY id ASC LIMIT :limit ") // -1 means not start entry nor stop entry
    fun getLcationListPerLimit(
        limit: Int,
        startId: Long,
        date: String,
    ): io.reactivex.Single<List<DeviceLocationModel>>

    @Query("SELECT * FROM deviceLocation")
    fun getAllLocation(): io.reactivex.Single<List<DeviceLocationModel>>

    @Query("DELETE  FROM deviceLocation WHERE id = :id ")
    fun deleteUploadedLocation(id: Long)

    @Query("DELETE FROM deviceLocation WHERE id = :id OR startId= :id")
    fun deleteStartStopEntry(id: Long)

    @Query("SELECT * FROM deviceLocation ORDER BY id ASC LIMIT 1")
    fun getFirstRecord(): DeviceLocationModel?

    @Query("SELECT * FROM deviceLocation WHERE startId = :startId AND status = 2 ORDER BY id ASC LIMIT 1")
    fun getFirstStopRecord(
        startId: Long,
    ): DeviceLocationModel?

    @Query("SELECT count(*) FROM deviceLocation")
    fun getCount(): Int

    @Query("SELECT id FROM deviceLocation WHERE status=1 ORDER BY id DESC LIMIT 1 ") // status 1 means start entry 2 means stop
    fun getLatestStartEntry(): Long

    @Query("DELETE FROM deviceLocation")
    fun clearTable()
}
