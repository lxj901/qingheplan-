package com.com.qingheplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.com.qingheplan.R
import com.com.qingheplan.models.CalendarDay
import com.google.android.material.card.MaterialCardView

/**
 * 日历网格适配器
 */
class CalendarAdapter(
    private var days: List<CalendarDay?>,
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        if (day != null) {
            holder.bind(day)
        } else {
            holder.bindEmpty()
        }
    }

    override fun getItemCount(): Int = days.size

    fun updateDays(newDays: List<CalendarDay?>) {
        days = newDays
        notifyDataSetChanged()
    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardDay: MaterialCardView = itemView.findViewById(R.id.cardDay)
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        private val viewTodayDot: View = itemView.findViewById(R.id.viewTodayDot)
        private val layoutScores: View = itemView.findViewById(R.id.layoutScores)
        private val tvMerit: TextView = itemView.findViewById(R.id.tvMerit)
        private val tvDemerit: TextView = itemView.findViewById(R.id.tvDemerit)

        fun bind(day: CalendarDay) {
            tvDay.text = day.day.toString()
            
            // 设置今日标记
            viewTodayDot.visibility = if (day.isToday) View.VISIBLE else View.GONE
            
            // 设置日期文字颜色
            val textColor = when {
                day.isToday -> ContextCompat.getColor(itemView.context, R.color.green_primary)
                !day.isCurrentMonth -> ContextCompat.getColor(itemView.context, R.color.text_secondary)
                else -> ContextCompat.getColor(itemView.context, R.color.text_primary)
            }
            tvDay.setTextColor(textColor)
            
            // 设置功过标签
            val score = day.dailyScore
            if (score != null && (score.merit > 0 || score.demerit > 0)) {
                layoutScores.visibility = View.VISIBLE
                
                if (score.merit > 0) {
                    tvMerit.visibility = View.VISIBLE
                    tvMerit.text = "功+${score.merit}"
                } else {
                    tvMerit.visibility = View.GONE
                }
                
                if (score.demerit > 0) {
                    tvDemerit.visibility = View.VISIBLE
                    tvDemerit.text = "过-${score.demerit}"
                } else {
                    tvDemerit.visibility = View.GONE
                }
            } else {
                layoutScores.visibility = View.GONE
            }
            
            // 设置选中状态
            if (day.isSelected && !day.isToday) {
                cardDay.strokeColor = ContextCompat.getColor(itemView.context, R.color.accent_gold)
                cardDay.strokeWidth = 3
            } else if (day.isToday) {
                cardDay.strokeColor = ContextCompat.getColor(itemView.context, R.color.green_primary)
                cardDay.strokeWidth = 3
            } else {
                cardDay.strokeColor = ContextCompat.getColor(itemView.context, android.R.color.transparent)
                cardDay.strokeWidth = 0
            }
            
            // 设置点击事件
            cardDay.setOnClickListener {
                onDayClick(day)
            }
        }

        fun bindEmpty() {
            cardDay.visibility = View.INVISIBLE
        }
    }
}

