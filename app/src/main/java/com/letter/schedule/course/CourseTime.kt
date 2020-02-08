package com.letter.schedule.course

import org.litepal.crud.LitePalSupport

/**
 * 课程时间数据
 * @property id Int id
 * @property tableId Int 课程表id
 * @property startTime String? 课程开始时间
 * @property endTime String? 课程结束时间
 * @constructor 构造一个课程时间数据
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class CourseTime
    constructor(var id : Int = 0,
                var tableId : Int = 0,
                var startTime : String ?= null,
                var endTime : String ?= null)
    : LitePalSupport() {

    override fun equals(other: Any?): Boolean {
        return other is CourseTime && startTime.equals(other.startTime) && endTime.equals(other.endTime)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    /**
     * 复制当前课程时间
     * @param tableId Int 课程表id
     * @return CourseTime 复制后的课程时间
     */
    fun copy(tableId: Int = 0) : CourseTime {
        return CourseTime(0, tableId, startTime, endTime)
    }

    /**
     * 获取开始时间的浮点数值
     * @return Float 开始时间
     */
    fun getStartTimeValue(): Float {
        val values = startTime?.split(":")
        if (values?.size == 2) {
            return values[0].toFloat() * 100 + values[1].toFloat() / 60
        }
        return 0f
    }

    /**
     * 获取结束时间的浮点数值
     * @return Float 开始时间
     */
    fun getEndTimeValue(): Float {
        val values = endTime?.split(":")
        if (values?.size == 2) {
            return values[0].toFloat() * 100 + values[1].toFloat() / 60
        }
        return 0f
    }
}