package com.example.notepad.pojo
//事务pojo类
data class Affairs(val id: Int =0, val createTime:Int, val updateTime:Int, val title:String, val content:String, val noticeTime:Int,val noticed: Int=0)