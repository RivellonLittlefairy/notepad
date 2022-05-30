package com.example.notepad

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

object Tool {
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChanel(manager: NotificationManager) {
        val channel =
            NotificationChannel(1.toString(), "提醒事务", NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
    }
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
}