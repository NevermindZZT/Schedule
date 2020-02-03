package com.letter.schedule.course

import org.litepal.crud.LitePalSupport

class CourseTime : LitePalSupport() {
    var startTime : String ?= null
    var endTime : String ?= null
}