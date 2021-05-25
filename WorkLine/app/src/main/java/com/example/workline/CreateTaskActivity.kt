package com.example.workline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.workline.modelos.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create_task.*

class CreateTaskActivity : AppCompatActivity() {

    private val dbrt = FirebaseDatabase.getInstance()
    private val refSubGroup = dbrt.getReference("SubGrupos")
    private val refTaskUsers = dbrt.getReference("TareasUsuarios")

    private var idSubGroup = ""
    private var nameSubGroup = ""
    private var idGroup = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        val bundle = intent.extras
        idGroup = bundle?.getString("idGroup").toString()
        idSubGroup = bundle?.getString("idSubGroup").toString()
        nameSubGroup = bundle?.getString("nameSubGroup").toString()

        imageButtonCreateTask.setOnClickListener {
            finish()
        }

        btnCreateTask.setOnClickListener {
            val titleTask = editTextTextTitleTask.text.toString()
            val descriptionTask = editTextTextDescriptionTask.text.toString()
            val puntosTask = editTextPuntos.text.toString().toInt()
            if(title.isNotEmpty() && descriptionTask.isNotEmpty() && puntosTask != null) {
                createTaskSubGroup(titleTask, descriptionTask, puntosTask)
                finish()
            }
        }
    }

    private fun createTaskSubGroup(title: String, description: String, points: Int) {
        val idTask = refSubGroup.push().key.toString()
        refSubGroup.child(idGroup).child(idSubGroup).get().addOnCompleteListener {
            val task = Task(idTask, title, description, idGroup, idSubGroup, nameSubGroup, it.result?.child("imageUrl")?.getValue().toString(), points)
            refSubGroup.child(idGroup).child(idSubGroup).child("Tareas").child(idTask).setValue(task)
            insertTaskUsers(idTask)
        }
    }

    private fun insertTaskUsers(idTask: String) {
        refSubGroup.child(idGroup).child(idSubGroup).child("Miembros").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    refTaskUsers.child(snap.child("id").getValue().toString()).child(idSubGroup).child("PuntosTotales").setValue(0)
                    refTaskUsers.child(snap.child("id").getValue().toString()).child(idSubGroup).child(idTask).child("Entregado").setValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        });
    }
}