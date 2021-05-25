package com.example.workline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workline.HomeActivity
import com.example.workline.R
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

class TasksFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val dbrt = FirebaseDatabase.getInstance()
    private val subGroupsRef = dbrt.getReference("SubGrupos")
    private val subGroupsByUserRef = dbrt.getReference("UsuariosSubGroup")

    var carrera: String = ""

    private val listSubGrupos = mutableListOf<String>()

    private val listTask = mutableListOf<Task>()
    private var adapter = TaskAdapter(activity, listTask)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_tasks, container, false)

        carrera = (getActivity() as HomeActivity).getCarrera()

        getSubGrupos()
        //getTasks()

        return rootView
    }

    private fun getSubGrupos() {
        auth = Firebase.auth
        subGroupsByUserRef.child(auth.currentUser.uid).child("SubGroups").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listSubGrupos.clear()
                for(snap in snapshot.children) {
                    val subGroup: SubGroup = snap.getValue(
                        SubGroup::class.java
                    ) as SubGroup
                    listSubGrupos.add(subGroup.groupId)

                    subGroupsRef.child(carrera).child(subGroup.groupId).child("Tareas").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            listTask.clear()
                            for(snap in snapshot.children) {
                                val task: Task = snap.getValue(
                                    Task::class.java
                                ) as Task
                                listTask.add(task)
                            }
                            //adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            println(error)
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }

    private fun getTasks() {
        for (subGrupo in listSubGrupos) {
            subGroupsRef.child(carrera).child(subGrupo).child("Tareas").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listTask.clear()
                    for(snap in snapshot.children) {
                        val task: Task = snap.getValue(
                            Task::class.java
                        ) as Task
                        listTask.add(task)
                    }
                    //adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error)
                }

            })
        }
    }
}