package com.letter.schedule

import android.app.Application
import org.litepal.LitePal

/**
 * 应用Application
 *
 * @author Letter(zhangkeqiang@gmail.com)
 * @since 1.0.0
 */
class ScheduleApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        instance = this
        LitePal.initialize(this)
    }

    companion object {
        /**
         * Application 单例
         */
        private var instance: ScheduleApplication ?= null

        /**
         * 获取Application实例
         * @return ScheduleApplication Application实例
         */
        fun instance() = instance!!
    }
}