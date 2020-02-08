package com.letter.schedule.course

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class ClassView @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0, defStyleRes: Int=0):
    FrameLayout(context, attrs, defStyleAttr, defStyleRes)  {

    var onClickListener : ((x: Int, y: Int) -> Unit) ?= null

    init {
        isClickable = true
        isFocusable = true
    }

    /**
     * 触摸事件
     * @param event MotionEvent 触摸时间
     * @return Boolean
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {
                onClickListener?.invoke(event.x.toInt(), event.y.toInt())
            }
        }
        return super.onTouchEvent(event)
    }

}