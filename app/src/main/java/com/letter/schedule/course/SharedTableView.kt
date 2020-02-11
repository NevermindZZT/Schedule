package com.letter.schedule.course

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.preference.PreferenceManager
import com.letter.schedule.R
import kotlinx.android.synthetic.main.layout_shared_table.view.*
import org.litepal.LitePal
import org.litepal.extension.find

/**
 * 分享课程表View
 * @property courseTableId Int 课程表id
 * @constructor 构造一个View
 */
class SharedTableView @JvmOverloads
constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0):
    CardView(context, attrs, defStyleAttr) {

    var courseTableId : Int = 0
    set(value) {
        field = value
        loadCourseTable(value)
    }

    init {
        radius = context.resources.displayMetrics.density * 8
        elevation = 0f
        LayoutInflater.from(context).inflate(R.layout.layout_shared_table, this)
    }

    /**
     * 加载课程表
     * @param tableId Int 课程表id
     */
    private fun loadCourseTable(tableId: Int) {
        layoutParams = ViewGroup.LayoutParams(1080, ViewGroup.LayoutParams.WRAP_CONTENT)

        courseView.measure(
            MeasureSpec.makeMeasureSpec(1080 - (context.resources.displayMetrics.density * 16).toInt(),
                MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(1920, MeasureSpec.AT_MOST))
        courseView.layout(0, 0, courseView.measuredWidth, courseView.measuredHeight)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        courseView.startOfWeek = sharedPreferences.getString("start_of_week", "1")?.toInt() ?: 1
        courseView.courseHeight =
            (sharedPreferences.getString("course_height", "64")?.toInt() ?: 64) *
                    resources.displayMetrics.density
        courseView.showEndTime = sharedPreferences.getBoolean("show_end_time", false)
        courseView.showTimeIndex = sharedPreferences.getBoolean("show_time_index", true)
        courseView.showCourseBorder = sharedPreferences.getBoolean("show_course_border", false)

        courseView.weekText = resources.getStringArray(R.array.week_title_content)
        courseView.initWeekTitle()

        tableNameText.text = LitePal.find<CourseTable>(tableId.toLong())?.name

        courseView.courseTimeList =
            LitePal.where("tableId like ?", tableId.toString())
                .find<CourseTime>().toMutableList()
        courseView.courseList =
            LitePal.where("tableId like ?", tableId.toString())
                .find<Course>().toMutableList()
    }

    /**
     * 创建View
     */
    private fun create() {
        measure(MeasureSpec.makeMeasureSpec(1080, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(3840, MeasureSpec.AT_MOST))
        layout(0, 0, measuredWidth, measuredHeight)
    }

    /**
     * 获取View位图
     * @return Bitmap View生成的位图
     */
    fun getBitmap(): Bitmap {
        create()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}