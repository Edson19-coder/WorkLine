package com.example.workline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.workline.adapters.UserPreviewAdapter
import com.example.workline.adapters.UserPreviewAddAdapter
import com.example.workline.modelos.UserPreview
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_agregar_sub_group.*

class AgregarSubGroupActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val refSubGroup = dbrt.getReference("SubGrupos")
    private val listMembers = mutableListOf<UserPreview>()
    private val listNoMembers = mutableListOf<UserPreview>()
    private var adapter = UserPreviewAddAdapter(this, listNoMembers,  "", "")

    private var idSubGroup: String = ""
    private var idGroup: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_sub_group)

        imageButtonAgregarSubGroup.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        idSubGroup = bundle?.getString("idSubGroup").toString()
        idGroup = bundle?.getString("idGroup").toString()

        adapter = UserPreviewAddAdapter(this, listNoMembers,  idGroup, idSubGroup)

        rvAgregarUsuario.adapter = adapter

        getUserMembers()
        getUsersNoMembers()
    }

    private fun getUsersNoMembers() {
        db.collection("users").whereEqualTo("carrera", idGroup).get().addOnSuccessListener {
            listNoMembers.clear()
            for(datos in it) {
                var isMember = false;
                for(member in listMembers) {
                    if(datos.id != member.id) {
                        isMember = false
                    } else {
                        isMember = true
                        break
                    }
                }

                if(isMember == false) {
                    val nameUser = datos["name"].toString() + " " + datos["lastName"].toString()
                    val usuario = UserPreview(datos.id, nameUser, datos["image"].toString())
                    listNoMembers.add(usuario)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun getUserMembers() {
        refSubGroup.child(idGroup).child(idSubGroup).child("Miembros").addValueEventListener(object :
                ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listMembers.clear()
                for (snap in snapshot.children) {
                    val member = UserPreview(snap.child("id").getValue().toString(), snap.child("name").getValue().toString(), snap.child("urlImage").getValue().toString())
                    listMembers.add(member)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}