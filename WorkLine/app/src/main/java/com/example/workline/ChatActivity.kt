package com.example.workline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import  com.example.workline.modelos.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val dbrt = FirebaseDatabase.getInstance()
    private val mensajeriaRef = dbrt.getReference("Mensajeria")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = Firebase.auth
        var myUserId = auth.currentUser.uid
        var friendUserId = "gd606cbMxWRP7Sxywbeb8S8sQfw2"

        getMessages(myUserId, friendUserId)

        btnSendMessage.setOnClickListener {
            val textMessage = editTextMessage.text.toString()

            if(myUserId.toString().isNotEmpty() && friendUserId.toString().isNotEmpty() && textMessage.isNotEmpty()) {
                //Insertamos el mensaje en el usuario emisor
                insertMessage(myUserId.toString(), friendUserId, textMessage)
                //Insertamos el mensaje en el usuario remitente
                insertMessage(friendUserId, myUserId.toString(), textMessage)
            }
            editTextMessage.text.clear()
        }
    }

    private fun insertMessage(emitter: String, remmiter: String, textMessage: String) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val created_at = sdf.format(Date())
        val message = Message(mensajeriaRef.push().key.toString(), textMessage, emitter.toString(), created_at)
        mensajeriaRef.child(emitter).child(remmiter).child(message.id).setValue(message)
    }

    private fun getMessages(userId: String ,friendId: String) {
        mensajeriaRef.child(userId).child(friendId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children) {
                    val message = snap
                    Log.d("Success", message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}