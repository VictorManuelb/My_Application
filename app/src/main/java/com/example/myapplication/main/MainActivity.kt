package com.example.myapplication.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Earthquake
import com.example.myapplication.R
import com.example.myapplication.api.EqJsonResponseStatus
import com.example.myapplication.api.WorkerUtil
import com.example.myapplication.databinding.ActivityMainBinding

const val TYPE_KEY = "type_key"
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WorkerUtil.scheduleSync(this)
        val sortType = getSortType()

        binding.eqRecycler.layoutManager = LinearLayoutManager(this)
        viewModel = ViewModelProvider(this, MainViewModelFactory(application, sortType)).get(MainViewModel::class.java)

        val adapter = EqAdapter(this)
        binding.eqRecycler.adapter = adapter

        viewModel.eqList.observe(this, Observer {
            adapter.submitList(it)

            handleEmptyView(it, binding)
        })

        viewModel.status.observe(this, Observer {
            if(it == EqJsonResponseStatus.LOADING) {
                binding.loadingWheel.visibility = View.VISIBLE
            } else if(it == EqJsonResponseStatus.DONE) {
                binding.loadingWheel.visibility = View.GONE
            } else if(it == EqJsonResponseStatus.ERROR) {
                binding.loadingWheel.visibility = View.GONE
            }
        })

        adapter.onItemClickListener = {
            Toast.makeText(this, it.place, Toast.LENGTH_SHORT).show()
        }

    }

    private fun getSortType(): Boolean {
        val prefs = getPreferences(MODE_PRIVATE)
        return prefs.getBoolean(TYPE_KEY, false)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if(itemId == R.id.sort_by_magnitude) {
            viewModel.reloadEarthquakesFromDb(true)
            saveType(true)
        } else if(itemId == R.id.sort_by_time) {
            viewModel.reloadEarthquakesFromDb(false)
            saveType(false)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveType(sortByMagnitude: Boolean) {
        val prefs = getPreferences(MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(TYPE_KEY, sortByMagnitude)
        editor.apply()
    }

    private fun handleEmptyView(eqList: MutableList<Earthquake>, binding: ActivityMainBinding) {
        if (eqList.isEmpty()) {
            binding.eqEmptyView.visibility = View.VISIBLE
        } else {
            binding.eqEmptyView.visibility = View.GONE
        }
    }
}