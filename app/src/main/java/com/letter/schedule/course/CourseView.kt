package com.letter.schedule.course

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import com.letter.schedule.R
import kotlinx.android.synthetic.main.layout_course_class_item.view.*

/**
 * 课程View
 *
 * @constructor 构造一个课程表View
 * @author Letter(zhangkeqiang@gmail.com)
 * @since 1.0.0
 */
class CourseView @JvmOverloads
        constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0, defStyleRes: Int=0):
        FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        const val TAG = "CourseView"

        const val DEFAULT_WEEK_TITLE_HEIGHT_DP = 16
        const val DEFAULT_COURSE_TIME_TITLE_WIDTH_DP = 32
        const val DEFAULT_COURSE_HEIGHT_DP = 64

        const val WEEK_SUNDAY = 0
        const val WEEK_MONDAY = 1
        const val WEEK_TUESDAY = 2
        const val WEEK_WEDNESDAY = 3
        const val WEEK_THURSDAY = 4
        const val WEEK_FRIDAY = 5
        const val WEEK_SATURDAY = 6

    }

    /**
     * 课程高度
     */
    private var courseHeight = 0f

    /**
     * 星期标题栏高度
     */
    private var weekTitleHeight = 0f

    /**
     * 课程时间标题栏宽度
     */
    private var courseTimeTitleWidth = 0f

    /**
     * 一周开始
     */
    private var startOfWeek = WEEK_SUNDAY

    /**
     * 滚动视图
     */
    private var scrollView : NestedScrollView

    /**
     * 主布局，包含时间布局和课程布局，添加至滚动视图中
     */
    private var mainLayout : FrameLayout

    /**
     * 课程布局
     */
    private var classLayout : ClassView

    /**
     * 星期标题Layout
     */
    private var weekLayout : LinearLayout

    /**
     * 时间标题Layout
     */
    private var timeLayout : LinearLayout

    /**
     * 测量得到的View宽度
     */
    private var widthMeasured = 0

    /**
     * 测量得到的View高度
     */
    private var heightMeasured = 0

    /**
     * 点击监听
     */
    var onClickListener: ((hasClass: Boolean,
                           classItemView: ClassItemView?,
                           course: Course?,
                           courseTime: CourseTime?,
                           weekday: Int) -> Unit) ?= null

    /**
     * 课程时间List
     */
    var courseTimeList = mutableListOf<CourseTime>()
    set(value) {
        field = value
        initTimeTitle()
    }

    /**
     * 课程List
     */
    var courseList = mutableListOf<Course>()
    set(value) {
        field = value
        initClass()
    }

    /**
     * 星期标题文本
     */
    var weekText = arrayOf("SUN", "MON", "TUES", "WED", "THUR", "FRI", "STA")
    set(value) {
        field = value
        initWeekTitle()
    }

    init {
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.CourseView)

        courseHeight = attrArray.getDimension(
            R.styleable.CourseView_courseHeight,
            context.resources.displayMetrics.density * DEFAULT_COURSE_HEIGHT_DP)
        weekTitleHeight = attrArray.getDimension(
            R.styleable.CourseView_weekTitleHeight,
            context.resources.displayMetrics.density * DEFAULT_WEEK_TITLE_HEIGHT_DP)
        courseTimeTitleWidth = attrArray.getDimension(
            R.styleable.CourseView_courseTimeTitleWidth,
            context.resources.displayMetrics.density * DEFAULT_COURSE_TIME_TITLE_WIDTH_DP)
        startOfWeek = attrArray.getInt(R.styleable.CourseView_startOfWeek, WEEK_MONDAY)

        attrArray.recycle()

        scrollView = NestedScrollView(context)
        mainLayout = FrameLayout(context)
        classLayout = ClassView(context)
        weekLayout = LinearLayout(context)
        timeLayout = LinearLayout(context)

        this.post {
            initLayout()
            initWeekTitle()
        }
    }

    /**
     * 测量控件大小
     * @param widthMeasureSpec Int widthMeasureSpec
     * @param heightMeasureSpec Int heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureWidthSize(widthMeasureSpec), measureHeightSize(heightMeasureSpec))
    }

    /**
     * 测量View宽度
     * @param measureSpec Int measureSpec
     * @return Int 测量得到的宽度
     */
    private fun measureWidthSize(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        widthMeasured = if (specMode == MeasureSpec.EXACTLY) specSize
            else (context.resources.displayMetrics.density * 350 + courseTimeTitleWidth).toInt()
        // 修正由于计算出的课程宽度小数部分不为0造成的View宽度不能完全填充的问题
        courseTimeTitleWidth =
            (widthMeasured - ((widthMeasured - courseTimeTitleWidth) / 7).toInt() * 7).toFloat()
        return widthMeasured
    }

    /**
     * 测量View高度
     * @param measureSpec Int measureSpec
     * @return Int 测量得到的高度
     */
    private fun measureHeightSize(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        heightMeasured = if (specMode == MeasureSpec.EXACTLY) specSize
            else (courseHeight * courseTimeList.size + weekTitleHeight).toInt()
        return heightMeasured
    }

    /**
     * 初始化星期和时间布局
     */
    private fun initLayout() {
        val weekLayoutParams = LayoutParams(
            (widthMeasured - courseTimeTitleWidth).toInt(),
            weekTitleHeight.toInt())
        weekLayoutParams.leftMargin = courseTimeTitleWidth.toInt()
        weekLayoutParams.topMargin = 0
        weekLayout.layoutParams = weekLayoutParams
        addView(weekLayout)

        val scrollViewParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        scrollViewParams.leftMargin = 0
        scrollViewParams.topMargin = weekTitleHeight.toInt()
        scrollView.layoutParams = scrollViewParams
        addView(scrollView)

        val mainLayoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        mainLayout.layoutParams = mainLayoutParams
        scrollView.addView(mainLayout)

        val timeLayoutParams = LayoutParams(
            courseTimeTitleWidth.toInt(),
            (courseTimeList.size * courseHeight).toInt())
        timeLayoutParams.leftMargin = 0
        timeLayoutParams.topMargin = 0
        timeLayout.layoutParams = timeLayoutParams
        timeLayout.orientation = LinearLayout.VERTICAL
        mainLayout.addView(timeLayout)

        val classLayoutParams = LayoutParams(
            (widthMeasured - courseTimeTitleWidth).toInt(),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        classLayoutParams.leftMargin = courseTimeTitleWidth.toInt()
        classLayoutParams.topMargin = 0
        classLayout.layoutParams = classLayoutParams
        classLayout.onClickListener = onEmptyClassViewClicked
        mainLayout.addView(classLayout)
    }

    /**
     * 初始化星期数据
     */
    private fun initWeekTitle() {
        weekLayout.removeAllViews()
        val layoutParams = LinearLayout.LayoutParams(
            ((widthMeasured - courseTimeTitleWidth) / 7).toInt(),
            weekTitleHeight.toInt())
        for (i in 0..6) {
            val weekItemView = WeekItemView(context)
            weekItemView.layoutParams = layoutParams
            weekItemView.week = weekText[(i + startOfWeek) % 7]
            weekLayout.addView(weekItemView)
        }
    }

    /**
     * 初始化课程时间
     */
    private fun initTimeTitle() {
        timeLayout.removeAllViews()
        timeLayout.layoutParams.width = courseTimeTitleWidth.toInt()
        timeLayout.layoutParams.height = (courseTimeList.size * courseHeight).toInt()
        val layoutParams = LayoutParams(
            (courseTimeTitleWidth).toInt(),
            courseHeight.toInt())
        courseTimeList.sortWith(compareBy({it.getStartTimeValue()}, {it.getEndTimeValue()}))
        var index = 1
        for (value in courseTimeList) {
            val timeItemView = TimeItemView(context)
            timeItemView.layoutParams = layoutParams
            timeItemView.courseTime = value
            timeItemView.courseIndex = index++
            timeLayout.addView(timeItemView)
        }
    }

    /**
     * 初始化课程
     */
    private fun initClass() {
        classLayout.removeAllViews()
        classLayout.layoutParams.height = (courseTimeList.size * courseHeight).toInt()
        val courseWidth = ((widthMeasured - courseTimeTitleWidth) / 7).toInt()
        for (course in courseList) {
            for (i in 0 until courseTimeList.size - 1) {
                if (course.startTime == courseTimeList[i].startTime) {
                    val layoutParams = LayoutParams(
                        courseWidth,
                        (course.length * courseHeight).toInt())
                    layoutParams.topMargin = (i * courseHeight).toInt()
                    layoutParams.leftMargin = ((course.weekDay - startOfWeek + 7) % 7) * courseWidth
                    val classItemView = ClassItemView(context)
                    classItemView.layoutParams = layoutParams
                    classItemView.course = course
                    classItemView.mainLayout.setOnClickListener {
                        onClassItemClicked(course, classItemView)
                    }
                    modifyClassBackground(classItemView, course.color)
                    classLayout.addView(classItemView)
                    break
                }
            }
        }
    }

    /**
     * 修改课程背景色
     * @param classItemView ClassItemView? 课程View
     * @param color Int 课程背景颜色
     */
    private fun modifyClassBackground(classItemView: ClassItemView, color: Int) {
        val stateList = arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_activated),
            intArrayOf())
        val stateColorList = intArrayOf(
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY)
        val colorStateList = ColorStateList(stateList, stateColorList)

        val colorDrawable = ColorDrawable(color)

        val rippleDrawable = RippleDrawable(colorStateList, colorDrawable, null)
        classItemView.mainLayout.background = rippleDrawable
    }

    /**
     * 课程View空白处点击处理
     */
    private val onEmptyClassViewClicked: ((x: Int, y: Int) -> Unit) = {
        x: Int, y: Int ->
        val column = y / courseHeight
        val row = x / (((widthMeasured - courseTimeTitleWidth) / 7).toInt())
        onClickListener?.invoke(false,
            null,
            null,
            courseTimeList[column.toInt()],
            (startOfWeek + row) % 7)
    }

    /**
     * 课程表点击事件处理
     */
    private val onClassItemClicked: ((course: Course, view: ClassItemView) -> Unit) = {
        course: Course, view: ClassItemView ->
        onClickListener?.invoke(true,
            view,
            course,
            null,
            0)
    }

    /**
     * 通知课程时间变化
     */
    fun notifyTimeChanged() {
        initTimeTitle()
    }

    /**
     * 通知课程数据变化
     */
    fun notifyClassChanged() {
        initClass()
    }

    /**
     * 通知课程更新
     * @param course Course 课程
     * @param classItemView ClassItemView 课程视图
     */
    fun notifyClassUpdate(course: Course, classItemView: ClassItemView) {
        val courseWidth = ((widthMeasured - courseTimeTitleWidth) / 7).toInt()
        for (i in 0 until courseTimeList.size - 1) {
            if (course.startTime == courseTimeList[i].startTime) {
                val layoutParams = classItemView.layoutParams
                if (layoutParams is LayoutParams) {
                    layoutParams.width = courseWidth
                    layoutParams.height = (course.length * courseHeight).toInt()
                    layoutParams.topMargin = (i * courseHeight).toInt()
                    layoutParams.leftMargin = ((course.weekDay - startOfWeek + 7) % 7) * courseWidth
                }
                classItemView.course = course
                modifyClassBackground(classItemView, course.color)
                break
            }
        }
    }
}