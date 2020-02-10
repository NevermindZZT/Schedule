package com.letter.schedule.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import com.letter.schedule.R
import com.letter.schedule.course.ClassItemView
import com.letter.schedule.course.Course
import com.letter.schedule.course.CourseTable
import com.letter.schedule.course.CourseTime
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.LitePal
import org.litepal.extension.find
import org.litepal.extension.findAll

/**
 * 应用主活动
 *
 * @author Letter(zhangkeqiang@gmail.com)
 * @since 1.0.0
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private var editMode: Boolean = false
    set(value) {
        field = value
        courseTableNameText.text = if (value) getString(R.string.main_activity_toolbar_menu_edit_mode)
            else LitePal.find<CourseTable>(selectedTableId.toLong())?.name ?: ""
        toolbar.menu.getItem(1).title =
            if (value) getString(R.string.main_activity_toolbar_menu_edit_complete)
            else getString(R.string.main_activity_toolbar_menu_edit_mode)
        toolbar.menu.getItem(1).setIcon(
            if (value) R.drawable.ic_toolbar_complete
            else R.drawable.ic_toolbar_order)
    }

    private var selectedTableId: Int = 0

    private var selectedCourse: Course ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // 设置为浅色状态栏
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 开启ActionBar home按钮并设置图标
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.title = ""

        // 抽屉菜单选项选择处理
        navigationView.setNavigationItemSelectedListener(onNavigationViewItemClick)

        courseTableNameText.setOnClickListener(onViewClick)
        tableAddButton.setOnClickListener(onViewClick)

        courseView.onClickListener = onCourseViewClick

        selectedTableId = getDefaultTableId()
    }

    override fun onResume() {
        super.onResume()
        courseView.post {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            courseView.startOfWeek = sharedPreferences.getString("start_of_week", "1")?.toInt() ?: 1
            courseView.courseHeight =
                (sharedPreferences.getString("course_height", "64")?.toInt() ?: 64) *
                        resources.displayMetrics.density
            courseView.showEndTime = sharedPreferences.getBoolean("show_end_time", false)
            loadCourseTable(selectedTableId)
        }
    }

    /**
     * 创建选项菜单
     * @param menu Menu 菜单
     * @return Boolean
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_toolbar, menu)
        return true
    }

    /**
     * 菜单选项选择处理
     * @param item 被选中的选项
     * @return Boolean 动作是否被处理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (selectedTableId == 0) {
            Toast.makeText(this, R.string.main_activity_toast_course_table_invalid, Toast.LENGTH_SHORT)
                .show()
        } else {
            when (item.itemId) {
                android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
                R.id.toolbar_table_edit -> {
                    val intent = Intent(this, CourseTableEditActivity::class.java)
                    intent.putExtra("table_id", selectedTableId)
                    startActivity(intent)
                }
                R.id.toolbar_edit -> {
                    editMode = !editMode
                }
            }
        }
        return true
    }

    /**
     * Navigation视图菜单项选择处理
     */
    private val onNavigationViewItemClick: ((item: MenuItem) -> Boolean) = {
        when (it.itemId) {
            R.id.nav_new -> startActivity(Intent(this, CourseTableCreateActivity::class.java))
            R.id.nav_setting -> startActivity(Intent(this, SettingActivity::class.java))
            R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
        }
        drawerLayout.closeDrawers()
        true
    }

    private fun getDefaultTableId() : Int {
        val courseTableList = LitePal.where("isDefault like ?", "1")
            .find<CourseTable>()
        return if (courseTableList.isNotEmpty()) courseTableList[0].id else 0
    }

    /**
     * 加载课程表
     * @param tableId Int 课程表id
     */
    private fun loadCourseTable(tableId: Int) {
        courseView.weekText = resources.getStringArray(R.array.week_title_content)
        courseView.initWeekTitle()
        var courseTable = LitePal.find<CourseTable>(tableId.toLong())
        if (courseTable == null) {
            val courseTableList = LitePal.findAll<CourseTable>()
            if (courseTableList.isNotEmpty()) {
                courseTable = courseTableList[0]
            }
        }
        if (courseTable != null) {
            courseView.courseTimeList =
                LitePal.where("tableId like ?", courseTable.id.toString())
                    .find<CourseTime>().toMutableList()
            courseView.courseList =
                LitePal.where("tableId like ?", courseTable.id.toString())
                    .find<Course>().toMutableList()
        }
        courseTableNameText.text = courseTable?.name
        emptyLayout.activeViewId = if (courseTable == null) 0 else 1
        selectedTableId =  courseTable?.id ?: 0
    }

    /**
     * View点击处理
     */
    private val onViewClick: ((view: View) -> Unit) = {
        when (it) {
            courseTableNameText -> {
                if (!editMode) {
                    val courseTableList = LitePal.findAll<CourseTable>()
                    if (courseTableList.isNotEmpty()) {
                        val nameList = mutableListOf<String>()
                        for (value in courseTableList) {
                            nameList.add(value.name ?: "")
                        }
                        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
                            .setItems(nameList.toTypedArray(),
                                {dialog, which ->
                                    loadCourseTable(courseTableList[which].id)
                                    dialog.dismiss()
                                })
                        builder.create().show()
                    }
                } else {
                    Toast.makeText(this, R.string.main_activity_toast_please_exit_edit_mode, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            tableAddButton -> startActivity(Intent(this, CourseTableCreateActivity::class.java))
        }
    }

    /**
     * 课程表点击处理
     */
    private val onCourseViewClick: ((hasClass: Boolean,
                                     classItemView: ClassItemView?,
                                     course: Course?,
                                     courseTime: CourseTime?,
                                     weekday: Int) -> Unit) = {
            hasClass: Boolean,
            classItemView: ClassItemView?,
            course: Course?,
            courseTime: CourseTime?,
            weekday: Int ->
        if (editMode) {
            if (hasClass) {
                if (selectedCourse == null) {
                    selectedCourse = course
                    classItemView?.checked = true
                } else {
                    selectedCourse?.switch(course!!)
                    selectedCourse?.save()
                    course?.save()
                    courseView.notifyClassChanged()
                    selectedCourse = null
                }
            } else {
                if (selectedCourse == null) {
                    Toast.makeText(this,
                        R.string.main_activity_toast_please_select_class,
                        Toast.LENGTH_SHORT)
                        .show()
                } else {
                    selectedCourse?.startTime = courseTime?.startTime
                    selectedCourse?.weekDay = weekday
                    selectedCourse?.save()
                    courseView.notifyClassChanged()
                    selectedCourse = null
                }
            }
        } else {
            val intent = Intent(this, CourseEditActivity::class.java)
            intent.putExtra("table_id", selectedTableId)
            if (hasClass) {
                intent.putExtra("course_id", course?.id)
            } else {
                intent.putExtra("course_time_id", courseTime?.id)
                intent.putExtra("week", weekday)
            }
            startActivity(intent)
        }
    }
}
