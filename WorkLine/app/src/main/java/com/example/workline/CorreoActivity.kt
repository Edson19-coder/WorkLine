package com.example.workline

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.workline.modelos.UserPreview
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_correo.*

class CorreoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val refSubGroup = dbrt.getReference("SubGrupos")

    private var idSubGroup: String = ""
    private var idGroup: String = ""

    private val listMembers = mutableListOf<UserPreview>()
    private val listCorreos = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correo)

        val bundle = intent.extras
        idSubGroup = bundle?.getString("idSubGroup").toString()
        idGroup = bundle?.getString("idGroup").toString()

        imageButtonCorreo.setOnClickListener { 
            finish()
        }

        btnSendEmail.setOnClickListener {
            var asunto = asuntoEmail.text.toString()
            var cuerpo = cuerpoEmail.text.toString()
            if(asunto.isNotEmpty() && cuerpo.isNotEmpty()) {
                sendEmails(asunto, cuerpo)
            }
        }

        getEmails()
    }

    private fun getEmails() {
        refSubGroup.child(idGroup).child(idSubGroup).child("Miembros").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listMembers.clear()
                for (snap in snapshot.children) {
                    val member = UserPreview(snap.child("id").getValue().toString(), snap.child("name").getValue().toString(), snap.child("urlImage").getValue().toString())
                    listMembers.add(member)
                }
                if(listMembers.size > 0) {
                    for(member in listMembers) {
                        db.collection("users").document(member.id).get().addOnSuccessListener {
                            listCorreos.add(it.get("email").toString())

                            if(listCorreos.size > 0) {
                                    var emailsTo = emailTo.text.toString()
                                    if(emailsTo != "") {
                                        emailTo.setText(emailsTo + ", " + it.get("email").toString())
                                    } else {
                                        emailTo.setText(it.get("email").toString())
                                    }
                            }
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }

    private fun sendEmails(asunto: String, cuerpo: String) {
        val intentCorreo = Intent(Intent.ACTION_SENDTO)
        intentCorreo.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailTo.text.toString()))
        intentCorreo.putExtra(Intent.EXTRA_SUBJECT, asunto)
        intentCorreo.putExtra(Intent.EXTRA_TEXT, cuerpo)
        intentCorreo.data = Uri.parse("mailto:")

        if(intentCorreo.resolveActivity(packageManager) != null) {
            startActivity(intentCorreo)
        }

    }
}