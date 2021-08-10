package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "earthquakes")
data class Earthquake(@PrimaryKey val id: String, val magnitude: Double, val place: String, val time: Long,
                      val longitude: Double, val latitude: Double)