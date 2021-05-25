package com.example.workline.adapters


import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.workline.R
import com.example.workline.modelos.Message
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.message_chat_element.view.*

class MessageInChatAdapter(val messages: MutableList<Message>) :
        RecyclerView.Adapter<MessageInChatAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(message: Message) {
            itemView.textViewMessageInChatUserName.text = message.emmiterName
            itemView.textViewMessageInChatTime.text = message.created_at
            if(message.typeMessage == 1) {
                itemView.textViewMessageInChat.visibility = View.GONE
                itemView.imageViewMessage.visibility = View.VISIBLE
                Picasso.get().load(Uri.parse(message!!.content)).into(itemView.imageViewMessage)
            } else {
                itemView.textViewMessageInChat.text = message.content
            }

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