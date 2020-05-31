package com.letter.schedule.activity

import android.os.Bundle
import com.letter.schedule.R
import kotlinx.android.synthetic.main.activity_course_table_list.*

class CourseTableListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_table_list)

        setSupportActionBar(toolbar)

        // 开启ActionBar home按钮并设置图标
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.title = getString(R.string.course_table_list_activity_title)
    }
}
