package com.example.notepad.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notepad.R
import com.example.notepad.dao.DBService
import com.example.notepad.pojo.Affairs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


open class EditActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //为日期选择器设置触摸事件
        val noticeDate = findViewById<EditText>(R.id.editDate)
        val title = findViewById<EditText>(R.id.editTitle)
        val content = findViewById<EditText>(R.id.editContent)
        val noticeTime = findViewById<EditText>(R.id.editTime)
        //设置日期和时间时候，会为这个变量加一，最后值为2说明设置好了提醒日期
        var notice=0
        noticeDate.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar: Calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        noticeDate.text =
                            Editable.Factory.getInstance().newEditable("$year-$month-$dayOfMonth")
                        noticeTime.visibility= View.VISIBLE
                        notice++
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
                true
            }
            false
        }

        //为时间选择器设置触摸事件
        noticeTime.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar: Calendar = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(
                    this, 2,
                    { _, hourOfDay, minute ->
                        noticeTime.text =
                            Editable.Factory.getInstance().newEditable("$hourOfDay:$minute:00")
                        notice++
                    },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
                )
                timePickerDialog.show()
                true
            }
            false
        }

        //提交事件
        val fab = findViewById<FloatingActionButton>(R.id.editFab)
        fab.setOnClickListener {
            val t=if (notice==2){
                val date=noticeDate.text.toString()
                val time=noticeTime.text.toString()
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                (simpleDateFormat.parse("$date $time").time/1000).toInt()
            }else{
                Int.MAX_VALUE
            }
            val affairs=Affairs(0,intent.getIntExtra("createTime", (Date().time /1000).toInt()),(Date().time /1000).toInt(),title.text.toString(), content.text.toString(),t)
            //Toast.makeText(this,t,Toast.LENGTH_SHORT).show()
            DBService.addAffairs(this,affairs)
        }
    }


}