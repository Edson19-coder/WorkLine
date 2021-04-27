package com.example.workline.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.workline.ChatActivity
import com.example.workline.ChatGroupActivity
import com.example.workline.R
import com.example.workline.modelos.Message
import com.example.workline.modelos.MessageGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.message_element.view.*

class GroupAdapter (val context: FragmentActivity?, val messages: MutableList<MessageGroup>) :
        RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    private lateinit var auth: FirebaseAuth

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(message: MessageGroup) {
            //itemView.titleTextView.text = user.nombre + " " + user.lastName
            auth = Firebase.auth
            if(message.emitter.equals(auth.uid))
                itemView.groupTextView.text = "Tu: " + message.content
            else
                itemView.groupTextView.text = message.content
            itemView.textViewDateSend.text = message.created_at
            itemView.titleTextView.text = message.nameGroup
            itemView.idFrameLayoutCard.setOnClickListener {
                val  activityIntent =  Intent(context, ChatGroupActivity::class.java)
                activityIntent.putExtra("carrera", message.nameGroup)
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