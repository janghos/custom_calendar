package com.jangho.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerSpinner  : DialogFragment() {

    interface DatePickerListener {
        fun onDateSelected(selectedDate: Calendar)
    }

    private var datePickerListener: DatePickerListener? = null

    fun setDatePickerListener(listener: DatePickerListener) {
        this.datePickerListener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_date_picker_spinner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val datePicker = view.findViewById<DatePicker>(R.id.datePicker)
        val btnApplyDate = view.findViewById<Button>(R.id.btn_apply_date)
        val btnCancelDate = view.findViewById<Button>(R.id.btn_cancel_date)

        // Set current date as the default date
        val currentDate = Calendar.getInstance()
        datePicker.init(
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH),
            null
        )

        btnApplyDate.setOnClickListener {
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month
            val selectedDayOfMonth = datePicker.dayOfMonth

            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

            datePickerListener?.onDateSelected(selectedDate)

            dismiss()
        }
        btnCancelDate.setOnClickListener {
            dismiss()
        }
    }
}