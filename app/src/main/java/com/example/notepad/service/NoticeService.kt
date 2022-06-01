package com.example.notepad.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.notepad.tool.Tool
//这个Service用来接收广播，无条件被唤醒发送通知
class NoticeService : Service(){
    override fun onBind(intent: Intent?): IBinder? {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("title")
        val content = intent?.getStringExtra("title")
        Tool.sendNotification(title!!,content!!)
        return super.onStartCommand(intent, flags, startId)
    }
}