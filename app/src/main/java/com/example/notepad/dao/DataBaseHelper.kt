package com.example.notepad.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.core.content.contentValuesOf

class DataBaseHelper(context:Context,name:String,version:Int):SQLiteOpenHelper(context,name,null,version) {
    private val createDBSQL:String="create table memo(id integer primary key autoincrement,createTime integer,updateTime integer,title text,content text,noticeTime integer,noticed integer default 0)"
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createDBSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //暂时不做版本迭代处理
    }
}