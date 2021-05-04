package com.example.workline.adapters

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_element.view.*

class UserPreviewAddAdapter (val context: FragmentActivity?, val members: MutableList<UserPreview>, val idGroupId: String, val idSubGroupId: String) :
        RecyclerView.Adapter<UserPreviewAddAdapter.ViewHolder>() {
    private lateinit var auth: FirebaseAuth

    private val dbrt = FirebaseDatabase.getInstance()
    private val subGroupsRef = dbrt.getReference("SubGrupos")
    private val subGroupsMembersRef = dbrt.getReference("UsuariosSubGroup")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(member: UserPreview) {
            auth = Firebase.auth

            Picasso.get().load(member.urlImage).into(itemView.imageViewUserPreview)
            itemView.TextViewNameUser.text = member.name

            itemView.idFLUserPreview.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("¿Quieres añadir a " + member.name + " al grupo?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            addInGroup(member)
                        }
                        .setNegativeButton("No") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
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

    private fun addInGroup(user: UserPreview) {
        subGroupsRef.child(idGroupId).child(idSubGroupId).child("Miembros").child(user.id).setValue(user).addOnSuccessListener {
            subGroupsRef.child(idGroupId).child(idSubGroupId).get().addOnCompleteListener {
                val subGroup = SubGroup(it.result?.key.toString(), it.result?.child("name")?.getValue().toString(), it.result?.child("imageUrl")?.getValue().toString(), it.result?.child("groupId")?.getValue().toString())
                AddUserMemberSubGroup(user, subGroup)
            }
        }
    }
    private fun AddUserMemberSubGroup(user: UserPreview, subGroup: SubGroup) {
        subGroupsMembersRef.child(user.id).child("SubGroups").child(subGroup.id).setValue(subGroup)
    }

}