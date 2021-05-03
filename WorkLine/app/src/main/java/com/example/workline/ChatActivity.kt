package com.example.workline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import com.example.workline.adapters.MessageAdapter
import com.example.workline.adapters.MessageInChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import  com.example.workline.modelos.Message
import com.example.workline.modelos.User
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val mensajeriaRef = dbrt.getReference("Mensajeria")
    private val listMessage = mutableListOf<Message>()
    private val adapter = MessageInChatAdapter(listMessage)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = Firebase.auth

        val bundle = intent.extras
        val idFriend = bundle?.getString("idFriend").toString()

        var myUserId = auth.currentUser.uid
        var friendUserId = idFriend
        //var friendUserId = "jh9GYTj4lcex7maGRtUY9kQ9I203" //David
        //var friendUserId = "irNUmW0uZTS0K68oYSYtNSwWPFo2" //Osmar
        //var friendUserId = "xNlc1h99vGNMLIsSapFLOjMB8OD2"   //Edson

        btnSendMessage.setOnClickListener {
            val textMessage = editTextMessage.text.toString()

            if(myUserId.toString().isNotEmpty() && friendUserId.toString().isNotEmpty() && textMessage.isNotEmpty()) {
                db.collection("users").document(myUserId).get().addOnSuccessListener {
                    val emmiterName = it.get("name").toString() + " " + it.get("lastName").toString()
                    //Insertamos el mensaje en el usuario emisor
                    insertMessage(myUserId.toString(), friendUserId, textMessage, myUserId.toString(), emmiterName)
                    //Insertamos el mensaje en el usuario remitente
                    insertMessage(friendUserId, myUserId.toString(), textMessage, myUserId.toString(), emmiterName)
                }
            }
            editTextMessage.text.clear()
        }
        rvChat.adapter = adapter
        getMessages(myUserId, friendUserId)
    }

    private fun insertMessage(me: String, friend: String, textMessage: String, emitterId: String, emitterName: String) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        db.collection("users").document(friend).get().addOnSuccessListener {
            val user = User(it.get("userName").toString(), it.get("email").toString(), it.get("name").toString(), it.get("lastName").toString(), it.get("carrera").toString(), it.get("image").toString())

            val message = Message(mensajeriaRef.push().key.toString(), textMessage, emitterId.toString(), currentDate, user.nombre + " " + user.lastName, emitterName, friend, user.image)
            mensajeriaRef.child(me).child(friend).child(message.id).setValue(message)
            insertLastMessage(me, friend, message)
        }
    }

    private fun getMessages(userId: String ,friendId: String) {
        mensajeriaRef.child(userId).child(friendId).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listMessage.clear()
                for(snap in snapshot.children) {
                    if(snap.key.toString() != "lastMessage") {
                        val message: Message = snap.getValue(
                                Message::class.java
                        ) as Message
                        message.mine = message.emitter.equals(userId)
                        //message.mine = message.emitter == userId
                        listMessage.add(message)
                        Log.d("Success", message.toString())
                    }
                }
                if(listMessage.size > 0 && listMessage != null) {
                    adapter.notifyDataSetChanged()
                    rvChat.smoothScrollToPosition(listMessage.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        adapter.notifyDataSetChanged()
    }

    private fun insertLastMessage(me: String, friend: String, lastMessage: Message) {
        mensajeriaRef.child(me).child("lastMessage").child(friend).setValue(lastMessage)
    }
}