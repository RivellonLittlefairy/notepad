package com.example.notepad

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.dao.DBService
import com.example.notepad.pojo.Affairs
import java.util.*

class MainActivity : AppCompatActivity() {
    private val todoList=LinkedList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //初始化recyclerView
        initTitle()
        val layoutManager=LinearLayoutManager(this)
        val adapter=TodoListAdapter(todoList)
        val recyclerView=findViewById<RecyclerView>(R.id.todoList)
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=adapter


        //初始化数据库
        //DBService.addAffairs(this, Affairs(1,1,1,"213","123213123123",3))
        DBService.deleteAffairs(this,1)
        DBService.deleteAffairs(this,2)
        DBService.deleteAffairs(this,3)
        DBService.changeAffairs(this,1,0,"0","123",0)
        val todoList1 = DBService.getTodoList(this,false)
        Toast.makeText(this, todoList1[0].title,Toast.LENGTH_SHORT).show()
    }

    private fun initTitle() {
        todoList.add(1)
        todoList.add(2)
        todoList.add(3)
        todoList.add(32)
        todoList.add(333)
        todoList.add(3344)
    }
}
class TodoListAdapter(private val todoList:List<Int>): RecyclerView.Adapter<TodoListAdapter.ViewHolder>(){
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val title:TextView=view.findViewById(R.id.title)
        val text:TextView=view.findViewById(R.id.text)
    }
    class obs(): RecyclerView.AdapterDataObserver(){
        override fun onChanged() {
            super.onChanged()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListAdapter.ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.todolist_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoListAdapter.ViewHolder, position: Int) {
        holder.title.text=todoList[position].toString()
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

}