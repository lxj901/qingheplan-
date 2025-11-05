package com.com.qingheplan.models

import java.util.Date

/**
 * 每日功过分数汇总
 */
data class DailyScore(
    val date: Date,
    val merit: Int,    // 功分
    val demerit: Int   // 过分
)

