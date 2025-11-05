package com.com.qingheplan.models

import java.util.Date

/**
 * 日历单元格数据模型
 */
data class CalendarDay(
    val date: Date,
    val day: Int,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val isCurrentMonth: Boolean = true,
    val dailyScore: DailyScore? = null
)

