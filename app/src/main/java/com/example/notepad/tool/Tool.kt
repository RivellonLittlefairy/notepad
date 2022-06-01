package com.example.notepad.tool

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.notepad.MyApplication
import com.example.notepad.R
import java.text.SimpleDateFormat
import java.util.*

object Tool {
    @RequiresApi(Build.VERSION_CODES.O)
    //创建通知通道
    fun createChanel(manager: NotificationManager) {
        val channel =
            NotificationChannel(1.toString(), "提醒事务", NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
    }
    //发送通知
    fun sendNotification(title:String, content:String){
        val manager = MyApplication.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) createChanel(manager)
        val notification=NotificationCompat.Builder(MyApplication.context,1.toString())
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.delete)
            .build()
        manager.notify(1,notification)
    }
    //将数据库中的时间戳转换为显示的时间
    fun timeStampToTime(t:Int):String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(Date(java.lang.String.valueOf(t).toLong()*1000)) // 时间戳转换成时间
    }
}