package com.example.myapplication.main

import com.example.myapplication.Earthquake
import com.example.myapplication.api.EqJsonResponse
import com.example.myapplication.api.service
import com.example.myapplication.database.EqDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(private val dataBase: EqDataBase) {

    suspend fun fetchEarthquakes(sortByMagnitude: Boolean):MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
            val eqJsonResponse = service.getLastHourEarthquakes()
            val eqList = parseResult(eqJsonResponse)

            dataBase.eqDao.insertAll(eqList)

            fetchEarthquakesFromDb(sortByMagnitude)
        }
    }

    suspend fun fetchEarthquakesFromDb(sortByMagnitude: Boolean):MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
            if (sortByMagnitude) {
                dataBase.eqDao.getEarthquakesByMagnitude()
            } else {
                dataBase.eqDao.getEarthquakes()
            }
        }
    }

    private fun parseResult(eqJsonResponse: EqJsonResponse): MutableList<Earthquake> {
        val eqList = mutableListOf<Earthquake>()
        val features = eqJsonResponse.features

        for(feature in features){
            val properties = feature.properties
            val id = feature.id
            val mag = properties.mag
            val place = properties.place
            val time = properties.time

            val geometry = feature.geometry
            val longitude = geometry.longitude
            val latitude = geometry.latitude

            eqList.add(Earthquake(id, mag, place, time, longitude, latitude))
        }
        return eqList
    }
}