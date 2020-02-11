package com.letter.schedule

import android.Manifest
import android.app.Application
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import org.litepal.LitePal

/**
 * 应用Application
 *
 * @author Letter(zhangkeqiang@gmail.com)
 * @since 1.0.0
 */
class ScheduleApplication : Application() {

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

    override fun onCreate() {
        super.onCreate()
        instance = this
        LitePal.initialize(this)
        requestPermission()
    }

    private fun requestPermission() {
        if (!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtils.permission(PermissionConstants.STORAGE)
                .request()
        }
    }
}