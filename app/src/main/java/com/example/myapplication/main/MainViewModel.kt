package com.example.myapplication.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Earthquake
import com.example.myapplication.api.EqJsonResponseStatus
import com.example.myapplication.database.getDataBase
import kotlinx.coroutines.launch
import java.net.UnknownHostException

private val TAG = MainViewModel::class.java.simpleName
class MainViewModel(application: Application, private val sortType: Boolean) : AndroidViewModel(application) {
    private val database = getDataBase(application)
    private val repository = MainRepository(database)

    private val _eqList = MutableLiveData<MutableList<Earthquake>>()
    val eqList: LiveData<MutableList<Earthquake>>
        get() = _eqList

    private val _status = MutableLiveData<EqJsonResponseStatus>()
    val status: LiveData<EqJsonResponseStatus>
        get() = _status


    init {
        reloadEarthquakesFromDb(sortType)
    }

    private fun reloadEarthquakes() {
        viewModelScope.launch {
            try {
                _status.value = EqJsonResponseStatus.LOADING
                _eqList.value = repository.fetchEarthquakes(sortType)
                _status.value = EqJsonResponseStatus.DONE
            } catch (e: UnknownHostException) {
                _status.value = EqJsonResponseStatus.ERROR
                Log.d(TAG, "Error")
            }
        }
    }

    fun reloadEarthquakesFromDb(sortByMagnitude: Boolean) {
        viewModelScope.launch {
            _eqList.value = repository.fetchEarthquakesFromDb(sortByMagnitude)
            if (_eqList.value!!.isEmpty()) {
                reloadEarthquakes()
            }
        }
    }
}