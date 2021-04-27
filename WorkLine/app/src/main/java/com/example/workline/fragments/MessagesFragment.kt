package com.example.workline.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workline.ChatActivity
import com.example.workline.HomeActivity
import com.example.workline.R
import com.example.workline.adapters.MessageAdapter
import com.example.workline.adapters.MessageInChatAdapter
import com.example.workline.modelos.Message
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.android.synthetic.main.fragment_messages.view.*
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 * Use the [MessagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessagesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val chatsRef = dbrt.getReference("Mensajeria")
    private lateinit var rootView: View

    var messages = arrayListOf<Message>(
            Message("0", "Hola", "Edson Lugo", "19/06/2021"),
            Message("1", "Hola", "Edson Lugo", "19/06/2021"))

    private val listLastMessageChat = mutableListOf<Message>()
    private var adapter = MessageAdapter(activity, listLastMessageChat)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_messages, container, false)
        adapter = MessageAdapter(activity, listLastMessageChat)
        rootView.rvMessage.adapter = adapter
        getChats()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = activity
        if (act != null) {
            act.title = "Mensajes"
        }
    }

    private fun getChats() {
        auth = Firebase.auth
        chatsRef.child(auth.currentUser.uid).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listLastMessageChat.clear()

                for(snap in snapshot.children) {

                    chatsRef.child(auth.currentUser.uid).child(snap.key.toString()).child("lastMessage").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val message: Message = snapshot.getValue(
                                    Message::class.java
                            ) as Message
                            listLastMessageChat.add(message)
                            Log.d("Success", "Listo last message")

                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}