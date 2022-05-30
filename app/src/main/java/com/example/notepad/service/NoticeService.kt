package com.example.notepad.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toolbar
import com.example.notepad.Tool

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