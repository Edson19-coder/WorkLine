package com.example.workline.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workline.InGroupActivity
import com.example.workline.R
import com.example.workline.adapters.MessageAdapter
import com.example.workline.adapters.MessageGroupInChatAdapter
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
import kotlinx.android.synthetic.main.activity_chat_group.*
import kotlinx.android.synthetic.main.fragment_messages.view.*
import kotlinx.android.synthetic.main.fragment_muro.*
import kotlinx.android.synthetic.main.fragment_muro.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [MuroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MuroFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val mensajeriaGroupRef = dbrt.getReference("MensajeriaMuro")
    private val listMessage = mutableListOf<MessageGroup>()
    private var adapter = MessageGroupInChatAdapter(listMessage)
    private var carrera = ""
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_muro, container, false)
        carrera = (getActivity() as InGroupActivity).getCarrera()
        adapter = MessageGroupInChatAdapter(listMessage)
        rootView.rvChatForo.adapter = adapter

        rootView.btnSendMessageMuroGroup.setOnClickListener {
            val messageText = editTextMuroMessageGroup.text.toString()
            createMessage(messageText)
            editTextMuroMessageGroup.text.clear()
        }

        getMessage()
        return rootView
    }

    private fun createMessage(textMessage: String) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        db.collection("users").document(auth.currentUser.uid).get().addOnSuccessListener {
            val user = User(it.get("userName").toString(), it.get("email").toString(), it.get("name").toString(), it.get("lastName").toString(), it.get("carrera").toString(), it.get("image").toString())

            val message = MessageGroup(mensajeriaGroupRef.push().key.toString(), textMessage, auth.currentUser.uid, currentDate, user.nombre + " " + user.lastName, user.carrera)
            mensajeriaGroupRef.child(message.nameGroup).child(message.id).setValue(message)
            insertLastMessage(message, user.carrera)
        }
    }

    private fun insertLastMessage(lastMessage: MessageGroup, group: String) {
        when(carrera) {
            "LMAD" -> lastMessage.imageGroup = "https://firebasestorage.googleapis.com/v0/b/proyecto-poi.appspot.com/o/images%2Flmad.png?alt=media&token=b921494d-6231-47cc-a062-7ea49271ef4b"
            "LCC" -> lastMessage.imageGroup = "https://firebasestorage.googleapis.com/v0/b/proyecto-poi.appspot.com/o/images%2Flcc.png?alt=media&token=2ebbef48-e5d7-412d-b372-e6580deefb19"
            "LCTI" -> lastMessage.imageGroup = "https://firebasestorage.googleapis.com/v0/b/proyecto-poi.appspot.com/o/images%2Flcti.png?alt=media&token=f981be1f-793f-4a96-8aed-6763653d9349"
            "LF" -> lastMessage.imageGroup = "https://firebasestorage.googleapis.com/v0/b/proyecto-poi.appspot.com/o/images%2Ffisico.png?alt=media&token=f61e145f-6cf8-46a7-bd2f-36d13eef2a4c"
            "LM" -> lastMessage.imageGroup = "https://firebasestorage.googleapis.com/v0/b/proyecto-poi.appspot.com/o/images%2Fmatematicas.jpg?alt=media&token=e03fda3d-3a1f-485a-b26d-089b288e484e"
            "Actuaria" -> lastMessage.imageGroup = "https://firebasestorage.googleapis.com/v0/b/proyecto-poi.appspot.com/o/images%2Factuaria.jpg?alt=media&token=51e51e25-9029-4642-8a23-2517e21cb4bc"
        }

        mensajeriaGroupRef.child(group).child("lastMessageGroup").setValue(lastMessage)
    }

    private fun getMessage() {
        auth = Firebase.auth
        mensajeriaGroupRef.child(carrera).addValueEventListener(object : ValueEventListener {
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
                    rootView.rvChatForo.smoothScrollToPosition(listMessage.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}