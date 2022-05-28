package com.example.notepad.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.health.TimerStat
import com.example.notepad.pojo.Affairs
import java.security.Timestamp
import java.util.*

object DBService {
    //获取数据库
    private fun getDb(context: Context): SQLiteDatabase {
        val dbHelper = DataBaseHelper(context, "memo", 1)
        return dbHelper.writableDatabase
    }

    @SuppressLint("Range")
    //获取备忘录记录
    fun getTodoList(context: Context, overdue: Boolean): List<Affairs> {
        val res = LinkedList<Affairs>()
        val db = getDb(context)
        val nowTime=System.currentTimeMillis()/1000
        val cursor = if (overdue){
            db.query("memo", null, "noticeTime<=?", arrayOf(nowTime.toString()), null, null, null)
        } else{
            db.query("memo", null, "noticeTime>?", arrayOf(nowTime.toString()), null, null, null)
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val createTime = cursor.getInt(cursor.getColumnIndex("createTime"))
                val updateTime = cursor.getInt(cursor.getColumnIndex("updateTime"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val noticeTime = cursor.getInt(cursor.getColumnIndex("noticeTime"))
                res.add(Affairs(id, createTime, updateTime, title, content, noticeTime))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return res
    }

    //增加一行备忘录记录
    fun addAffairs(context: Context, affairs: Affairs) {
        val db = getDb(context)
        val values = ContentValues().apply {
            put("createTime", affairs.createTime)
            put("updateTime", affairs.updateTime)
            put("title", affairs.title)
            put("content", affairs.content)
            put("noticeTime", affairs.noticeTime)
        }
        db.insert("memo", null, values)
    }

    //修改一行备忘录记录
    fun changeAffairs(
        context: Context,
        id: Int,
        updateTime: Int,
        title: String,
        content: String,
        noticeTime: Int
    ) {
        val db = getDb(context)
        val values = ContentValues()
        values.put("updateTime", updateTime)
        values.put("title", title)
        values.put("content", content)
        values.put("noticeTime", noticeTime)
        db.update("memo", values, "id=?", arrayOf(id.toString()))
    }
    //删除记录
    fun deleteAffairs(context: Context,id: Int){
        val db= getDb(context)
        db.delete("memo","id=?", arrayOf(id.toString()))
    }
}