package com.example.workline.adapters

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.workline.ChatActivity
import com.example.workline.R
import com.example.workline.modelos.Message
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.message_element.view.*
import kotlinx.coroutines.awaitAll
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class MessageAdapter(val context: FragmentActivity?, val messages: MutableList<Message>) :
        RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private lateinit var auth: FirebaseAuth

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(message: Message) {
            auth = Firebase.auth
            if(message.emitter.equals(auth.uid))
                itemView.groupTextView.text = "Tu: " + message.content
            else
                itemView.groupTextView.text = message.content
            itemView.textViewDateSend.text = message.created_at
            itemView.titleTextView.text = message.nameChat
            itemView.idFrameLayoutCard.setOnClickListener {
                val  activityIntent =  Intent(context,ChatActivity::class.java)
                activityIntent.putExtra("idFriend", message.idFriendChat)
                context?.startActivity(activityIntent)
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