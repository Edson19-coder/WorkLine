package com.example.workline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_task.*

class TaskActivity : AppCompatActivity() {

    private var idSubGroup: String = ""
    private var idGroup: String = ""
    private var idTask: String = ""
    private var pointsTask: Int = 0

    private val dbrt = FirebaseDatabase.getInstance()
    private val subGroupsRef = dbrt.getReference("SubGrupos")
    private val refTaskUsers = dbrt.getReference("TareasUsuarios")
    private lateinit var auth: FirebaseAuth

    private var isSent: Boolean = false
    private var totalPoints: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        val bundle = intent.extras
        idSubGroup = bundle?.getString("idSubGroup").toString()
        idGroup = bundle?.getString("idGroup").toString()
        idTask = bundle?.getString("idTask").toString()

        auth = Firebase.auth

        btnEntregar.setOnClickListener {
            if(isSent == false) {
                sendTask()
            } else {
                println("Ya la enviaste perro")
            }
        }

        getTask()
        validTaskSend()
        getTotalPoinst()
    }

    private fun getTask() {
        subGroupsRef.child(idGroup).child(idSubGroup).child("Tareas").child(idTask).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                textViewSubGroupName.text = snapshot.child("groupName").value.toString()
                textViewTitleTask.text = snapshot.child("title").value.toString()
                textViewDescription.text = snapshot.child("description").value.toString()
                textViewPoints.text = "Puntos de la tarea: " + snapshot.child("points").value.toString()
                pointsTask = snapshot.child("points").value.toString().toInt()

            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }

    private fun sendTask() {
        refTaskUsers.child(auth.currentUser.uid).child(idSubGroup).child(idTask).child("Entregado").setValue(true)
        refTaskUsers.child(auth.currentUser.uid).child(idSubGroup).child("PuntosTotales").setValue(totalPoints + pointsTask)
    }

    private fun getTotalPoinst() {
        refTaskUsers.child(auth.currentUser.uid).child(idSubGroup).child("PuntosTotales").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot != null) {
                    totalPoints = snapshot.value.toString().toInt()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }

    private fun validTaskSend() {
        refTaskUsers.child(auth.currentUser.uid).child(idSubGroup).child(idTask).child("Entregado").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isSent = snapshot.value as Boolean
                if(isSent == true) {
                    textViewMessageSent.visibility = View.VISIBLE
                    btnEntregar.visibility = View.INVISIBLE
                } else {
                    textViewMessageSent.visibility = View.INVISIBLE
                    btnEntregar.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }
}