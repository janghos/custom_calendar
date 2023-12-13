package com.jangho.myapplication

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jangho.myapplication.calendarData.Item
import com.prolificinteractive.materialcalendarview.*
import org.threeten.bp.DayOfWeek
import java.util.*


class CalendarDialogFragment : DialogFragment(), DatePickerSpinner.DatePickerListener {

    var calendarView: MaterialCalendarView? = null
    var holidayList: List<Item> = emptyList()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = context?.let { AlertDialog.Builder(it) }
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_calendar_dialog, null)

        calendarView = view.findViewById(R.id.calendar_view)

        calendarView?.setOnTitleClickListener {
            val datePickerSpinner = DatePickerSpinner()
            datePickerSpinner.setDatePickerListener(this)
            datePickerSpinner.show(parentFragmentManager, "DatePickerSpinner")
        }

        holidayList = getItemsFromSharedPreferences()


        configureCalendarView(calendarView!!)
        builder?.setView(view)
        return builder?.create() ?: throw IllegalStateException("Activity cannot be null")

    }


    private fun configureCalendarView(calendarView: MaterialCalendarView) {
        val dayDecorator = DayDecorator(requireContext())
        val todayDecorator = TodayDecorator(requireContext())
        val sundayDecorator = SundayDecorator()
        val saturdayDecorator = SaturdayDecorator()
        var selectedMonthDecorator = SelectedMonthDecorator(CalendarDay.today().month)

        // 선택된 날짜 리스너 설정 (필요한 경우)
        calendarView.setOnDateChangedListener { widget, date, selected ->
            // 선택된 날짜에 대한 작업 수행
            val selectedDate = date.date
//            Toast.makeText(
//                requireContext(),
//                "Selected Date: $selectedDate",
//                Toast.LENGTH_SHORT
//            ).show()
        }


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

    override fun onDateSelected(selectedDate: Calendar) {
        val currentlySelectedDate = calendarView?.selectedDate

        currentlySelectedDate?.let {
            calendarView?.clearSelection()
        }

        // 새로 선택된 날짜를 CalendarDay로 변환
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH) + 1
        val dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH)
        val selectedCalendarDay = CalendarDay.from(year, month, dayOfMonth)

        // CalendarView에 선택된 날짜 설정
        calendarView?.setDateSelected(selectedCalendarDay, true)
        calendarView?.setDateSelected(selectedCalendarDay, true)

        calendarView?.setCurrentDate(selectedCalendarDay.date)
    }

    private fun getItemsFromSharedPreferences(): List<Item> {
        // SharedPreferences 객체 생성
        val sharedPreferences = requireContext().getSharedPreferences("app_data", 0)

        // null 체크 추가
        return if (sharedPreferences != null) {
            // 저장된 JSON 문자열을 읽어옴
            val itemsJson = sharedPreferences.getString("holidayList", null)

            // JSON 문자열을 객체로 변환
            if (itemsJson != null) {
                Gson().fromJson(itemsJson, object : TypeToken<List<Item>>() {}.type)
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}