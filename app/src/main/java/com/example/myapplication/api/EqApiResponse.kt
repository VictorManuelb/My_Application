package com.example.myapplication.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface EqApiResponse {
    @GET("all_hour.geojson")
    suspend fun getLastHourEarthquakes(): EqJsonResponse
}

var retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

var service: EqApiResponse = retrofit.create(EqApiResponse::class.java)