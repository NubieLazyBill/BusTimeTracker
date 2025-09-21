package com.example.bustimetracker

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: HistoryAdapter
    private var times = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        listView = findViewById(R.id.historyListView)
        loadData()
    }

    private fun loadData() {
        times = loadAllTimes().toMutableList()
        adapter = HistoryAdapter(this, times)
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

    private fun deleteTimeEntry(entry: String) {
        val parts = entry.split(" - ")
        if (parts.size == 2) {
            val dateKey = parts[0]

            val sharedPref = getSharedPreferences("BusTimes", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.remove(dateKey)
            editor.apply()

            Toast.makeText(this, "Запись удалена", Toast.LENGTH_SHORT).show()
        }
    }

    inner class HistoryAdapter(
        context: Context,
        private val items: MutableList<String>
    ) : ArrayAdapter<String>(context, R.layout.list_item_history, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.list_item_history, parent, false)

            val textView = view.findViewById<TextView>(R.id.dateTimeTextView)
            val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)

            textView.text = items[position]

            deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(position)
            }

            return view
        }

        private fun showDeleteConfirmationDialog(position: Int) {
            AlertDialog.Builder(context)
                .setTitle("Удаление записи")
                .setMessage("Вы уверены, что хотите удалить эту запись?")
                .setPositiveButton("Удалить") { dialog: DialogInterface, which: Int ->
                    val entryToDelete = items[position]
                    deleteTimeEntry(entryToDelete)
                    items.removeAt(position)
                    notifyDataSetChanged()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Обновляем данные при возвращении на экран
        loadData()
    }
}