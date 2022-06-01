package com.example.notepad.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.MyApplication
import com.example.notepad.R
import com.example.notepad.tool.Tool
import com.example.notepad.dao.DBService
import com.example.notepad.pojo.Affairs
import java.util.*

class TodoListAdapter(private val context: Context, private val todoList: LinkedList<Affairs>): RecyclerView.Adapter<TodoListAdapter.ViewHolder>(){
    var checked=false
    val removeList= LinkedList<Int>()
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val title: TextView =view.findViewById(R.id.title)
        val checkBox: CheckBox =view.findViewById(R.id.checkbox)
        val item: ConstraintLayout =view.findViewById(R.id.item)
        val updateTime: TextView =view.findViewById(R.id.updateTime)
        val createTime: TextView =view.findViewById(R.id.createTime)
        val view: View =view
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.todolist_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(checked) holder.checkBox.visibility= View.VISIBLE
        else holder.checkBox.visibility= View.GONE
        holder.item.background.alpha= 150
        if(position%2==1) holder.item.setBackgroundResource(R.drawable.blue)
        holder.title.text=todoList[position].title
        holder.updateTime.text= Tool.timeStampToTime(todoList[position].updateTime)
        holder.createTime.text= Tool.timeStampToTime(todoList[position].createTime)

        //设置选中删除事件，将记录id添加到List中
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) removeList.add(todoList[position].id)
            else removeList.remove(todoList[position].id)
        }

        //长按删除弹窗
        holder.view.setOnLongClickListener {
            if(!checked){
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
            }
            true
        }

        //短按详情弹窗
        holder.view.setOnClickListener {
            if(checked){
                holder.checkBox.isChecked=!holder.checkBox.isChecked
            }else{
                AlertDialog.Builder(context).apply {
                    setTitle(todoList[position].title)
                    setCancelable(false)
                    var time="暂无提醒时间"
                    if(todoList[position].noticeTime!=Int.MAX_VALUE){
                        time= Tool.timeStampToTime(todoList[position].noticeTime)
                    }
                    setMessage(todoList[position].content+"\n提醒时间：$time")
                    setPositiveButton("修改"){ _, _ ->
                        val intent= Intent(context,EditActivity::class.java)
                        intent.putExtra("title",todoList[position].title)
                        intent.putExtra("id",todoList[position].id)
                        intent.putExtra("content",todoList[position].content)
                        intent.putExtra("createTime",todoList[position].createTime)
                        intent.putExtra("noticeTime",todoList[position].noticeTime)
                        context.startActivity(intent)
                    }
                    setNegativeButton("取消"){ _, _ ->
                    }
                    show()
                }
            }
        }

        //如果这个任务已经过期了，标红展示
        if((Date().time /1000).toInt()>todoList[position].noticeTime){
            holder.title.setTextColor(Color.parseColor("#99EF0C0C"))
        }
    }


    override fun getItemCount(): Int {
        return todoList.size
    }

    //删除一项
    private fun remove(i: Affairs){
        todoList.remove(i)
        notifyDataSetChanged()
    }

}