package com.letter.schedule.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.IntentUtils
import com.letter.schedule.R
import com.letter.schedule.course.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_shared_table.view.*
import org.litepal.LitePal
import org.litepal.extension.find
import org.litepal.extension.findAll
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

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

        selectedTableId = getDefaultTableId()
    }

    override fun onResume() {
        super.onResume()
        courseView.post {
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
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
            R.id.toolbar_table_edit -> {
                if (selectedTableId == 0) {
                    Toast.makeText(this, R.string.main_activity_toast_course_table_invalid, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val intent = Intent(this, CourseTableEditActivity::class.java)
                    intent.putExtra("table_id", selectedTableId)
                    startActivity(intent)
                }
            }
            R.id.toolbar_edit -> {
                if (selectedTableId == 0) {
                    Toast.makeText(this, R.string.main_activity_toast_course_table_invalid, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    editMode = !editMode
                }
            }
            R.id.toolbar_save_picture -> {
                val file = saveCourseTableAsPicture()
                Toast.makeText(this,
                    if (file != null) R.string.main_activity_toast_save_picture_success
                    else R.string.main_activity_toast_save_picture_fail,
                    Toast.LENGTH_SHORT)
                    .show()
            }
            R.id.toolbar_share -> {
                if (selectedTableId == 0) {
                    Toast.makeText(this, R.string.main_activity_toast_course_table_invalid, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    shareCourseTable()
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

    /**
     * 获取默认课表的id
     * @return Int 默认课表id
     */
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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        courseView.startOfWeek = sharedPreferences.getString("start_of_week", "1")?.toInt() ?: 1
        courseView.courseHeight =
            (sharedPreferences.getString("course_height", "64")?.toInt() ?: 64) *
                    resources.displayMetrics.density
        courseView.showEndTime = sharedPreferences.getBoolean("show_end_time", false)
        courseView.showTimeIndex = sharedPreferences.getBoolean("show_time_index", true)
        courseView.showCourseBorder = sharedPreferences.getBoolean("show_course_border", false)

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
     * 将当前课表保存为图片
     * @return File? 保存后的文件
     */
    private fun saveCourseTableAsPicture(): File? {
        val sharedTableView = SharedTableView(this)
        sharedTableView.courseTableId = selectedTableId
        val bitmap = sharedTableView.getBitmap()
        if (FileUtils.createOrExistsDir(getExternalFilesDir(Environment.DIRECTORY_PICTURES))) {
            try {
                val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
                    .format(Date(System.currentTimeMillis())) + ".jpg"
                val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
                Log.d(TAG, "file path: ${file.path}")
                val fileOutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                fileOutputStream.flush()
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_STARTED,
                    Uri.fromFile(File(file.path))))

                return file
            } catch (exception: Exception) {
                Log.e(TAG, "", exception)
            }
        }
        return null
    }

    /**
     * 分享当前课程表
     */
    private fun shareCourseTable() {
        val file = saveCourseTableAsPicture()
        if (file != null) {
            val intent = IntentUtils.getShareImageIntent("", file.path)
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
                if (selectedCourse == null) {
                    selectedCourse = course
                    classItemView?.checked = true
                    courseLongClick = longClick
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
