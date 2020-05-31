package com.letter.schedule.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.letter.colorpicker.ColorPickerDialog
import com.letter.schedule.R
import com.letter.schedule.course.Course
import com.letter.schedule.course.CourseTime
import kotlinx.android.synthetic.main.activity_course_edit.*
import org.litepal.LitePal
import org.litepal.extension.find

/**
 * 课程编辑活动
 * @property course Course 课程
 * @property courseTimeArray Array<String> 课程时间列表
 * @property onViewClick Function1<[@kotlin.ParameterName] View, Unit> View点击处理
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class CourseEditActivity : BaseActivity() {

    companion object {
        val WEEK_TEXT = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六", "周日")
        val LENGTH_TEXT = arrayOf("1课时", "2课时", "3课时", "4课时")
    }

    private lateinit var course: Course
    private lateinit var courseTimeArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_edit)

        setSupportActionBar(toolbar)

        // 开启ActionBar home按钮并设置图标
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.title = getString(R.string.course_edit_activity_title)

        initData()
    }

    /**
     * 菜单选项选择处理
     * @param item 被选中的选项
     * @return Boolean 动作是否被处理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.delete -> {
                val dialog = AlertDialog.Builder(this, R.style.DialogTheme)
                    .setMessage(R.string.course_edit_activity_delete_dialog_message)
                    .setPositiveButton(R.string.course_time_dialog_positive_button_text,
                        {
                            dialogInterface, i ->
                            dialogInterface.dismiss()
                            course.delete()
                            Toast.makeText(this, R.string.course_edit_activity_toast_delete_complete, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        })
                    .setNegativeButton(R.string.course_time_dialog_negative_button_text,
                        {
                            dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                    .create()
                dialog.show()
            }
            R.id.save -> {
                if (nameText.text.toString().isEmpty()) {
                    Toast.makeText(this, R.string.course_edit_activity_toast_name_empty, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    course.name = nameText.text.toString()
                    course.teacher = teacherText.text.toString()
                    course.location = locationText.text.toString()
                    course.color = themeColor.color
                    course.save()
                    Toast.makeText(this, R.string.course_edit_activity_toast_save_complete, Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }
        return true
    }

    /**
     * 构建选项菜单
     * @param menu Menu 菜单
     * @return Boolean
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_course_edit_toolbar, menu)
        return true
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        val tableId = intent.getIntExtra("table_id", 0)
        val courseId = intent.getIntExtra("course_id", -1)
        val courseTimeId = intent.getIntExtra("course_time_id", -1)
        val week = intent.getIntExtra("week", 0)

        course = LitePal.find<Course>(courseId.toLong()) ?: Course()
        val courseTime = LitePal.find<CourseTime>(courseTimeId.toLong())
        if (courseTime != null) {
            course.startTime = courseTime.startTime
        }
        course.tableId = tableId
        if (courseId == -1) {
            course.weekDay = week
        }

        nameText.setText(course.name)
        teacherText.setText(course.teacher)
        locationText.setText(course.location)
        startTimeText.text = course.startTime
        lengthText.text = LENGTH_TEXT[course.length - 1]
        weekText.text = WEEK_TEXT[course.weekDay]
        themeColor.color = course.color

        timeLayout.setOnClickListener(onViewClick)
        lengthLayout.setOnClickListener(onViewClick)
        weekLayout.setOnClickListener(onViewClick)
        themeLayout.setOnClickListener(onViewClick)

        val courseStartTimeList = mutableListOf<String>()
        for (value in LitePal.where("tableId like ?", tableId.toString()).find<CourseTime>()) {
            courseStartTimeList.add(value.startTime ?: "")
        }
        courseTimeArray = courseStartTimeList.toTypedArray()
    }

    private val onViewClick: (view: View) -> Unit = {
        when (it) {
            timeLayout -> {
                val builder = AlertDialog.Builder(this, R.style.DialogTheme)
                    .setItems(
                        courseTimeArray,
                        {dialog, which ->
                            startTimeText.text = courseTimeArray[which]
                            course.startTime = courseTimeArray[which]
                            dialog.dismiss()
                        })
                builder.create().show()
            }
            lengthLayout -> {
                val builder = AlertDialog.Builder(this, R.style.DialogTheme)
                    .setItems(
                        LENGTH_TEXT,
                        {dialog, which ->
                            lengthText.text = LENGTH_TEXT[which]
                            course.length = which + 1
                            dialog.dismiss()
                        })
                builder.create().show()
            }
            weekLayout -> {
                val builder = AlertDialog.Builder(this, R.style.DialogTheme)
                    .setItems(
                        WEEK_TEXT,
                        {dialog, which ->
                            weekText.text = WEEK_TEXT[which]
                            course.weekDay = which
                            dialog.dismiss()
                        })
                builder.create().show()
            }
            themeLayout -> {
                val dialog = ColorPickerDialog.Builder(this, R.style.DialogTheme)
                    .setOnColorSelectListener {
                        dialog, color ->
                        themeColor.color = color
                        dialog.dismiss()
                    }
                    .setSelectedColor(themeColor.color)
                    .setColors(resources.getStringArray(R.array.color_picker_values))
                    .setColumns(4)
                    .create()
                dialog.show()
            }
        }
    }
}
