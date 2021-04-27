package com.example.workline.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.workline.HomeActivity
import com.example.workline.R
import com.example.workline.adapters.GroupAdapter
import com.example.workline.adapters.MessageAdapter
import com.example.workline.adapters.MessageInChatAdapter
import com.example.workline.modelos.Message
import com.example.workline.modelos.MessageGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_groups.view.*
import kotlinx.android.synthetic.main.fragment_messages.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GroupsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val dbrt = FirebaseDatabase.getInstance()
    private val groupRef = dbrt.getReference("MensajeriaMuro")
    private lateinit var rootView: View

    var messages = arrayListOf<MessageGroup>(
            MessageGroup("0", "Hola", "Edson Lugo", "19/06/2021", "Edson19", "LMAD"))

    private val listLastMessageGroupChat = mutableListOf<MessageGroup>()
    private var adapter = GroupAdapter(activity, listLastMessageGroupChat)
    var carrera = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        carrera = (getActivity() as HomeActivity).getCarrera()

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, carrera, duration)
        toast.show()

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_groups, container, false)
        adapter = GroupAdapter(activity, listLastMessageGroupChat)
        rootView.listGroup.adapter = adapter
        getGroup()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = activity
        if (act != null) {
            act.title = "Grupos"
        }
    }

    private fun getGroup() {
        auth = Firebase.auth
        groupRef.child(carrera).child("lastMessageGroup").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listLastMessageGroupChat.clear()
                val groupMessage: MessageGroup = snapshot.getValue(
                        MessageGroup::class.java
                ) as MessageGroup

                listLastMessageGroupChat.add(groupMessage)
                adapter.notifyDataSetChanged()
                Log.d("success", snapshot.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}