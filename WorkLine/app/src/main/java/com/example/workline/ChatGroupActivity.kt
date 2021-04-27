package com.example.workline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.workline.adapters.MessageGroupInChatAdapter
import com.example.workline.adapters.MessageInChatAdapter
import com.example.workline.modelos.Message
import com.example.workline.modelos.MessageGroup
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat_group.*
import java.text.SimpleDateFormat
import java.util.*

class ChatGroupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val mensajeriaGroupRef = dbrt.getReference("MensajeriaMuro")
    private val listMessage = mutableListOf<MessageGroup>()
    private val adapter = MessageGroupInChatAdapter(listMessage)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_group)

        btnSendMessageGroup.setOnClickListener {
            val messageText = editTextMessageGroup.text.toString()
            createMessage(messageText)
            editTextMessageGroup.text.clear()
        }

        rvChatGroup.adapter = adapter
        getMessage()
    }

    private fun getMessage() {
        auth = Firebase.auth
        mensajeriaGroupRef.child("LMAD").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listMessage.clear()
                for(snap in snapshot.children) {
                    if(snap.key.toString() != "lastMessageGroup") {
                        val groupMessage: MessageGroup = snap.getValue(
                            MessageGroup::class.java
                        ) as MessageGroup
                        groupMessage.mine = groupMessage.emitter.equals(auth.currentUser.uid)
                        listMessage.add(groupMessage)
                        Log.d("success", snapshot.toString())
                    }
                }

                if(listMessage.size > 0 && listMessage != null) {
                    adapter.notifyDataSetChanged()
                    rvChatGroup.smoothScrollToPosition(listMessage.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun createMessage(textMessage: String) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        db.collection("users").document(auth.currentUser.uid).get().addOnSuccessListener {
            val user = User(it.get("userName").toString(), it.get("email").toString(), it.get("name").toString(), it.get("lastName").toString(), it.get("carrera").toString())

            val message = MessageGroup(mensajeriaGroupRef.push().key.toString(), textMessage, auth.currentUser.uid, currentDate, user.nombre + " " + user.lastName, user.carrera)
            mensajeriaGroupRef.child(message.nameGroup).child(message.id).setValue(message)
            insertLastMessage(message, user.carrera)
        }
    }

    private fun insertLastMessage(lastMessage: MessageGroup, group: String) {
        mensajeriaGroupRef.child(group).child("lastMessageGroup").setValue(lastMessage)
    }
}