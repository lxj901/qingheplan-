package com.com.qingheplan.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.com.qingheplan.R
import com.com.qingheplan.adapters.CalendarAdapter
import com.com.qingheplan.adapters.MeritRecordsAdapter
import com.com.qingheplan.models.CalendarDay
import com.com.qingheplan.models.DailyScore
import com.com.qingheplan.models.MeritRecord
import java.text.SimpleDateFormat
import java.util.*

/**
 * 记录 Fragment（功过格）
 */
class RecordFragment : Fragment() {

    private lateinit var scrollView: NestedScrollView
    private lateinit var btnPrevMonth: ImageView
    private lateinit var btnNextMonth: ImageView
    private lateinit var tvMonthTitle: TextView
    private lateinit var rvCalendar: RecyclerView
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnAddRecord: ImageView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var rvRecords: RecyclerView

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var recordsAdapter: MeritRecordsAdapter

    private var currentMonth: Calendar = Calendar.getInstance()
    private var selectedDate: Calendar = Calendar.getInstance()

    // 模拟数据
    private val dailyScores = mutableMapOf<String, DailyScore>()
    private val dayRecords = mutableMapOf<String, MutableList<MeritRecord>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)

        initViews(view)
        setupCalendar()
        setupRecordsList()
        loadMockData()
        updateUI()

        return view
    }

    private fun initViews(view: View) {
        scrollView = view.findViewById(R.id.scrollView)
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth)
        btnNextMonth = view.findViewById(R.id.btnNextMonth)
        tvMonthTitle = view.findViewById(R.id.tvMonthTitle)
        rvCalendar = view.findViewById(R.id.rvCalendar)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)
        btnAddRecord = view.findViewById(R.id.btnAddRecord)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        rvRecords = view.findViewById(R.id.rvRecords)

        btnPrevMonth.setOnClickListener {
            currentMonth.add(Calendar.MONTH, -1)
            updateUI()
        }

        btnNextMonth.setOnClickListener {
            currentMonth.add(Calendar.MONTH, 1)
            updateUI()
        }

        btnAddRecord.setOnClickListener {
            // TODO: 打开添加记录页面
        }
    }

    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter(emptyList()) { day ->
            selectedDate.time = day.date
            updateUI()
        }

        rvCalendar.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = calendarAdapter
        }
    }

    private fun setupRecordsList() {
        recordsAdapter = MeritRecordsAdapter(emptyList()) { record ->
            showDeleteDialog(record)
        }

        rvRecords.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordsAdapter
        }
    }

    private fun updateUI() {
        updateMonthTitle()
        updateCalendar()
        updateRecordsList()
    }

    private fun updateMonthTitle() {
        val format = SimpleDateFormat("yyyy年M月", Locale.CHINA)
        tvMonthTitle.text = format.format(currentMonth.time)
    }

    private fun updateCalendar() {
        val days = generateCalendarDays()
        calendarAdapter.updateDays(days)
    }

    private fun updateRecordsList() {
        val dateKey = getDateKey(selectedDate)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        tvSelectedDate.text = format.format(selectedDate.time)

        val records = dayRecords[dateKey] ?: emptyList()

        if (records.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            rvRecords.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            rvRecords.visibility = View.VISIBLE
            recordsAdapter.updateRecords(records)
        }
    }



    private fun generateCalendarDays(): List<CalendarDay?> {
        val days = mutableListOf<CalendarDay?>()

        // 获取当月第一天
        val firstDay = Calendar.getInstance().apply {
            time = currentMonth.time
            set(Calendar.DAY_OF_MONTH, 1)
        }

        // 获取当月天数
        val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 获取第一天是星期几（0=周日, 1=周一, ..., 6=周六）
        val firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK) - 1

        // 添加前导空白
        repeat(firstDayOfWeek) {
            days.add(null)
        }

        // 添加当月所有日期
        val today = Calendar.getInstance()
        for (day in 1..daysInMonth) {
            val date = Calendar.getInstance().apply {
                time = currentMonth.time
                set(Calendar.DAY_OF_MONTH, day)
            }

            val dateKey = getDateKey(date)
            val score = dailyScores[dateKey]

            val isToday = isSameDay(date, today)
            val isSelected = isSameDay(date, selectedDate)

            days.add(
                CalendarDay(
                    date = date.time,
                    day = day,
                    isToday = isToday,
                    isSelected = isSelected,
                    isCurrentMonth = true,
                    dailyScore = score
                )
            )
        }

        return days
    }

    private fun getDateKey(calendar: Calendar): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return format.format(calendar.time)
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun showDeleteDialog(record: MeritRecord) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除记录")
            .setMessage("确定要删除这条记录吗？")
            .setPositiveButton("删除") { _, _ ->
                deleteRecord(record)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deleteRecord(record: MeritRecord) {
        val dateKey = getDateKey(selectedDate)
        dayRecords[dateKey]?.remove(record)

        // 重新计算当日分数
        updateDailyScore(dateKey)

        // 刷新UI
        updateUI()
    }

    private fun updateDailyScore(dateKey: String) {
        val records = dayRecords[dateKey] ?: emptyList()
        var merit = 0
        var demerit = 0

        records.forEach { record ->
            if (record.type == "merit") {
                merit += record.points
            } else {
                demerit += record.points
            }
        }

        if (merit > 0 || demerit > 0) {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(dateKey)
            dailyScores[dateKey] = DailyScore(date!!, merit, demerit)
        } else {
            dailyScores.remove(dateKey)
        }
    }

    private fun loadMockData() {
        // 生成一些模拟数据
        val today = Calendar.getInstance()

        // 今天的记录
        val todayKey = getDateKey(today)
        dayRecords[todayKey] = mutableListOf(
            MeritRecord(
                id = "1",
                date = today.time,
                type = "merit",
                title = "早起锻炼",
                points = 5
            ),
            MeritRecord(
                id = "2",
                date = today.time,
                type = "merit",
                title = "阅读30分钟",
                points = 3
            ),
            MeritRecord(
                id = "3",
                date = today.time,
                type = "demerit",
                title = "熬夜玩手机",
                points = 2
            )
        )
        updateDailyScore(todayKey)

        // 昨天的记录
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }
        val yesterdayKey = getDateKey(yesterday)
        dayRecords[yesterdayKey] = mutableListOf(
            MeritRecord(
                id = "4",
                date = yesterday.time,
                type = "merit",
                title = "帮助他人",
                points = 10
            )
        )
        updateDailyScore(yesterdayKey)

        // 前天的记录
        val dayBefore = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -2)
        }
        val dayBeforeKey = getDateKey(dayBefore)
        dayRecords[dayBeforeKey] = mutableListOf(
            MeritRecord(
                id = "5",
                date = dayBefore.time,
                type = "demerit",
                title = "发脾气",
                points = 5
            )
        )
        updateDailyScore(dayBeforeKey)
    }
}
