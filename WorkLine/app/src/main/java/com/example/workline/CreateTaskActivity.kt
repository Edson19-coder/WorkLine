package com.example.workline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.workline.modelos.Task
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_task.*

class CreateTaskActivity : AppCompatActivity() {

    private val dbrt = FirebaseDatabase.getInstance()
    private val refSubGroup = dbrt.getReference("SubGrupos")

    private var idSubGroup = ""
    private var idGroup = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        val bundle = intent.extras
        idGroup = bundle?.getString("idGroup").toString()
        idSubGroup = bundle?.getString("idSubGroup").toString()

        imageButtonCreateTask.setOnClickListener {
            finish()
        }

        btnCreateTask.setOnClickListener {
            val titleTask = editTextTextTitleTask.text.toString()
            val descriptionTask = editTextTextDescriptionTask.text.toString()
            if(title.isNotEmpty() && descriptionTask.isNotEmpty()) {
                createTaskSubGroup(titleTask, descriptionTask)
                finish()
            }
        }
    }

    private fun createTaskSubGroup(title: String, description: String) {
        val idTask = refSubGroup.push().key.toString()
        refSubGroup.child(idGroup).child(idSubGroup).get().addOnCompleteListener {
            val task = Task(idTask, title, description, idGroup, idSubGroup, it.result?.child("imageUrl")?.getValue().toString())
            refSubGroup.child(idGroup).child(idSubGroup).child("Tareas").child(idTask).setValue(task)
        }
    }
}