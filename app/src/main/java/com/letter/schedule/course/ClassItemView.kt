package com.letter.schedule.course

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import com.letter.schedule.R
import kotlinx.android.synthetic.main.layout_course_class_item.view.*

/**
 * 课程View
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class ClassItemView @JvmOverloads
constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0):
    CardView(context, attrs, defStyleAttr) {

    init {
        radius = context.resources.displayMetrics.density * 8
        elevation = 0f
    }

    /**
     * 课程
     */
    var course : Course?=null
    set(value) {
        field = value
        nameText.text = value?.name
        if (value?.teacher?.isEmpty() ?: true && value?.location?.isEmpty() ?: true) {
            extraText.visibility = View.GONE
        } else {
            val extra = "${value?.teacher}@${value?.location}"
            extraText.text = extra
            extraText.visibility = View.VISIBLE
        }

        // 根据主题色深度，调整文本颜色
        if (value != null) {
            val bright = (value.color.and(0x00FF0000).ushr(16) * 0.3
                    + value.color.and(0x0000FF00).ushr(8) * 0.6
                    + value.color.and(0x000000FF) * 0.1)
            if (bright < 0x80 && value.color.toLong().and(0xFF000000).ushr(24) > 0x20) {
                nameText.setTextColor(Color.WHITE)
                extraText.setTextColor(Color.WHITE)
            }
        }
    }

    /**
     * 选中状态
     */
    var checked : Boolean = false
    set(value) {
        field = value
        checkImage.visibility = if (value) View.VISIBLE else View.GONE
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_course_class_item, this)
    }
}