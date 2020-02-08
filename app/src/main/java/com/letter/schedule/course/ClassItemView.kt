package com.letter.schedule.course

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import com.letter.schedule.R
import kotlinx.android.synthetic.main.layout_course_class_item.view.*

class ClassItemView @JvmOverloads
constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0):
    CardView(context, attrs, defStyleAttr) {

    var course : Course?=null
    set(value) {
        field = value
        nameText.text = field?.name
        val extra = "${field?.teacher}@${field?.location}"
        extraText.text = extra
    }

    var checked : Boolean = false
    set(value) {
        field = value
        checkImage.visibility = if (value) View.VISIBLE else View.GONE
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_course_class_item, this)
    }
}