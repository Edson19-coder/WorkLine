package com.example.workline.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.workline.ChatActivity
import com.example.workline.InSubGroupActivity
import com.example.workline.R
import com.example.workline.modelos.SubGroup
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_element.view.*

class SubGroupAdapter (val context: FragmentActivity?, val subGroups: MutableList<SubGroup>) :
        RecyclerView.Adapter<SubGroupAdapter.ViewHolder>() {
    private lateinit var auth: FirebaseAuth

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(subGroup: SubGroup) {
            Picasso.get().load(subGroup.imageUrl).into(itemView.imageView5)
            itemView.TextViewTitleSubGroup.text = subGroup.name
            itemView.FrameLayoutSubGroup.setOnClickListener {
                val  activityIntent =  Intent(context, InSubGroupActivity::class.java)
                activityIntent.putExtra("idSubGroup", subGroup.id)
                activityIntent.putExtra("nameGroup", subGroup.name)
                activityIntent.putExtra("groupId", subGroup.groupId)
                context?.startActivity(activityIntent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.group_element, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return subGroups.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(subGroups[position])
    }
}