package com.com.qingheplan.models

import java.util.Date

/**
 * 功过记录数据模型
 */
data class MeritRecord(
    val id: String,
    val date: Date,
    val type: String,  // "merit" 或 "demerit"
    val title: String,
    val points: Int,
    val createdAt: Date = Date()
)

