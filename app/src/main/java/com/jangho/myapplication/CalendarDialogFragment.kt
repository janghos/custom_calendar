package com.jangho.myapplication

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jangho.myapplication.calendarData.Item
import com.jangho.myapplication.databinding.ActivityMainBinding
import com.jangho.myapplication.databinding.FragmentCalendarDialogBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.DayOfWeek
import java.io.IOException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


class CalendarDialogFragment : DialogFragment(), DatePickerSpinner.DatePickerListener {

    private lateinit var binding: FragmentCalendarDialogBinding

    private var isRangeSelect = false
    private var startDate = ""
    private var endDate = ""
    private var selectSingleDate = ""

    var holidayList: List<Item> = emptyList()
    lateinit var mView : View

    companion object {
        fun newInstance(): CalendarDialogFragment {
            return CalendarDialogFragment()
        }
    }

    interface OnDateSelectListener {
        fun onSingleDateSelect(selectDate : String)
        fun onRangeDateSelect(startDate : String, endDate : String)
    }

    fun setOnDateSelectListener(listener: OnDateSelectListener){
        onDateSelectListener = listener
    }

    private lateinit var onDateSelectListener : OnDateSelectListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        holidayList = readJsonFromAssets(requireContext(), "holiday_korea.json")

        val builder = AlertDialog.Builder(requireContext())
        binding = FragmentCalendarDialogBinding.inflate(layoutInflater)

        binding.calendarView.setOnTitleClickListener {
            val datePickerSpinner = DatePickerSpinner()
            datePickerSpinner.setDatePickerListener(this)
            datePickerSpinner.show(parentFragmentManager, "DatePickerSpinner")
        }

        binding.btnApplyDate.setOnClickListener {

            if(isRangeSelect){
                onDateSelectListener.onRangeDateSelect(startDate, endDate)
            } else {
                if(selectSingleDate.isEmpty()){
                    val date = Date(System.currentTimeMillis())
                    val calendar = android.icu.util.Calendar.getInstance()
                    calendar.time = date
                    val currentDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time).toString()
                    onDateSelectListener.onSingleDateSelect(currentDate)
                }else{
                    onDateSelectListener.onSingleDateSelect(selectSingleDate)
                }
            }
            dismiss()
        }

        configureCalendarView(binding.calendarView)
        builder.setView(binding.root)
        return builder.create()

    }


    private fun configureCalendarView(calendarView: MaterialCalendarView) {
        val dayDecorator = DayDecorator(requireContext())
        val todayDecorator = TodayDecorator(requireContext())
        val sundayDecorator = SundayDecorator()
        val saturdayDecorator = SaturdayDecorator()
        var selectedMonthDecorator = SelectedMonthDecorator(CalendarDay.today().month)


        calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, saturdayDecorator, selectedMonthDecorator, holidayDecorator(holidayList))

        // 좌우 화살표 가운데의 연/월이 보이는 방식 지정
        calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)
        calendarView.setTitleFormatter { day ->
            val inputText = day.date
            val calendarHeaderElements = inputText.toString().split("-")
            val calendarHeaderBuilder = StringBuilder()

            calendarHeaderBuilder.append(calendarHeaderElements[0]).append("년 ")
                .append(calendarHeaderElements[1]).append("월")

            calendarHeaderBuilder.toString()
        }

        // 캘린더에 보여지는 Month가 변경된 경우
        calendarView.setOnMonthChangedListener { widget, date ->
            // 기존에 설정되어 있던 Decorators 초기화
            calendarView.removeDecorators()
            calendarView.invalidateDecorators()

            // Decorators 추가
            selectedMonthDecorator = SelectedMonthDecorator(date.month)
            calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, saturdayDecorator, selectedMonthDecorator, holidayDecorator(holidayList))
        }

        binding.btnHoliday.setOnClickListener {
            calendarView.removeDecorators()
            calendarView.invalidateDecorators()

            if(holidayList.isEmpty()) {
                binding.btnHoliday.setText("공휴일 비활성")
                holidayList = readJsonFromAssets(requireContext(), "holiday_korea.json")
            }else {
                binding.btnHoliday.setText("공휴일 활성")
                holidayList = emptyList()
            }
            calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, saturdayDecorator, selectedMonthDecorator, holidayDecorator(holidayList))
        }

        val calendar = android.icu.util.Calendar.getInstance()

        calendarView.setOnDateChangedListener { widget, date, selected ->
            calendar.set(android.icu.util.Calendar.YEAR, date.year)
            calendar.set(android.icu.util.Calendar.MONTH, date.month-1)
            calendar.set(android.icu.util.Calendar.DATE, date.day)


            if(!selected) {
                widget.selectedDate = date
                selectSingleDate = ""
            }

            selectSingleDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time).toString()

            if(selectSingleDate.isNotEmpty()) {
                MaterialCalendarView.SELECTION_MODE_RANGE
            } else {
                MaterialCalendarView.SELECTION_MODE_SINGLE
            }
            isRangeSelect = false

        }

        calendarView.setOnRangeSelectedListener { widget, dates ->

            val calendar = android.icu.util.Calendar.getInstance()

            calendar.set(android.icu.util.Calendar.YEAR, dates[0].year)
            calendar.set(android.icu.util.Calendar.MONTH, dates[0].month-1)
            calendar.set(android.icu.util.Calendar.DATE, dates[0].day)

            startDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time).toString()

            calendar.set(android.icu.util.Calendar.YEAR, dates.last().year)
            calendar.set(android.icu.util.Calendar.MONTH, dates.last().month-1)
            calendar.set(android.icu.util.Calendar.DATE, dates.last().day)

            endDate =  SimpleDateFormat("yyyy-MM-dd").format(calendar.time).toString()

            isRangeSelect = true
        }
    }

    private fun readJsonFromAssets(context: Context, fileName: String): List<Item> {
        var items: List<Item> = emptyList()
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charset.defaultCharset())

            // Gson을 사용하여 JSON을 List<Item>으로 파싱
            items = Gson().fromJson(json, object : TypeToken<List<Item>>() {}.type)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return items
    }

    /* 선택된 날짜의 background를 설정하는 클래스 */
    private inner class DayDecorator(context: Context) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context,R.drawable.calendar_selector)
        // true를 리턴 시 모든 요일에 내가 설정한 드로어블이 적용된다
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return true
        }

        // 일자 선택 시 내가 정의한 드로어블이 적용되도록 한다
        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(drawable!!)
        }
    }

    override fun onDateSelected(selectedDate: Calendar) {

        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(selectedDate.time).toString()
        selectSingleDate = currentDate

        binding.calendarView.clearSelection()
        binding.calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_RANGE

        isRangeSelect = false

        // 새로 선택된 날짜를 CalendarDay로 변환
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH) + 1
        val dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH)
        val selectedCalendarDay = CalendarDay.from(year, month, dayOfMonth)

        // CalendarView에 선택된 날짜 설정
        binding.calendarView.setDateSelected(selectedCalendarDay, true)
        binding.calendarView.setCurrentDate(selectedCalendarDay.date)
    }

    /* 오늘 날짜의 background를 설정하는 클래스 */
    private class TodayDecorator(context: Context): DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context,R.drawable.calendar_circle_gray)
        private var date = CalendarDay.today()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }
        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable!!)
        }
    }

    /* 이번달에 속하지 않지만 캘린더에 보여지는 이전달/다음달의 일부 날짜를 설정하는 클래스 */
    private inner class SelectedMonthDecorator(val selectedMonth : Int) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.month != selectedMonth
        }
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.gray)))
        }
    }

    /* 일요일 날짜의 색상을 설정하는 클래스 */
    private class SundayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val sunday = day.date.with(DayOfWeek.SUNDAY).dayOfMonth
            return sunday == day.day
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(object:ForegroundColorSpan(Color.RED){})
        }
    }

    private class holidayDecorator(private val holidays: List<Item>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val formattedDate = "${day.year}${formatTwoDigits(day.month)}${formatTwoDigits(day.day)}"
            return holidays.any { it.locdate == formattedDate.toInt() }
        }

        override fun decorate(view: DayViewFacade) {
            // Decorate된 날짜에 대한 작업 수행 (여기서는 텍스트 색상 변경)
            view.addSpan(object:ForegroundColorSpan(Color.RED){})
        }

        private fun formatTwoDigits(value: Int): String {
            return String.format(Locale.getDefault(), "%02d", value)
        }
    }

    /* 토요일 날짜의 색상을 설정하는 클래스 */
    private class SaturdayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val saturday = day.date.with(DayOfWeek.SATURDAY).dayOfMonth
            return saturday == day.day
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(object:ForegroundColorSpan(Color.BLUE){})
        }
    }
}
