package com.example.workline.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workline.R
import com.example.workline.modelos.Message
import kotlinx.android.synthetic.main.message_element.view.*

class MessageAdapter(val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(message: Message) {
            itemView.titleTextView.text = message.emitter
            itemView.groupTextView.text = message.content
            itemView.pointsTextView.text = message.created_at
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.message_element, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(messages[position])
    }
}