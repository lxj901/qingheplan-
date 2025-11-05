package com.com.qingheplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.com.qingheplan.R
import com.com.qingheplan.models.MeritRecord

/**
 * 功过记录列表适配器
 */
class MeritRecordsAdapter(
    private var records: List<MeritRecord>,
    private val onRecordLongClick: (MeritRecord) -> Unit
) : RecyclerView.Adapter<MeritRecordsAdapter.RecordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_merit_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount(): Int = records.size

    fun updateRecords(newRecords: List<MeritRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvType: TextView = itemView.findViewById(R.id.tvType)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        private val tvPoints: TextView = itemView.findViewById(R.id.tvPoints)

        fun bind(record: MeritRecord) {
            val isMerit = record.type == "merit"
            
            // 设置类型标记
            tvType.text = if (isMerit) "功" else "过"
            val bgDrawable = if (isMerit) {
                R.drawable.circle_merit_bg
            } else {
                R.drawable.circle_demerit_bg
            }
            tvType.setBackgroundResource(bgDrawable)
            
            // 设置标题
            tvTitle.text = record.title
            
            // 设置副标题
            tvSubtitle.text = if (isMerit) "加分" else "减分"
            
            // 设置分数
            val pointsText = if (isMerit) "+${record.points}" else "-${record.points}"
            tvPoints.text = pointsText
            val pointsColor = if (isMerit) {
                ContextCompat.getColor(itemView.context, R.color.green_primary)
            } else {
                ContextCompat.getColor(itemView.context, R.color.error_red)
            }
            tvPoints.setTextColor(pointsColor)
            
            // 设置长按事件（用于删除）
            itemView.setOnLongClickListener {
                onRecordLongClick(record)
                true
            }
        }
    }
}

