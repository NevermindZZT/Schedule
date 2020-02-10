package com.letter.schedule.course

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.letter.schedule.R
import kotlinx.android.synthetic.main.layout_course_time_item.view.*

/**
 * 课程时间View
 *
 * @property courseTime CourseTime? 课程时间
 * @property courseIndex Int 课程序号
 * @constructor 构造一个课程时间View
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class TimeItemView @JvmOverloads
constructor(context: Context,
            attrs: AttributeSet?=null,
            defStyleAttr: Int=0,
            defStyleRes: Int=0):
    LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    var courseTime: CourseTime?=null
    set(value) {
        field = value
        startTimeText.text = field?.startTime
        endTimeText.text = field?.endTime
    }

    var courseIndex = 0
    set(value) {
        field = value
        orderText.text = field.toString()
    }

    var showEndTime: Boolean = true
    set(value) {
        field = value
        endTimeText.visibility = if (value) View.VISIBLE else View.GONE
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_course_time_item, this)
    }

}