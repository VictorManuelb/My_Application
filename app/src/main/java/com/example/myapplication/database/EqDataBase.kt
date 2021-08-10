package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.Earthquake

@Database(entities = [Earthquake::class], version = 1)
abstract class EqDataBase: RoomDatabase() {
    abstract val eqDao: EqDao
}

    private lateinit var INSTANCE: EqDataBase

    fun getDataBase(application: Context): EqDataBase {
        synchronized(EqDataBase::class.java){
            if(!::INSTANCE.isInitialized){
                INSTANCE = Room.databaseBuilder(application, EqDataBase::class.java, "earthquake_db").build()
            }
            return INSTANCE
        }
    }
