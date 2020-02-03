package com.letter.schedule.course

import org.litepal.crud.LitePalSupport

/**
 * 课程数据
 *
 * @author Letter(zhangkeqiang@gmail.com)
 * @since 1.0.0
 */
class Course : LitePalSupport() {

    var id : Int = 0
    var name : String ?= null
    var location : String ?= null
    var teacher : String ?= null
    var startTime : String ?= null
    var endTime : String ?= null
    var weekDay : Int = WEEK_SUNDAY

    companion object {
        const val WEEK_SUNDAY = 0
        const val WEEK_MONDAY = 1
        const val WEEK_TUESDAY = 2
        const val WEEK_WEDNESDAY = 3
        const val WEEK_THURSDAY = 4
        const val WEEK_FRIDAY = 5
        const val WEEK_SATURDAY = 6
    }
}