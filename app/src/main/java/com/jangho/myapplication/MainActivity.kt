package com.jangho.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.jangho.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btn.setOnClickListener {
            val calendarDialogFragment = CalendarDialogFragment.newInstance()
            calendarDialogFragment.setOnDateSelectListener(object : CalendarDialogFragment.OnDateSelectListener {
                override fun onSingleDateSelect(selectDate: String) {
                    binding.tv1.text = selectDate
                    binding.tv2.text = ""
                    binding.tv3.text = ""
                }

                override fun onRangeDateSelect(startDate: String, endDate: String) {
                    binding.tv1.text = ""
                    binding.tv2.text = startDate
                    binding.tv3.text = endDate
                }
            })

            calendarDialogFragment.show(supportFragmentManager, "calendar_dialog")
        }
    }
}