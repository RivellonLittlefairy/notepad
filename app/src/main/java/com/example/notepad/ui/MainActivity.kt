package com.example.notepad.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.MyApplication
import com.example.notepad.R
import com.example.notepad.dao.DBService
import com.example.notepad.pojo.Affairs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
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
class TodoListAdapter(val context:Context,private val todoList:LinkedList<Affairs>): RecyclerView.Adapter<TodoListAdapter.ViewHolder>(){
    var checked=false
    val removeList=LinkedList<Int>()
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val title:TextView=view.findViewById(R.id.title)
        val checkBox: CheckBox =view.findViewById(R.id.checkbox)
        val item: ConstraintLayout =view.findViewById(R.id.item)
        val updateTime: TextView =view.findViewById(R.id.updateTime)
        val createTime: TextView =view.findViewById(R.id.createTime)
        val view:View=view
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.todolist_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(checked) holder.checkBox.visibility=View.VISIBLE
        else holder.checkBox.visibility=View.GONE
        holder.item.background.alpha= 150
        if(position%2==1) holder.item.setBackgroundResource(R.drawable.blue)
        holder.title.text=todoList[position].title
        holder.updateTime.text=timeStampToTime(todoList[position].updateTime)
        holder.createTime.text=timeStampToTime(todoList[position].createTime)

        //设置选中删除事件，将记录id添加到List中
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) removeList.add(todoList[position].id)
            else removeList.remove(todoList[position].id)
        }

        holder.view.setOnLongClickListener {
            AlertDialog.Builder(context).apply {
                setTitle("确认删除这一项？")
                setCancelable(false)
                setPositiveButton("删除"){ _, _ ->
                    DBService.deleteAffairs(MyApplication.context,todoList[position].id)
                    remove(todoList[position])
                }
                setNegativeButton("取消"){ _, _ ->
                }
                show()
            }
            true
        }

        holder.view.setOnClickListener {
            Toast.makeText(MyApplication.context,"up",Toast.LENGTH_SHORT).show()
        }
    }

    //将数据库中的时间戳转换为显示的时间
    private fun timeStampToTime(t:Int):String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(Date(java.lang.String.valueOf(t).toLong()*1000)) // 时间戳转换成时间
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    //删除一项
    fun remove(i:Affairs){
        todoList.remove(i)
        notifyDataSetChanged()
    }

}