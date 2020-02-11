package com.letter.schedule.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * 空白布局
 * @property activeViewId Int 激活的View id
 * @constructor 构建一个空白布局
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class EmptyLayout @JvmOverloads
constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0, defStyleRes: Int=0):
    FrameLayout(context, attrs, defStyleAttr, defStyleRes)  {

    var activeViewId = 0
    set(value) {
        for (i in 0 until childCount) {
            getChildAt(i)?.visibility = View.GONE
        }
        field = value
        getChildAt(field)?.visibility = View.VISIBLE
    }

    init {
        activeViewId = 0
    }
}