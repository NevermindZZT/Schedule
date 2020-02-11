package com.letter.schedule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.letter.schedule.R
import com.letter.schedule.course.CourseTime

/**
 * 课表时间适配器
 * @property courseTimeList List<CourseTime>? 课表时间列表
 * @property context Context? context
 * @property onItemClickListener Function2<[@kotlin.ParameterName] Adapter<ViewHolder>, [@kotlin.ParameterName] Int, Unit>?
 *           列表项单机处理监听
 * @property onItemLongClickListener Function2<[@kotlin.ParameterName] Adapter<ViewHolder>, [@kotlin.ParameterName] Int, Unit>?
 *           列表项长按处理监听
 * @constructor 构建一个适配器
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class CourseTimeAdapter
    constructor(var courseTimeList : List<CourseTime> ?= null): RecyclerView.Adapter<CourseTimeAdapter.ViewHolder>() {

    private var context : Context?= null

    var onItemClickListener: ((adapter: RecyclerView.Adapter<ViewHolder>, position: Int) -> Unit) ?= null
    var onItemLongClickListener: ((adapter: RecyclerView.Adapter<ViewHolder>, position: Int) -> Unit) ?= null

    /**
     * ViewHolder
     * @property contentText TextView 内容TextView
     * @constructor 构建一个ViewHolder
     */
    inner class ViewHolder
        constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

        var contentText : TextView

        init {
            contentText = itemView.findViewById(R.id.contentText)
        }
    }

    /**
     * 创建ViewHolder
     * @param parent ViewGroup 父布局
     * @param viewType Int viewType
     * @return ViewHolder viewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (context == null) {
            context = parent.context
        }
        val view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_course_time_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * 绑定ViewHolder
     * @param holder ViewHolder viewHolder
     * @param position Int 位置
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = "${courseTimeList?.get(position)?.startTime}-${courseTimeList?.get(position)?.endTime}"
        holder.contentText.text = content
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(this, position)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.invoke(this, position)
            true
        }
    }

    /**
     * 获取子项数量
     * @return Int 子项数量
     */
    override fun getItemCount(): Int {
        return courseTimeList?.size ?: 0
    }
}