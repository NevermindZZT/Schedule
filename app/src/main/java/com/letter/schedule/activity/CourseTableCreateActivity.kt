package com.letter.schedule.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.letter.schedule.R
import com.letter.schedule.course.Course
import com.letter.schedule.course.CourseTable
import com.letter.schedule.course.CourseTime
import kotlinx.android.synthetic.main.activity_about.toolbar
import kotlinx.android.synthetic.main.activity_course_table_create.*
import org.litepal.LitePal
import org.litepal.extension.find
import org.litepal.extension.findAll

/**
 * 课程表新建活动
 * @property courseTableList List<CourseTable> 已有的课程表列表
 */
class CourseTableCreateActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var courseTableList : List<CourseTable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_table_create)
        setSupportActionBar(toolbar)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.title = getString(R.string.course_table_create_activity_title)

        newButton.setOnClickListener(this)
        copyButton.setOnClickListener(this)

        initCourseTableSpinner()
    }

    /**
     * 菜单选项选择处理
     * @param item 被选中的选项
     * @return Boolean 动作是否被处理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    /**
     * View点击处理
     * @param v View 被点击的View
     */
    override fun onClick(v: View?) {
        when (v) {
            newButton -> {
                if (newCourseTableNameText.text.toString().isEmpty()) {
                    Toast.makeText(
                        this,
                        R.string.course_table_create_activity_toast_course_table_name_empty,
                        Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val courseTable = CourseTable()
                    courseTable.name = newCourseTableNameText.text.toString()
                    courseTable.save()
                }
            }
            copyButton -> {
                if (copyCourseTableNameText.text.toString().isEmpty()) {
                    Toast.makeText(
                        this,
                        R.string.course_table_create_activity_toast_course_table_name_empty,
                        Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val courseTable = CourseTable()
                    courseTable.name = copyCourseTableNameText.text.toString()
                    courseTable.save()
                    val srcTableId = courseTableList[courseTableSpinner.selectedItemId.toInt()].id
                    for (value in LitePal
                        .where("tableId like ?", srcTableId.toString())
                        .find<CourseTime>()) {
                        value.copy(courseTable.id).save()
                    }
                    for (value in LitePal
                        .where("tableId like ?", srcTableId.toString())
                        .find<Course>()) {
                        value.copy(courseTable.id).save()
                    }
                }
            }
        }
    }

    /**
     * 初始化课程表下拉选项
     */
    private fun initCourseTableSpinner() {
        courseTableList = LitePal.findAll<CourseTable>()
        if (courseTableList.isEmpty()) {
            copyCardView.visibility = View.GONE
            return
        }
        val courseNameList = mutableListOf<String>()
        for (value in courseTableList) {
            courseNameList.add(value.name ?: "")
        }
        val spinnerAdapter = ArrayAdapter<String>(
            this,
            R.layout.layout_course_table_item_select,
            courseNameList)
        spinnerAdapter.setDropDownViewResource(R.layout.layout_course_table_item_drop)
        courseTableSpinner.adapter = spinnerAdapter
    }

}
