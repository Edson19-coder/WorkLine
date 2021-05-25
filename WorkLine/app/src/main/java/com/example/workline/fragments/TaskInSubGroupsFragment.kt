package com.example.workline.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workline.CreateTaskActivity
import com.example.workline.InSubGroupActivity
import com.example.workline.R
import com.example.workline.adapters.SubGroupAdapter
import com.example.workline.adapters.TaskAdapter
import com.example.workline.modelos.SubGroup
import com.example.workline.modelos.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_task_in_sub_groups.*
import kotlinx.android.synthetic.main.fragment_task_in_sub_groups.view.*

class TaskInSubGroupsFragment : Fragment() {

    private var idSubGroup = ""
    private var idGroup = ""
    private var nameSubGroup = ""
    private lateinit var rootView: View

    private val dbrt = FirebaseDatabase.getInstance()
    private val subGroupsRef = dbrt.getReference("SubGrupos")
    private val refTaskUsers = dbrt.getReference("TareasUsuarios")

    private val listTask = mutableListOf<Task>()
    private var adapter = TaskAdapter(activity, listTask)

    private lateinit var auth: FirebaseAuth

    private var totalPoints: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_task_in_sub_groups, container, false)
        idSubGroup = (getActivity() as InSubGroupActivity).getSubGroup()
        idGroup = (getActivity() as InSubGroupActivity).getGroup()
        nameSubGroup = (getActivity() as InSubGroupActivity).getNameSubGroup()

        auth = Firebase.auth

        rootView.floatingActionButtonCreateTask.setOnClickListener {
            val  activityIntent =  Intent(context, CreateTaskActivity::class.java)
            activityIntent.putExtra("idGroup", idGroup)
            activityIntent.putExtra("idSubGroup", idSubGroup)
            activityIntent.putExtra("nameSubGroup", nameSubGroup)
            context?.startActivity(activityIntent)
        }

        adapter = TaskAdapter(activity, listTask)
        rootView.rvTaskInSubGroup.adapter = adapter

        getTasks()
        getTotalPoinst()

        return rootView
    }

    private fun getTasks() {
        subGroupsRef.child(idGroup).child(idSubGroup).child("Tareas").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listTask.clear()
                for(snap in snapshot.children) {
                    val task: Task = snap.getValue(
                        Task::class.java
                    ) as Task
                    listTask.add(task)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }

    private fun getTotalPoinst() {
        refTaskUsers.child(auth.currentUser.uid).child(idSubGroup).child("PuntosTotales").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot != null) {
                    if (snapshot.value != null) {
                        totalPoints = snapshot.value.toString().toInt()
                        if(totalPoints != null) {
                            textViewTotalPointsUser.text = "Puntos totales: " + totalPoints.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }

}