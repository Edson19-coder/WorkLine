package com.example.workline.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.workline.ChatActivity
import com.example.workline.R
import com.example.workline.modelos.Message
import kotlinx.android.synthetic.main.message_element.view.*

class MessageAdapter(val context: FragmentActivity?, val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        fun setData(message: Message) {
            itemView.titleTextView.text = message.emitter
            itemView.groupTextView.text = message.content
            itemView.pointsTextView.text = message.created_at
        }

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            when(v!!.id) {
                R.id.idFrameLayoutCard -> {
                    val  activityIntent =  Intent(context,ChatActivity::class.java)
                    //activityIntent.putExtra(ALBUM_POSITION,this.albumPosition)
                    context?.startActivity(activityIntent)
                }
            }
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