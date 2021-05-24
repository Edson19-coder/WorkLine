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
import com.example.workline.modelos.Task
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_task_in_sub_groups.view.*

class TaskInSubGroupsFragment : Fragment() {

    private var idSubGroup = ""
    private var idGroup = ""
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_task_in_sub_groups, container, false)
        idSubGroup = (getActivity() as InSubGroupActivity).getSubGroup()
        idGroup = (getActivity() as InSubGroupActivity).getGroup()

        rootView.floatingActionButtonCreateTask.setOnClickListener {
            val  activityIntent =  Intent(context, CreateTaskActivity::class.java)
            activityIntent.putExtra("idGroup", idGroup)
            activityIntent.putExtra("idSubGroup", idSubGroup)
            context?.startActivity(activityIntent)
        }

        return rootView
    }

}