package com.example.workline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workline.InGroupActivity
import com.example.workline.R
import com.example.workline.adapters.MessageAdapter
import com.example.workline.adapters.MessageGroupInChatAdapter
import com.example.workline.adapters.UserPreviewAdapter
import com.example.workline.modelos.MessageGroup
import com.example.workline.modelos.SubGroup
import com.example.workline.modelos.UserPreview
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_members.view.*

class MembersFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var rootView: View

    private val listMembers = mutableListOf<UserPreview>()
    private var adapter = UserPreviewAdapter(activity, listMembers)
    private var carrera = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_members, container, false)
        carrera = (getActivity() as InGroupActivity).getCarrera()
        adapter = UserPreviewAdapter(activity, listMembers)
        rootView.rvMembersGroup.adapter = adapter
        getUsersMembers()
        return rootView
    }

    private fun getUsersMembers() {
        db.collection("users").whereEqualTo("carrera", carrera).get().addOnSuccessListener {
            listMembers.clear()
            for(datos in it) {
                val nameUser = datos["name"].toString() + " " + datos["lastName"].toString()
                val usuario = UserPreview(datos.id, nameUser, datos["image"].toString())
                listMembers.add(usuario)
            }
            adapter.notifyDataSetChanged()
        }
    }

}