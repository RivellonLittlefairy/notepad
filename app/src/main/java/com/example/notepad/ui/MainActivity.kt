package com.example.notepad.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.R
import com.example.notepad.dao.DBService
import com.example.notepad.pojo.Affairs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val todoList=LinkedList<Affairs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        //隐藏ActionBar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        //初始化recyclerView
        initTitle()
        val layoutManager=LinearLayoutManager(this)
        val adapter= TodoListAdapter(todoList)
        val recyclerView=findViewById<RecyclerView>(R.id.todoList)
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=adapter

        //增添记录的悬浮按钮，为其设置事件
        val fab=findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            val intent= Intent(this,EditActivity::class.java)
            startActivity(intent)
        }

        //设置多选删除事件
        findViewById<Button>(R.id.mainMultiSelect).setOnClickListener {
            adapter.ischecked=!adapter.ischecked
            adapter.notifyDataSetChanged()
        }
    }

    private fun initTitle() {
        todoList.clear()
        todoList.addAll(DBService.getTodoList(this, true))
        todoList.addAll(DBService.getTodoList(this, false))

    }
}
class TodoListAdapter(private val todoList:List<Affairs>): RecyclerView.Adapter<TodoListAdapter.ViewHolder>(){
    var ischecked=false
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val title:TextView=view.findViewById(R.id.title)
        val checkBox: CheckBox =view.findViewById(R.id.checkbox)
        val item: ConstraintLayout =view.findViewById(R.id.item)
        val updateTime: TextView =view.findViewById(R.id.updateTime)
        val createTime: TextView =view.findViewById(R.id.createTime)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.todolist_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(ischecked) holder.checkBox.visibility=View.VISIBLE
        else holder.checkBox.visibility=View.GONE
        holder.item.background.alpha= 150
        if(position%2==1) holder.item.setBackgroundResource(R.drawable.blue)
        holder.title.text=todoList[position].title
        holder.updateTime.text=timeStampToTime(todoList[position].updateTime)
        holder.createTime.text=timeStampToTime(todoList[position].createTime)
    }

    //将数据库中的时间戳转换为显示的时间
    private fun timeStampToTime(t:Int):String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(Date(java.lang.String.valueOf(t).toLong()*1000)) // 时间戳转换成时间
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

}