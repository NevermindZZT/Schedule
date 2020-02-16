package com.letter.schedule.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.UriUtils
import com.letter.schedule.R
import com.letter.schedule.course.*
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
            else selectedTable?.name
        toolbar.menu.getItem(1).title =
            if (value) getString(R.string.main_activity_toolbar_menu_edit_complete)
            else getString(R.string.main_activity_toolbar_menu_edit_mode)
        toolbar.menu.getItem(1).setIcon(
            if (value) R.drawable.ic_toolbar_complete
            else R.drawable.ic_toolbar_order)
        selectedClassItemView?.checked = false
    }

    private var selectedTable: CourseTable ?= null

    private var selectedCourse: Course ?= null
    private var selectedClassItemView: ClassItemView ?= null
    private var selected: Boolean = false

    private var courseLongClick: Boolean = false

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

        selectedTable = getDefaultTable()
    }

    override fun onResume() {
        super.onResume()
        courseView.post {
            loadCourseTable(selectedTable)
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
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
            R.id.toolbar_table_edit -> {
                if (selectedTable == null) {
                    Toast.makeText(this, R.string.main_activity_toast_course_table_invalid, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val intent = Intent(this, CourseTableEditActivity::class.java)
                    intent.putExtra("table_id", selectedTable?.id ?: 0)
                    startActivity(intent)
                }
            }
            R.id.toolbar_edit -> {
                if (selectedTable == null) {
                    Toast.makeText(this, R.string.main_activity_toast_course_table_invalid, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    editMode = !editMode
                }
            }
            R.id.toolbar_save_picture -> {
                val file = selectedTable?.saveAsPicture(this)
                Toast.makeText(this,
                    if (file != null) R.string.main_activity_toast_save_picture_success
                    else R.string.main_activity_toast_save_picture_fail,
                    Toast.LENGTH_SHORT)
                    .show()
//                val uri = selectedTable?.saveAsPictureToAlbum(this)
//                Toast.makeText(this,
//                    if (uri != null) R.string.main_activity_toast_save_picture_success
//                    else R.string.main_activity_toast_save_picture_fail,
//                    Toast.LENGTH_SHORT)
//                    .show()
            }
            R.id.toolbar_share -> {
                if (selectedTable == null) {
                    Toast.makeText(this, R.string.main_activity_toast_course_table_invalid, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    shareCourseTable()
                }
            }
            R.id.toolbar_export_excel -> {
                if (selectedTable == null) {
                    Toast.makeText(this, R.string.main_activity_toast_course_table_invalid, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val file = selectedTable?.saveAsExcel(this)
                    Toast.makeText(
                        this,
                        if (file != null) R.string.main_activity_toast_export_excel_success
                        else R.string.main_activity_toast_export_excel_fail,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
            R.id.toolbar_share_excel -> {
                if (selectedTable == null) {
                    Toast.makeText(this, R.string.main_activity_toast_course_table_invalid, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    shareCourseTableAsExcel()
                }
            }
            R.id.popup_copy -> {
                selected = true
                selectedClassItemView?.checked = true
                Toast.makeText(this, R.string.main_activity_toast_course_copy, Toast.LENGTH_SHORT)
                    .show()
            }
            R.id.popup_delete -> {
                val dialog = AlertDialog.Builder(this, R.style.DialogTheme)
                    .setMessage("${getString(R.string.main_activity_delete_dialog_message)} ${selectedCourse?.name}?")
                    .setPositiveButton(R.string.main_activity_delete_positive_button,
                        {
                                dialogInterface, i ->
                            dialogInterface.dismiss()
                            courseView.courseList.remove(selectedCourse)
                            selectedCourse?.delete()
                            Toast.makeText(this, R.string.main_activity_toast_delete_complete, Toast.LENGTH_SHORT)
                                .show()
                            courseView.notifyClassChanged()
                        })
                    .setNegativeButton(R.string.main_activity_delete_negative_button,
                        {
                                dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                    .create()
                dialog.show()
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

    /**
     * 获取默认课表的
     * @return Int 默认课表
     */
    private fun getDefaultTable() : CourseTable? {
        val courseTableList = LitePal.where("isDefault like ?", "1")
            .find<CourseTable>()
        return if (courseTableList.isNotEmpty()) courseTableList[0] else null
    }

    /**
     * 加载课程表
     * @param table CourseTable 课程表
     */
    private fun loadCourseTable(table: CourseTable?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        courseView.startOfWeek = sharedPreferences.getString("start_of_week", "1")?.toInt() ?: 1
        courseView.courseHeight =
            (sharedPreferences.getString("course_height", "64")?.toInt() ?: 64) *
                    resources.displayMetrics.density
        courseView.showEndTime = sharedPreferences.getBoolean("show_end_time", false)
        courseView.showTimeIndex = sharedPreferences.getBoolean("show_time_index", true)
        courseView.showCourseBorder = sharedPreferences.getBoolean("show_course_border", false)
        courseView.courseTextSize = sharedPreferences.getString("course_text_size", "14")?.toFloat() ?: 14f

        courseView.weekText = resources.getStringArray(R.array.week_title_content)
        courseView.initWeekTitle()
        var courseTable = table
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
        selectedTable =  courseTable
    }

    /**
     * 分享当前课程表
     */
    private fun shareCourseTable() {
        val file = selectedTable?.saveAsPicture(this)
        if (file != null) {
            val intent = IntentUtils.getShareImageIntent("", file.path)
            startActivity(Intent.createChooser(intent, "分享"))
        }
    }

    /**
     * 以Excel形式分享当前课表
     */
    private fun shareCourseTableAsExcel() {
        val file = selectedTable?.saveAsExcel(this)
        if (file != null) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, UriUtils.file2Uri(file))
            intent.type = "*/*"
            startActivity(Intent.createChooser(intent, "分享"))
        }
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
                                    loadCourseTable(courseTableList[which])
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
                                     weekday: Int,
                                     longClick: Boolean) -> Unit) = {
            hasClass: Boolean,
            classItemView: ClassItemView?,
            course: Course?,
            courseTime: CourseTime?,
            weekday: Int,
            longClick: Boolean ->
        if (editMode) {
            if (hasClass) {
                Log.d(TAG, "selected $selected")
                if (!selected) {
                    selectedCourse = course
                    selectedClassItemView = classItemView
                    courseLongClick = longClick
                    if (longClick) {
                        val popupMenu = PopupMenu(this, classItemView)
                        popupMenu.menuInflater.inflate(R.menu.menu_course_popup, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener {
                            onOptionsItemSelected(it)
                        }
                        popupMenu.show()
                    } else {
                        classItemView?.checked = true
                        selected = true
                    }
                } else {
                    selectedCourse?.switch(course!!)
                    selectedCourse?.save()
                    course?.save()
                    courseView.notifyClassChanged()
                    selectedCourse = null
                    selected = false
                }
            } else {
                if (!selected) {
                    Toast.makeText(this,
                        R.string.main_activity_toast_please_select_class,
                        Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (courseLongClick) {
                        val newCourse = selectedCourse?.copy(selectedCourse?.tableId!!)
                        newCourse?.startTime = courseTime?.startTime
                        newCourse?.weekDay = weekday
                        newCourse?.save()
                        courseView.courseList.add(newCourse!!)
                    } else {
                        selectedCourse?.startTime = courseTime?.startTime
                        selectedCourse?.weekDay = weekday
                        selectedCourse?.save()
                    }
                    courseView.notifyClassChanged()
                    selectedCourse = null
                    selected = false
                }
            }
        } else {
            val intent = Intent(this, CourseEditActivity::class.java)
            intent.putExtra("table_id", selectedTable?.id ?: 0)
            if (!longClick) {
                if (hasClass) {
                    intent.putExtra("course_id", course?.id)
                } else {
                    intent.putExtra("course_time_id", courseTime?.id)
                    intent.putExtra("week", weekday)
                }
                startActivity(intent)
            }
            selected = false
        }
    }
}
