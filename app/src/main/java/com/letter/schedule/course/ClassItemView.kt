package com.letter.schedule.course

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.letter.schedule.R
import kotlinx.android.synthetic.main.layout_course_class_item.view.*

class ClassItemView @JvmOverloads
constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0, defStyleRes: Int=0):
    LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    var course : Course?=null
    set(value) {
        field = value
        nameText.text = field?.name
        val extra = "${field?.teacher}@${field?.location}"
        extraText.text = extra
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_course_class_item, this)
    }
}