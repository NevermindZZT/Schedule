package com.letter.schedule.course

import org.litepal.crud.LitePalSupport
import java.io.File

/**
 * 课程表
 * @property id Int id
 * @property name String? 课程表名字
 * @property isDefault Int 是否为默认课程表
 * @constructor 构建一个课程表
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class CourseTable
    constructor(var id : Int = 0,
                var name : String ?= null,
                var isDefault : Int = DEFAULT_FALSE) : LitePalSupport() {

    companion object {
        const val DEFAULT_FALSE = 0
        const val DEFAULT_TRUE = 1
    }

    fun toExcel(file: File) {

    }
}