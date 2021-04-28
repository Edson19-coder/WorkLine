package com.example.workline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    val carreras = arrayOf("Actuaria","LCC","LCTI","LF","LM","LMAD")
    var carrera: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegisterClose.setOnClickListener {
            finish()
        }

        spinnerRegisterCarrera.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,carreras)

        spinnerRegisterCarrera.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                carrera = carreras.get(0)
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                carrera = carreras.get(p2)
            }

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
                                        "name" to editTextRegisterName.text.toString(),
                                        "lastName" to editTextRegisterApellidos.text.toString(),
                                        "email" to editTextRegisterEmail.text.toString(),
                                        "carrera" to carrera
                                    )
                                ).addOnCompleteListener {
                                    if(task.isSuccessful) {
                                        val user = User(editTextRegisterUsuario.text.toString(), editTextRegisterEmail.text.toString(), editTextRegisterName.text.toString(), editTextRegisterApellidos.text.toString(), carrera)
                                        Log.d("Success", "Usuario registrado correctamente")
                                        showHome(user)
                                    } else {
                                        Log.e("Error", "Error al crear el usuario")
                                    }
                                }
                            } else {

                                Log.e("Error", "Error al crear auth del usuario")
                                Log.e("Error", task.exception.toString())
                            }
                        }
            }
        }
    }

    private fun showHome(user:User) {
        val activityHome = Intent(this, HomeActivity::class.java).apply {
            putExtra("carrera", user.carrera)
        }
        startActivity(activityHome)
    }
}