package com.example.notepad.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.notepad.MyApplication
import com.example.notepad.R
import com.example.notepad.tool.Tool
import com.example.notepad.dao.DBService
import com.example.notepad.pojo.Affairs
import com.example.notepad.service.NoticeService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


open class EditActivity : AppCompatActivity() {
    //isNew表示是否是新建的记录
    var isNew = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        //隐藏ActionBar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //为日期选择器设置触摸事件
        val noticeDate = findViewById<EditText>(R.id.editDate)
        val title = findViewById<EditText>(R.id.editTitle)
        val content = findViewById<EditText>(R.id.editContent)
        val noticeTime = findViewById<EditText>(R.id.editTime)
        val fab = findViewById<FloatingActionButton>(R.id.editFab)
        val back = findViewById<Button>(R.id.back)

        //如果是通过编辑的方法进入，应该为标题和内容设置初始值
        if (intent.getStringExtra("title") != null) {
            isNew = false
            title.text = Editable.Factory.getInstance().newEditable(intent.getStringExtra("title"))
            content.text =
                Editable.Factory.getInstance().newEditable(intent.getStringExtra("content"))
            if (intent.getIntExtra("noticeTime", 0) != Int.MAX_VALUE) {
                val time = Tool.timeStampToTime(intent.getIntExtra("noticeTime", 0))
                val split = time.split(" ")
                noticeDate.text = Editable.Factory.getInstance().newEditable(split[0])
                noticeTime.text = Editable.Factory.getInstance().newEditable(split[1])
                noticeTime.visibility = View.VISIBLE
            }
        }

        //设置日期和时间选择器
        noticeDate.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar: Calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        var month = month + 1
                        noticeDate.text =
                            Editable.Factory.getInstance().newEditable("$year-$month-$dayOfMonth")
                        noticeTime.visibility = View.VISIBLE
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
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
                    },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
                )
                timePickerDialog.show()
            }
            false
        }

        //提交事件
        fab.setOnClickListener {
            //如果标题为空，阻止提交
            if (title.text.toString() == "") {
                Toast.makeText(this, "请输入内容再提交", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val date = noticeDate.text.toString()
            val time = noticeTime.text.toString()
            //只有在填写了两个时间框之后才会存储真正的时间
            val t=if(date == "" || time == "") {
                //只填写了一项时间，弹出警告
                if(date != "" || time != "") {
                    Toast.makeText(this, "时间未填写完整！不会提醒", Toast.LENGTH_SHORT).show()
                }
                Int.MAX_VALUE
            } else{
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                (simpleDateFormat.parse("$date $time").time / 1000).toInt()
            }
            val am =
                MyApplication.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //isNew表示是否是新建的记录
            if (isNew) {
                val affairs = Affairs(
                    0,
                    intent.getIntExtra("createTime", (Date().time / 1000).toInt()),
                    (Date().time / 1000).toInt(),
                    title.text.toString(),
                    content.text.toString(),
                    t
                )
                DBService.addAffairs(this, affairs)
            } else {
                DBService.changeAffairs(
                    this,
                    intent.getIntExtra("id", 0),
                    (Date().time / 1000).toInt(),
                    title.text.toString(),
                    content.text.toString(),
                    t
                )
                am.cancel(
                    PendingIntent.getService(
                        this,
                        intent.getIntExtra("id", 0),
                        intent,
                        PendingIntent.FLAG_MUTABLE
                    )
                )
            }
            //发送定时任务
            if (t != Int.MAX_VALUE) {
                val intent = Intent(this, NoticeService::class.java)
                intent.putExtra("title", title.text.toString())
                intent.putExtra("content", content.text.toString())
                val pi = PendingIntent.getService(
                    this,
                    intent.getIntExtra("id", 0),
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )
                am.set(AlarmManager.RTC_WAKEUP, t.toLong() * 1000, pi)
            }
            finish()

        }

        //返回键响应
        back.setOnClickListener {
            finish()
        }
    }


}