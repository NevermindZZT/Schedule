package com.letter.schedule.activity

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.letter.schedule.R
import com.letter.schedule.adapter.CourseTimeAdapter
import com.letter.schedule.course.Course
import com.letter.schedule.course.CourseTable
import com.letter.schedule.course.CourseTime
import com.letter.schedule.dialog.CourseTimeDialog
import kotlinx.android.synthetic.main.activity_course_table_edit.*
import kotlinx.android.synthetic.main.activity_course_table_edit.courseTableNameText
import kotlinx.android.synthetic.main.activity_course_table_edit.toolbar
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.LitePal
import org.litepal.extension.find

class CourseTableEditActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CourseTableEditActivity"
    }

    private var courseTable : CourseTable ?= null
    private var courseTimeList : MutableList<CourseTime> ?= null
    private var courseTimeAdapter : CourseTimeAdapter ?= null
    private var selectedCourseTime : CourseTime ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_table_edit)

        setSupportActionBar(toolbar)
        // 设置为浅色状态栏
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 开启ActionBar home按钮并设置图标
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.title = getString(R.string.course_table_edit_activity_title)

        courseTable = LitePal.find<CourseTable>(intent.getIntExtra("table_id", 0).toLong())
        courseTableNameText.setText(courseTable?.name)
        defaultCheckBox.isChecked = (courseTable?.isDefault == 1)
        if (courseTable != null) {
            courseTimeList = LitePal.where("tableId like ?", courseTable?.id.toString())
                .find<CourseTime>().toMutableList()
            courseTimeList?.sortWith(compareBy({it.getStartTimeValue()}, {it.getEndTimeValue()}))
        }
        if (courseTimeList == null) {
            courseTimeList = mutableListOf()
        }

        courseTimeRecyclerView.layoutManager = LinearLayoutManager(this)
        courseTimeAdapter = CourseTimeAdapter(courseTimeList)
        courseTimeAdapter?.onItemClickListener = onItemClick
        courseTimeAdapter?.onItemLongClickListener = onItemLongClick
        courseTimeRecyclerView.adapter = courseTimeAdapter

        addButton.setOnClickListener(onViewClick)
        defaultCheckBox.setOnClickListener(onViewClick)
    }

    override fun onPause() {
        super.onPause()
        courseTable?.name = courseTableNameText.text.toString()
        courseTable?.isDefault = if (defaultCheckBox.isChecked) 1 else 0
        courseTable?.save()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_course_table_edit_toolbar, menu)
        return true
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
                val dialog = androidx.appcompat.app.AlertDialog.Builder(this, R.style.DialogTheme)
                    .setMessage(R.string.course_table_edit_activity_delete_dialog_message)
                    .setPositiveButton(R.string.course_table_edit_activity_delete_positive_button,
                        {
                                dialogInterface, i ->
                            dialogInterface.dismiss()
                            deleteCourseTable()
                            finish()
                        })
                    .setNegativeButton(R.string.course_table_edit_activity_delete_negative_button,
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

    private fun deleteCourseTable() {
        if (courseTimeList != null) {
            for (value in courseTimeList!!) {
                value.delete()
            }
        }
        for (value in LitePal
            .where("tableId like ?", courseTable?.id.toString())
            .find<Course>()) {
            value.delete()
        }
        LitePal.delete(CourseTable::class.java, courseTable?.id?.toLong() ?: 0)
    }

    private fun showTimeDialog(init: (CourseTimeDialog.() -> Unit) ?= null) {
        val dialog = CourseTimeDialog(this, R.style.DialogTheme)
        dialog.show {
            onButtonClickListener = {
                    dialog: Dialog, witch: Int, startTime: String?, endTime: String? ->
                if (witch == CourseTimeDialog.BUTTON_POSITIVE) {
                    val courseTime = selectedCourseTime ?: CourseTime(0, courseTable?.id ?: 0)

                    courseTime.startTime = startTime
                    courseTime.endTime = endTime
                    courseTime.save()

                    if (selectedCourseTime == null) {
                        courseTimeList?.add(courseTime)
                    }
                    courseTimeList?.sortWith(
                        compareBy({item -> item.getStartTimeValue()},
                            {item -> item.getEndTimeValue()}))
                    courseTimeAdapter?.notifyDataSetChanged()
                }
                dialog.dismiss()
                selectedCourseTime = null
            }
            if (init != null) {
                dialog.init()
            }
        }
    }

    private val onViewClick : (view: View) -> Unit = {
        when (it) {
            addButton -> showTimeDialog()
            defaultCheckBox -> {
                if (defaultCheckBox.isChecked) {
                    val courseTableList = LitePal.where("isDefault like ?", "1")
                        .find<CourseTable>()
                    for (value in courseTableList) {
                        value.isDefault = 0
                        value.save()
                    }
                }
            }
        }
    }

    private val onItemClick : ((adapter: RecyclerView.Adapter<CourseTimeAdapter.ViewHolder>, position: Int) -> Unit) = {
            adapter: RecyclerView.Adapter<CourseTimeAdapter.ViewHolder>, position: Int ->
        when (adapter) {
            courseTimeAdapter -> {
                showTimeDialog {
                    selectedCourseTime = courseTimeList?.get(position)
                    setTime(courseTimeList?.get(position)?.startTime, courseTimeList?.get(position)?.endTime)
                }
            }
        }
    }

    private val onItemLongClick : ((adapter: RecyclerView.Adapter<CourseTimeAdapter.ViewHolder>, position: Int) -> Unit) = {
            adapter: RecyclerView.Adapter<CourseTimeAdapter.ViewHolder>, position: Int ->
        when (adapter) {
            courseTimeAdapter -> {
                val dialog = AlertDialog.Builder(this, R.style.DialogTheme)
                    .setMessage("确认删除课程时间 ${courseTimeList?.get(position)?.startTime}-${courseTimeList?.get(position)?.endTime}")
                    .setPositiveButton("确认",
                        {
                            dialogInterface, i ->
                            courseTimeList?.get(position)?.delete()
                            courseTimeList?.removeAt(position)
                            courseTimeAdapter?.notifyItemRemoved(position)
                        })
                    .create()
                dialog.show()
            }
        }
    }
}
