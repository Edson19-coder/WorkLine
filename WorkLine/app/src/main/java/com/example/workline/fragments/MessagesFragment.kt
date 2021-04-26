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
import com.example.workline.modelos.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_messages.*

/**
 * A simple [Fragment] subclass.
 * Use the [MessagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessagesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val dbrt = FirebaseDatabase.getInstance()
    private val chatsRef = dbrt.getReference("PrivateChat")
    private val pathChild = arrayListOf<String>("email-user-1", "email-user-2")

    var messages = arrayListOf<Message>(
        Message("0", "Hola", "Edson Lugo", "19/06/2021"),
        Message("1", "Hola", "Edson Lugo", "19/06/2021"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = activity
        if (act != null) {
            act.title = "Mensajes"
        }
        listMessage.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = MessageAdapter(act,messages)
        }
    }
}