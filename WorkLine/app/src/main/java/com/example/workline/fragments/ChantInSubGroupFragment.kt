package com.example.workline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workline.InSubGroupActivity
import com.example.workline.R
import com.example.workline.adapters.MessageGroupInChatAdapter
import com.example.workline.adapters.MessageSubGroupInChatAdapter
import com.example.workline.modelos.MessageGroup
import com.example.workline.modelos.MessageSubGroup
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chant_in_sub_group.*
import kotlinx.android.synthetic.main.fragment_chant_in_sub_group.view.*
import kotlinx.android.synthetic.main.fragment_muro.*
import kotlinx.android.synthetic.main.fragment_muro.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChantInSubGroupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val refSubGroup = dbrt.getReference("SubGrupos")
    private val listMessage = mutableListOf<MessageSubGroup>()
    private var adapter = MessageSubGroupInChatAdapter(listMessage)

    private var idSubGroup = ""
    private var idGroup = ""
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_chant_in_sub_group, container, false)
        idSubGroup = (getActivity() as InSubGroupActivity).getSubGroup()
        idGroup = (getActivity() as InSubGroupActivity).getGroup()
        adapter = MessageSubGroupInChatAdapter(listMessage)
        rootView.rvChatSubGroup.adapter = adapter

        rootView.btnSendMessageSubGroup.setOnClickListener {
            val textMessage = editTextMessageSubGroup.text.toString()
            if(textMessage.isNotEmpty()) {
                insertMessage(textMessage)
            }
            editTextMessageSubGroup.text.clear()
        }
        
        getMessage()
        return rootView
    }
    
    private fun insertMessage(textMessage: String) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        auth = Firebase.auth
        db.collection("users").document(auth.currentUser.uid).get().addOnSuccessListener {
            val user = User(
                it.get("userName").toString(),
                it.get("email").toString(),
                it.get("name").toString(),
                it.get("lastName").toString(),
                it.get("carrera").toString(),
                it.get("image").toString()
            )
            val message = MessageSubGroup(refSubGroup.push().key.toString(), textMessage, it.id, currentDate, user.nombre + " " + user.lastName)
            refSubGroup.child(idGroup).child(idSubGroup).child("Mensajes").child(message.id).setValue(message)
        }
    }

    private fun getMessage() {
        auth = Firebase.auth
        refSubGroup.child(idGroup).child(idSubGroup).child("Mensajes").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listMessage.clear()
                if(snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val subGroupMessage: MessageSubGroup = snap.getValue(
                            MessageSubGroup::class.java
                        ) as MessageSubGroup
                        subGroupMessage.mine = subGroupMessage.emitter.equals(auth.currentUser.uid)
                        listMessage.add(subGroupMessage)
                    }
                }
                if(listMessage.size > 0) {
                    adapter.notifyDataSetChanged()
                    rootView.rvChatSubGroup.smoothScrollToPosition(listMessage.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}