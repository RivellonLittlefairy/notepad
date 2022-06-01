package com.example.notepad.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.R
import com.example.notepad.dao.DBService
import com.example.notepad.pojo.Affairs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() {
    private val todoList=LinkedList<Affairs>()
    lateinit var fab:FloatingActionButton
    //需要根据是否进入多选模式来变化fab的图形， 因此adapter的checked必须能被外界访问
    lateinit var adapter:TodoListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        //隐藏ActionBar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        //初始化recyclerView
        initAdapter()


        //增添记录的悬浮按钮，为其设置事件
        fab=findViewById(R.id.fabAdd)
        fab.setOnClickListener {
            if(adapter.checked){
                for(i in adapter.removeList){
                    DBService.deleteAffairs(this,i)
                }
                changeFabIcon()
                adapter.checked=!adapter.checked
                initAdapter()
            }else{
                val intent= Intent(this,EditActivity::class.java)
                startActivity(intent)
            }
        }

        //设置多选删除事件
        findViewById<Button>(R.id.mainMultiSelect).setOnClickListener {
            changeFabIcon()
            adapter.checked=!adapter.checked
            adapter.notifyDataSetChanged()
        }
    }

    //从数据库中加载数据
    private fun initAdapter() {
        todoList.clear()
        todoList.addAll(DBService.getTodoList(this, true))
        todoList.addAll(DBService.getTodoList(this, false))
        adapter= TodoListAdapter(this,todoList)
        val layoutManager=LinearLayoutManager(this)
        val recyclerView=findViewById<RecyclerView>(R.id.todoList)
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=adapter
    }

    //点击多选按钮进入编辑模式以后，悬浮按钮变成删除功能
    private fun changeFabIcon(){
        if(adapter.checked) fab.setImageResource(R.drawable.add)
        else fab.setImageResource(R.drawable.remove)
    }

    override fun onResume() {
        initAdapter()
        super.onResume()
    }
}