package com.example.workline.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.workline.ChatActivity
import com.example.workline.R
import com.example.workline.TaskActivity
import com.example.workline.modelos.Task
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.task_element.view.*

class TaskAdapter (val context: FragmentActivity?, val tasks: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(task: Task) {

            if(task.imageGroup != null) {
                Picasso.get().load(task.imageGroup).into(itemView.imageView3)
            }

            itemView.groupTextView.text = task.groupName
            itemView.titleTextView.text = task.title

            itemView.cvTask.setOnClickListener {
                val activityIntent = Intent(context, TaskActivity::class.java)
                activityIntent.putExtra("idGroup", task.idGroup)
                activityIntent.putExtra("idSubGroup", task.idSubGroup)
                activityIntent.putExtra("idTask", task.id)
                context?.startActivity(activityIntent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_element, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tasks[position])
    }

}