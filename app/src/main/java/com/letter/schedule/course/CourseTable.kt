package com.letter.schedule.course

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.blankj.utilcode.util.FileUtils
import com.letter.schedule.R
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableWorkbook
import org.litepal.LitePal
import org.litepal.crud.LitePalSupport
import org.litepal.extension.find
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 课程表
 * @property id Int id
 * @property name String? 课程表名字
 * @property isDefault Int 是否为默认课程表
 * @constructor 构建一个课程表
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class CourseTable
    constructor(var id : Int = 0,
                var name : String ?= null,
                var isDefault : Int = DEFAULT_FALSE) : LitePalSupport() {

    companion object {
        const val DEFAULT_FALSE = 0
        const val DEFAULT_TRUE = 1
    }

    /**
     * 保存为Excel文件
     * @param context Context context
     * @return File? 保存后的文件
     */
    fun saveAsExcel(context: Context): File? {
        val format = WritableCellFormat(WritableFont(WritableFont.ARIAL, 12))
        val courseTimeList = LitePal.where("tableId like ?", id.toString())
            .find<CourseTime>().toMutableList()
        courseTimeList.sortWith(compareBy({it.getStartTimeValue()}, {it.getEndTimeValue()}))
        val courseList = LitePal.where("tableId like ?", id.toString())
            .find<Course>()
        try {
            val fileName = "$name-${SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
                .format(Date(System.currentTimeMillis()))}.xls"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val workbook = Workbook.createWorkbook(file)
            val sheet = workbook.createSheet(name, 0)
            val weekText = context.resources.getStringArray(R.array.week_title_content)
            for (i in 0..6) {
                sheet.addCell(Label(i + 1, 0, weekText[i], format))
            }
            for (i in 0 until courseTimeList.size) {
                sheet.addCell(Label(
                    0,
                    i + 1,
                    "${courseTimeList[i].startTime}-${courseTimeList[i].endTime}",
                    format))
                sheet.setRowView(i + 1, 64)
            }
            for (course in courseList) {
                for (i in 0 until courseTimeList.size) {
                    if (course.startTime == courseTimeList[i].startTime) {
                        sheet.addCell(Label(
                            course.weekDay + 1,
                            i + 1,
                            "${course.name}\r\n${course.teacher}@${course.location}\r\n${course.length}课时",
                            format))
                        if (course.length > 1) {
                            sheet.mergeCells(course.weekDay + 1, i + 1, course.weekDay + 1, i + course.length)
                        }
                    }
                }
            }
            workbook.write()
            workbook.close()
            return file
        } catch (exception: Exception) {

        }
        return null
    }

    /**
     * 保存为图片
     * @param context Context context
     * @return File? 保存后的文件
     */
    fun saveAsPicture(context: Context): File? {
        val sharedTableView = SharedTableView(context)
        sharedTableView.courseTableId = id
        val bitmap = sharedTableView.getBitmap()
        if (FileUtils.createOrExistsDir(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))) {
            try {
                val fileName = "$name-${SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
                    .format(Date(System.currentTimeMillis()))}.jpg"
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
                val fileOutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                fileOutputStream.flush()
                context.sendBroadcast(
                    Intent(Intent.ACTION_MEDIA_SCANNER_STARTED,
                    Uri.fromFile(File(file.path)))
                )

                return file
            } catch (exception: Exception) {
            }
        }
        return null
    }
}