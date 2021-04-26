package com.example.workline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegisterClose.setOnClickListener {
            finish()
        }

        setup()
    }

    private fun setup() {
        title = "Registro"

        btnRegisterRegistrar.setOnClickListener {
            if(editTextRegisterEmail.text.isNotEmpty() && editTextRegisterUsuario.text.isNotEmpty() &&
                editTextRegisterPassword.text.isNotEmpty()) {
                    auth = Firebase.auth
                    auth.createUserWithEmailAndPassword(editTextRegisterEmail.text.toString(), editTextRegisterPassword.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful) {
                                db.collection("users").document(auth.currentUser.uid).set(
                                    hashMapOf(
                                        "userName" to editTextRegisterUsuario.text.toString(),
                                        "name" to null,
                                        "lastName" to null,
                                        "email" to editTextRegisterEmail.text.toString()
                                    )
                                ).addOnCompleteListener {
                                    if(task.isSuccessful) {
                                        val user = User(editTextRegisterUsuario.text.toString(), editTextRegisterEmail.text.toString(), "", "")
                                        Log.d("Success", "Usuario registrado correctamente")
                                        showHome(user)
                                    } else {
                                        Log.e("Error", "Error al crear el usuario")
                                    }
                                }
                            } else {
                                Log.e("Error", "Error al crear auth del usuario")
                            }
                        }
            }
        }
    }

    private fun showHome(user:User) {
        val activityHome = Intent(this, HomeActivity::class.java)
        startActivity(activityHome)
    }
}