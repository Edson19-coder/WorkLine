package com.example.workline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workline.InGroupActivity
import com.example.workline.R
import com.example.workline.adapters.GroupAdapter
import com.example.workline.adapters.SubGroupAdapter
import com.example.workline.modelos.MessageGroup
import com.example.workline.modelos.SubGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_groups.view.*
import kotlinx.android.synthetic.main.fragment_sub_group.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [SubGroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubGroupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val dbrt = FirebaseDatabase.getInstance()
    private val subGroupsRef = dbrt.getReference("SubGrupos")
    private val subGroupsByUserRef = dbrt.getReference("UsuariosSubGroup")

    private val listSubGroups = mutableListOf<SubGroup>()
    private var adapter = SubGroupAdapter(activity, listSubGroups)

    private var carrera = ""
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sub_group, container, false)
        carrera = (getActivity() as InGroupActivity).getCarrera()
        adapter = SubGroupAdapter(activity, listSubGroups)
        rootView.rvSubGroups.adapter = adapter
        getSubGroupsByUser()
        return rootView
    }

    private fun getSubGroupsByUser() {
        auth = Firebase.auth
        subGroupsByUserRef.child(auth.currentUser.uid).child("SubGroups").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listSubGroups.clear()
                for(snap in snapshot.children) {
                    val subGroup: SubGroup = snap.getValue(
                            SubGroup::class.java
                    ) as SubGroup
                    listSubGroups.add(subGroup)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }
}