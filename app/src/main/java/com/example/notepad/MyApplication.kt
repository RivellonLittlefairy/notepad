package com.example.notepad

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication:Application() {
    //全局获取context
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context=applicationContext
    }
}