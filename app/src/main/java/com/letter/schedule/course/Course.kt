package com.letter.schedule.course

import org.litepal.crud.LitePalSupport

/**
 * 课程数据
 * @property id Int id
 * @property tableId Int 课程表id
 * @property name String? 课程名
 * @property location String? 上课地点
 * @property teacher String? 授课教师
 * @property startTime String? 开始时间
 * @property length Int 课程长度
 * @property weekDay Int 课程星期
 * @property color Int 课程颜色
 * @constructor 构造一个课程数据
 * @author Letter(nevermindzzt@mail.com)
 * @since 1.0.0
 */
class Course
    constructor(var id : Int = 0,
                var tableId : Int = 0,
                var name : String ?= null,
                var location : String ?= null,
                var teacher : String ?= null,
                var startTime : String ?= null,
                var length : Int = 0,
                var weekDay : Int = WEEK_SUNDAY,
                var color : Int = 0): LitePalSupport() {

    companion object {
        const val WEEK_SUNDAY = 0
        const val WEEK_MONDAY = 1
        const val WEEK_TUESDAY = 2
        const val WEEK_WEDNESDAY = 3
        const val WEEK_THURSDAY = 4
        const val WEEK_FRIDAY = 5
        const val WEEK_SATURDAY = 6
    }

    /**
     * 复制当前课程
     * @param tableId Int 课程表id
     * @return Course 复制后的课程
     */
    fun copy(tableId: Int = 0) : Course {
        return Course(0, tableId, name, location, teacher, startTime, length, weekDay, color)
    }

    /**
     * 交换课程内容
     * @param course Course 需要交换的课表
     */
    fun switch(course: Course) {
        val tmpCourse = Course(id, tableId, name, location, teacher, startTime, length, weekDay, color)

        this.startTime = course.startTime
        this.length = course.length
        this.weekDay = course.weekDay

        course.startTime = tmpCourse.startTime
        course.length = tmpCourse.length
        course.weekDay = tmpCourse.weekDay
    }

}