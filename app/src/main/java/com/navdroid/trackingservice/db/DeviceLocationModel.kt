package com.navdroid.trackingservice.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "deviceLocation", ignoredColumns = ["btsId", "btsLoc", "src"])
data class DeviceLocationModel(
    @SerializedName("BtsId") var btsId: String,
    @SerializedName("BtsLoc") var btsLoc: String,
    @SerializedName("Src") var src: String,

    @ColumnInfo
    @SerializedName("Lat")
    var lat: String,

    @ColumnInfo
    @SerializedName("Long")
    var lng: String,

    @ColumnInfo
    @SerializedName("Date")
    var date: String = "0",

    @ColumnInfo
    @SerializedName("Time")
    var time: String = "0",

    @ColumnInfo
    @SerializedName("StartId")
    var startId: Long = 0,

    @ColumnInfo
    @SerializedName("Speed")
    var speed: Float = 0f,

    @ColumnInfo
    @SerializedName("Accuracy")
    var accuracy: Float = 0f,

    @ColumnInfo
    @SerializedName("Status")
    var status: Int = -1,

) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
    )
}
