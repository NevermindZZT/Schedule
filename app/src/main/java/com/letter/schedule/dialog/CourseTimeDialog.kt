package com.letter.schedule.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.NumberPicker
import com.letter.schedule.R
import kotlinx.android.synthetic.main.dialog_course_time.*

class CourseTimeDialog
    @JvmOverloads
    constructor(context: Context, theme: Int = 0)
    : Dialog(context, theme) {

    companion object {
        const val BUTTON_NEGATIVE = -1
        const val BUTTON_POSITIVE = 0
    }

    var onButtonClickListener :
            ((dialog: Dialog, witch: Int, startTime: String?, endTime: String?) -> Unit) ?= null
    set(value) {
        field = value
        setButton()
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_course_time, null)
        addContentView(view,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        initPicker()
    }

    private fun initPicker() {
        val hoursList = mutableListOf<String>()
        for (i in 0..11) {
            hoursList.add(i.toString())
        }
        val minuteList = mutableListOf<String>()
        for (i in 0..59) {
            minuteList.add(String.format("%02d", i))
        }

        startTimeHour.displayedValues = hoursList.toTypedArray()
        startTimeHour.minValue = 0
        startTimeHour.maxValue = hoursList.size - 1
        startTimeHour.value = 8
        startTimeHour.wrapSelectorWheel = true
        startTimeHour.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        startTimeMinute.displayedValues = minuteList.toTypedArray()
        startTimeMinute.maxValue = 0
        startTimeMinute.maxValue = minuteList.size - 1
        startTimeMinute.value = 0
        startTimeMinute.wrapSelectorWheel = true
        startTimeMinute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        endTimeHour.displayedValues = hoursList.toTypedArray()
        endTimeHour.minValue = 0
        endTimeHour.maxValue = hoursList.size - 1
        endTimeHour.value = 8
        endTimeHour.wrapSelectorWheel = true
        endTimeHour.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        endTimeMinute.displayedValues = minuteList.toTypedArray()
        endTimeMinute.maxValue = 0
        endTimeMinute.maxValue = minuteList.size - 1
        endTimeMinute.value = 0
        endTimeMinute.wrapSelectorWheel = true
        endTimeMinute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }

    private fun setButton() {
        positiveButton.setOnClickListener {
            val startTime = String.format("%d:%02d", startTimeHour.value, startTimeMinute.value)
            val endTime = String.format("%d:%02d", endTimeHour.value, endTimeMinute.value)
            onButtonClickListener?.invoke(this, BUTTON_POSITIVE, startTime, endTime)
        }
        negativeButton.setOnClickListener {
            onButtonClickListener?.invoke(this, BUTTON_NEGATIVE, null, null)
        }
    }

    fun setTime(startTime: String?, endTime: String?) {
        val startValues = startTime?.split(":")
        if (startValues?.size == 2) {
            startTimeHour.value = startValues[0].toInt()
            startTimeHour.wrapSelectorWheel = true
            startTimeMinute.value = startValues[1].toInt()
            startTimeMinute.wrapSelectorWheel = true
        }

        val endValues = endTime?.split(":")
        if (endValues?.size == 2) {
            endTimeHour.value = endValues[0].toInt()
            endTimeHour.wrapSelectorWheel = true
            endTimeMinute.value = endValues[1].toInt()
            endTimeMinute.wrapSelectorWheel = true
        }
    }

    fun show(init: (CourseTimeDialog.() -> Unit) ?= null) {
        if (init != null) {
            this.init()
        }
        show()
    }
}