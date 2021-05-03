package com.example.workline.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.workline.ChatActivity
import com.example.workline.R
import com.example.workline.modelos.SubGroup
import com.example.workline.modelos.UserPreview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_element.view.*
import kotlinx.android.synthetic.main.user_element.view.*

class UserPreviewAdapter (val context: FragmentActivity?, val members: MutableList<UserPreview>) :
        RecyclerView.Adapter<UserPreviewAdapter.ViewHolder>() {
    private lateinit var auth: FirebaseAuth

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(member: UserPreview) {
            auth = Firebase.auth

            Picasso.get().load(member.urlImage).into(itemView.imageViewUserPreview)
            itemView.TextViewNameUser.text = member.name

            itemView.idFLUserPreview.setOnClickListener {
                if(auth.currentUser.uid != member.id) {
                    val activityIntent = Intent(context, ChatActivity::class.java)
                    activityIntent.putExtra("idFriend", member.id)
                    context?.startActivity(activityIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.user_element, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return members.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(members[position])
    }
}