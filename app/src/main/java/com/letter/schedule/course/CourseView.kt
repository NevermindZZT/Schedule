package com.letter.schedule.course

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.letter.schedule.R

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

//    constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int):
//            this(context, attrs, defStyleAttr, 0)
//
//    constructor(context: Context, attrs: AttributeSet?=null):
//            this(context, attrs, 0, 0)
//
//    constructor(context: Context):
//            this(context, null, 0, 0)

    companion object {
        const val DEFAULT_WEEK_TITLE_HEIGHT_DP = 16
        const val DEFAULT_COURSE_TIME_TITLE_WIDTH_DP = 32

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
     * 课程时间List
     */
    var courseTimeList = mutableListOf<CourseTime>()

    /**
     * 课程List
     */
    var courseList = mutableListOf<Course>()

    init {
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.CourseView)

        courseHeight = attrArray.getDimension(R.styleable.CourseView_courseHeight, 0f)
        weekTitleHeight = attrArray.getDimension(
            R.styleable.CourseView_weekTitleHeight,
            context.resources.displayMetrics.density * DEFAULT_WEEK_TITLE_HEIGHT_DP)
        courseTimeTitleWidth = attrArray.getDimension(
            R.styleable.CourseView_courseTimeTitleWidth,
            context.resources.displayMetrics.density * DEFAULT_COURSE_TIME_TITLE_WIDTH_DP)
        startOfWeek = attrArray.getInt(R.styleable.CourseView_startOfWeek, WEEK_SUNDAY)

        attrArray.recycle()
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
        return if (specMode == MeasureSpec.EXACTLY) specSize
                else (context.resources.displayMetrics.density * 350 + weekTitleHeight).toInt()
    }

    /**
     * 测量View高度
     * @param measureSpec Int measureSpec
     * @return Int 测量得到的高度
     */
    private fun measureHeightSize(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return if (specMode == MeasureSpec.EXACTLY) specSize
                else (courseHeight * courseTimeList.size + courseTimeTitleWidth).toInt()
    }
}