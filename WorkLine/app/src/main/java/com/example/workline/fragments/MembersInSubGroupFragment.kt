package com.example.workline.fragments

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workline.*
import com.example.workline.adapters.UserPreviewAdapter
import com.example.workline.modelos.MessageSubGroup
import com.example.workline.modelos.User
import com.example.workline.modelos.UserPreview
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_members.view.*
import kotlinx.android.synthetic.main.fragment_members.view.rvMembersGroup
import kotlinx.android.synthetic.main.fragment_members_in_sub_group.*
import kotlinx.android.synthetic.main.fragment_members_in_sub_group.view.*

class MembersInSubGroupFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val refSubGroup = dbrt.getReference("SubGrupos")
    private val listMembers = mutableListOf<UserPreview>()
    private var adapter = UserPreviewAdapter(activity, listMembers)

    private var idSubGroup = ""
    private var idGroup = ""
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_members_in_sub_group, container, false)
        idSubGroup = (getActivity() as InSubGroupActivity).getSubGroup()
        idGroup = (getActivity() as InSubGroupActivity).getGroup()
        adapter = UserPreviewAdapter(activity, listMembers)
        rootView.rvMembersSubGroup.adapter = adapter

        rootView.btnAgregarUsuario.setOnClickListener {
            val  activityIntent =  Intent(context, AgregarSubGroupActivity::class.java)
            activityIntent.putExtra("idSubGroup", idSubGroup)
            activityIntent.putExtra("idGroup", idGroup)
            context?.startActivity(activityIntent)
        }

        rootView.btnEnviarEmailSubGroup.setOnClickListener {
            val  activityIntent =  Intent(context, CorreoActivity::class.java)
            activityIntent.putExtra("idSubGroup", idSubGroup)
            activityIntent.putExtra("idGroup", idGroup)
            context?.startActivity(activityIntent)
        }

        getMembers()
        return rootView
    }

    private fun getMembers() {
        refSubGroup.child(idGroup).child(idSubGroup).child("Miembros").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listMembers.clear()
                for (snap in snapshot.children) {
                    val member = UserPreview(snap.child("id").getValue().toString(), snap.child("name").getValue().toString(), snap.child("urlImage").getValue().toString())
                    listMembers.add(member)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }

}