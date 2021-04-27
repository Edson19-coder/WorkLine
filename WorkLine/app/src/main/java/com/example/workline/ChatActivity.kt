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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val dbrt = FirebaseDatabase.getInstance()
    private val mensajeriaRef = dbrt.getReference("Mensajeria")
    private val listMessage = mutableListOf<Message>()
    private val adapter = MessageInChatAdapter(listMessage)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = Firebase.auth
        var myUserId = auth.currentUser.uid
        //var friendUserId = "gd606cbMxWRP7Sxywbeb8S8sQfw2"
        //var friendUserId = "ECpsTvTvloeK2lCuwcKJOzlORpm2"
        var friendUserId = "VEubfRognTaaKLQLMLEwHgu16Ks1"

        btnSendMessage.setOnClickListener {
            val textMessage = editTextMessage.text.toString()

            if(myUserId.toString().isNotEmpty() && friendUserId.toString().isNotEmpty() && textMessage.isNotEmpty()) {
                //Insertamos el mensaje en el usuario emisor
                insertMessage(myUserId.toString(), friendUserId, textMessage, myUserId.toString())
                //Insertamos el mensaje en el usuario remitente
                insertMessage(friendUserId, myUserId.toString(), textMessage, myUserId.toString())
            }
            editTextMessage.text.clear()
        }
        rvChat.adapter = adapter
        getMessages(myUserId, friendUserId)
    }

    private fun insertMessage(me: String, friend: String, textMessage: String, emitter: String) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val message = Message(mensajeriaRef.push().key.toString(), textMessage, emitter.toString(), currentDate)
        mensajeriaRef.child(me).child(friend).child(message.id).setValue(message)
        insertLastMessage(me, friend, message)
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
        mensajeriaRef.child(me).child(friend).child("lastMessage").setValue(lastMessage)
    }
}