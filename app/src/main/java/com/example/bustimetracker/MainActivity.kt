package com.example.bustimetracker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.os.Environment

class MainActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var saveButton: Button
    private lateinit var historyButton: Button
    private lateinit var exportButton: Button
    private lateinit var importButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                123
            )
        }

        timePicker = findViewById(R.id.timePicker)
        saveButton = findViewById(R.id.saveButton)
        historyButton = findViewById(R.id.historyButton)
        exportButton = findViewById<Button>(R.id.exportButton)
        importButton = findViewById<Button>(R.id.importButton)

        // Устанавливаем текущее время (совместимый способ)
        val calendar = Calendar.getInstance()
        setTimePickerTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

        saveButton.setOnClickListener {
            saveTime()
        }

        historyButton.setOnClickListener {
            showHistory()
        }

        exportButton.setOnClickListener {
            showExportDialog()
        }

        importButton.setOnClickListener {
            showImportDialog()
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

    private fun exportData() {
        try {
            val sharedPref = getSharedPreferences("BusTimes", Context.MODE_PRIVATE)
            val allEntries = sharedPref.all

            // Преобразуем в JSON
            val json = Gson().toJson(allEntries)

            // Сохраняем в ПУБЛИЧНУЮ папку Downloads
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "bus_times_backup.txt")

            FileOutputStream(file).use { stream ->
                stream.write(json.toByteArray())
            }

            // Показываем путь к файлу
            val message = "Данные экспортированы в:\n${file.absolutePath}\n\nФайл доступен в папке Загрузки"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка экспорта: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importData() {
        try {
            // Ищем файл в ПУБЛИЧНОЙ папке Downloads
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "bus_times_backup.txt")

            if (file.exists()) {
                // Читаем файл
                val json = FileInputStream(file).use { stream ->
                    String(stream.readBytes())
                }

                // Парсим JSON
                val type = object : TypeToken<Map<String, String>>() {}.type
                val data = Gson().fromJson<Map<String, String>>(json, type)

                // Сохраняем в SharedPreferences
                val sharedPref = getSharedPreferences("BusTimes", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear()

                data.forEach { (key, value) ->
                    editor.putString(key, value)
                }
                editor.apply()

                Toast.makeText(this, "Данные импортированы (${data.size} записей)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Файл backup не найден в папке Загрузки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка импорта: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Добавляем диалог подтверждения
    private fun showExportDialog() {
        AlertDialog.Builder(this)
            .setTitle("Экспорт данных")
            .setMessage("Экспортировать все записи в файл?")
            .setPositiveButton("Экспорт") { dialog, which ->
                exportData()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showImportDialog() {
        AlertDialog.Builder(this)
            .setTitle("Импорт данных")
            .setMessage("Импортировать записи из файла? Текущие данные будут заменены.")
            .setPositiveButton("Импорт") { dialog, which ->
                importData()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}