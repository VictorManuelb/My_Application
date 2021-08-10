package com.example.myapplication.api

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.database.getDataBase
import com.example.myapplication.main.MainRepository

class SyncWorkManager(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "syncWorkManager"
    }
    private val database = getDataBase(appContext)
    private val repository = MainRepository(database)

    override suspend fun doWork(): Result {
        repository.fetchEarthquakes(true)

        return Result.success()
    }

}