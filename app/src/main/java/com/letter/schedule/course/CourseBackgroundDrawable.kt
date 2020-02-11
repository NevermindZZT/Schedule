package com.letter.schedule.course

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

/**
 * 课程背景Drawable
 * @property width Int 宽度
 * @property height Int 高度
 * @property row Int 课程行数
 * @property color Int 边框颜色
 * @property paint Paint 画笔
 * @constructor 构建一个Drawable
 * @author Letter(neverminzzt@gmial.com)
 * @since 1.0.1
 */
class CourseBackgroundDrawable
    constructor(var width: Int = 0, var height: Int = 0, var row: Int = 0, var color: Int = 0) : Drawable() {

    private val paint: Paint

    init {
        paint = Paint()
    }

    override fun draw(canvas: Canvas) {
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        for (i in 1..(height / row)) {
            canvas.drawLine(0f,
                (height * i / row).toFloat() - 1,
                width.toFloat(),
                (height * i / row).toFloat() - 1,
                paint)
        }
        for (i in 1..7) {
            canvas.drawLine((width * i / 7).toFloat() - 1,
                0f,
                (width * i / 7).toFloat() - 1,
                height.toFloat(),
                paint)
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}