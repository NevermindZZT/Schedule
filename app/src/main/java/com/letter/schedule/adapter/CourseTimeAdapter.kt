package com.letter.schedule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.letter.schedule.R
import com.letter.schedule.course.CourseTime

class CourseTimeAdapter
    constructor(var courseTimeList : List<CourseTime> ?= null): RecyclerView.Adapter<CourseTimeAdapter.ViewHolder>() {

    private var context : Context?= null

    var onItemClickListener: ((adapter: RecyclerView.Adapter<ViewHolder>, position: Int) -> Unit) ?= null
    var onItemLongClickListener: ((adapter: RecyclerView.Adapter<ViewHolder>, position: Int) -> Unit) ?= null

    inner class ViewHolder
        constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

        var contentText : TextView

        init {
            contentText = itemView.findViewById(R.id.contentText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (context == null) {
            context = parent.context
        }
        val view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_course_time_item, parent, false)
        return ViewHolder(view)
    }

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

    override fun getItemCount(): Int {
        return courseTimeList?.size ?: 0
    }
}