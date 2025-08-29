package com.example.bustimetracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var saveButton: Button
    private lateinit var historyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)
        saveButton = findViewById(R.id.saveButton)
        historyButton = findViewById(R.id.historyButton)

        // Устанавливаем текущее время (совместимый способ)
        val calendar = Calendar.getInstance()
        setTimePickerTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

        saveButton.setOnClickListener {
            saveTime()
        }

        historyButton.setOnClickListener {
            showHistory()
        }
    }

    private fun setTimePickerTime(hour: Int, minute: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.hour = hour
            timePicker.minute = minute
        } else {
            timePicker.currentHour = hour
            timePicker.currentMinute = minute
        }
    }

    private fun saveTime() {
        val hour: Int
        val minute: Int

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = timePicker.hour
            minute = timePicker.minute
        } else {
            hour = timePicker.currentHour
            minute = timePicker.currentMinute
        }

        val timeString = String.format("%02d:%02d", hour, minute)

        // Сохраняем в SharedPreferences
        val sharedPref = getSharedPreferences("BusTimes", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Используем текущую дату как ключ
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        editor.putString(dateKey, timeString)
        editor.apply()

        Toast.makeText(this, "Время сохранено: $timeString", Toast.LENGTH_SHORT).show()
    }

    private fun showHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }
}