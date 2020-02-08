package com.letter.schedule.course

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

class CourseTable
    constructor(var id : Int = 0,
                var name : String ?= null,
                var default : Boolean = false) : LitePalSupport() {

    @Column(ignore = true)
    var courseList: MutableList<Course> ?= null

    @Column(ignore = true)
    var courseTimeList: MutableList<CourseTime> ?= null

    fun addCourse(course: Course) {
        courseList?.add(course)
    }

    fun removeCourse(course: Course) {
        courseList?.remove(course)
    }

    fun removeCourse(index: Int) {
        courseList?.removeAt(index)
    }

    fun addTime(courseTime: CourseTime) {
        courseTimeList?.add(courseTime)
    }

    fun removeTime(courseTime: CourseTime) {
        courseTimeList?.remove(courseTime)
    }

    fun removeTime(index: Int) {
        courseTimeList?.removeAt(index)
    }
}