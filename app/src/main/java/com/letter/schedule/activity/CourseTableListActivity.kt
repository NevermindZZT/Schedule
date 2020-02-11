package com.letter.schedule.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.letter.schedule.R
import kotlinx.android.synthetic.main.activity_course_table_list.*

class CourseTableListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_table_list)

        setSupportActionBar(toolbar)
        // 设置为浅色状态栏
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 开启ActionBar home按钮并设置图标
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.title = getString(R.string.course_table_list_activity_title)
    }
}
