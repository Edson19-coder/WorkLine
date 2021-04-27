package com.example.workline.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.workline.R
import com.example.workline.modelos.Message
import com.example.workline.modelos.MessageGroup
import kotlinx.android.synthetic.main.message_chat_element.view.*

class MessageGroupInChatAdapter (val messages: MutableList<MessageGroup>) :
    RecyclerView.Adapter<MessageGroupInChatAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(message: MessageGroup) {
            itemView.textViewMessageInChat.text = message.content

            val params = itemView.contenedorMensaje.layoutParams

            if(message.mine) {
                val newParams = FrameLayout.LayoutParams(
                    params.width,
                    params.height,
                    Gravity.END
                )
                itemView.contenedorMensaje.layoutParams = newParams
            } else {
                val newParams = FrameLayout.LayoutParams(
                    params.width,
                    params.height,
                    Gravity.START
                )
                itemView.contenedorMensaje.layoutParams = newParams
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.message_chat_element, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(messages[position])
    }

}