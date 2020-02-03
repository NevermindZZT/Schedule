package com.letter.schedule.course

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.letter.schedule.R
import kotlinx.android.synthetic.main.layout_course_week_item.view.*

/**
 * 课程星期表头View
 *
 * @property week String? 星期文本
 * @constructor 构造一个课程星期表头View
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class WeekItemView @JvmOverloads
constructor(context: Context,
            attrs: AttributeSet?=null,
            defStyleAttr: Int=0,
            defStyleRes: Int=0):
    LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    var week: String?=null
    set(value) {
        field = value
        weekText.text = field
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_course_week_item, this)
    }
}