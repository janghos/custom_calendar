package com.jangho.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Button
import com.google.gson.Gson
import com.jangho.myapplication.calendarData.CalApplication
import com.jangho.myapplication.calendarData.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn).setOnClickListener {
            val calendarDialogFragment = CalendarDialogFragment()
            calendarDialogFragment.show(supportFragmentManager, "calendar_dialog")
        }

        val calApi = CalApplication.getInstance().getService()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allItems = mutableListOf<Item>()

                for (year in 2004..2025) {
                    val response = calApi.getRest(
                        serviceKey = "cXfXHgUCAiFycnhIOQbzT35mE7ZWWAeZmkYlD0ekUI6SDJ2oGvHagJ7AFdgbDQTQsx21PEKdYMArXRxJHtiktQ==",
                        solYear = year.toString(),
                        _type = "json",
                        pageNo = "1",
                        numOfRows = "50"
                    )

                    if (response.isSuccessful) {
                        val itemList = response.body()?.response?.body?.items?.item
                        itemList?.let { items ->
                            allItems.addAll(items)
                        }
                    } else {
                        // 에러 처리
                    }
                }
                saveToSharedPreferences(allItems)
            } catch (e: Exception) {
                // 예외 처리
                Log.e("예외", "Error: ${e.message}")
            }
        }
    }


    private fun saveToSharedPreferences(items: List<Item>) {
        val sharedPreferences = getSharedPreferences("app_data",0)
        val editor = sharedPreferences.edit()

        val itemsJson = Gson().toJson(items)
        editor.putString("holidayList", itemsJson)
        editor.apply()
    }
}