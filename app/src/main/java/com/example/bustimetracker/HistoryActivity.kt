package com.example.bustimetracker

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val listView = findViewById<ListView>(R.id.historyListView)
        val times = loadAllTimes()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, times)
        listView.adapter = adapter
    }

    private fun loadAllTimes(): List<String> {
        val sharedPref = getSharedPreferences("BusTimes", Context.MODE_PRIVATE)
        val allEntries = sharedPref.all
        val result = mutableListOf<String>()

        allEntries.entries.forEach { entry ->
            if (entry.value is String) {
                result.add("${entry.key} - ${entry.value}")
            }
        }

        return result.sortedDescending()
    }
}